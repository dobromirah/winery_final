package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.NotificationResponseDTO;
import bg.tu.varna.si.dto.WineBatchCreateDTO;
import bg.tu.varna.si.dto.WineBatchProduceDTO;
import bg.tu.varna.si.dto.WineBatchResponseDTO;
import bg.tu.varna.si.mapper.WineBatchMapper;
import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.*;
import bg.tu.varna.si.service.CurrentUserService;
import bg.tu.varna.si.service.NotificationService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/wine-batches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WineBatchResource {

    @Inject WineBatchRepository batchRepository;
    @Inject WineBatchGrapeUsageRepository usageRepository;
    @Inject WineTypeRepository wineTypeRepository;
    @Inject WineRecipeRepository recipeRepository;
    @Inject GrapeStockMovementRepository grapeStockRepository;
    @Inject NotificationService notificationService;
    @Inject CurrentUserService currentUserService;

    @GET
    @RolesAllowed({"ADMIN", "OPERATOR", "WAREHOUSE_MANAGER"})
    public List<WineBatchResponseDTO> listAll() {
        List<WineBatch> batches = batchRepository.listAll();

        return batches.stream()
                .map(batch -> {
                    List<WineBatchGrapeUsage> usages =
                            usageRepository.list("batch.id", batch.id);
                    return WineBatchMapper.toDTO(batch, usages);
                })
                .collect(Collectors.toList());
    }

    @GET
    @RolesAllowed({"ADMIN", "OPERATOR", "WAREHOUSE_MANAGER"})
    @Path("/{id}")
    public WineBatchResponseDTO getById(@PathParam("id") Long id) {
        WineBatch batch = batchRepository.findById(id);
        if (batch == null)
            throw new NotFoundException("Batch not found");

        List<WineBatchGrapeUsage> usages =
                usageRepository.list("batch.id", id);

        return WineBatchMapper.toDTO(batch, usages);
    }

    @PUT
    @Path("/{id}/produce")
    @RolesAllowed("OPERATOR")
    @Transactional
    public WineBatchResponseDTO setProduced(@PathParam("id") Long id, WineBatchProduceDTO dto) {
        if (dto == null || dto.producedLiters == null) {
            throw new WebApplicationException("producedLiters is required", 400);
        }
        if (dto.producedLiters < 0) {
            throw new WebApplicationException("producedLiters must be >= 0", 400);
        }

        WineBatch batch = batchRepository.findById(id);
        if (batch == null) throw new NotFoundException("Batch not found");
        if (dto.producedLiters < batch.producedLiters) {
            throw new WebApplicationException(
                    "The latest production must be >= the previous one (" + batch.producedLiters + ").",
                    400
            );
        }
        if (batch.status == WineBatchStatus.CANCELLED) {
            throw new WebApplicationException("Cancelled batch cannot be produced.", 400);
        }
        if (batch.status == WineBatchStatus.BOTTLED || batch.bottledLiters > 0.0) {
            throw new WebApplicationException("Bottled batch cannot change produced liters.", 400);
        }
        if (dto.producedLiters > batch.plannedLiters) {
            throw new WebApplicationException("producedLiters cannot exceed plannedLiters", 400);
        }

        batch.producedLiters = dto.producedLiters;

        if (dto.producedLiters == 0.0) batch.status = WineBatchStatus.PLANNED;
        else if (dto.producedLiters < batch.plannedLiters) batch.status = WineBatchStatus.IN_PRODUCTION;
        else batch.status = WineBatchStatus.COMPLETED;

        List<WineBatchGrapeUsage> usages = usageRepository.list("batch.id", batch.id);
        return WineBatchMapper.toDTO(batch, usages);
    }

    @POST
    @Path("/{id}/cancel")
    @RolesAllowed("OPERATOR")
    @Transactional
    public WineBatchResponseDTO cancel(@PathParam("id") Long id) {
        WineBatch batch = batchRepository.findById(id);
        if (batch == null) throw new NotFoundException("Batch not found");

        if (batch.status == WineBatchStatus.CANCELLED) {
            throw new WebApplicationException("Batch already cancelled.", 400);
        }
        if (batch.status == WineBatchStatus.BOTTLED || batch.bottledLiters > 0.0) {
            throw new WebApplicationException("Bottled batch cannot be cancelled.", 400);
        }
        if (batch.status == WineBatchStatus.COMPLETED) {
            throw new WebApplicationException("Completed batch cannot be cancelled.", 400);
        }

        AppUser user = currentUserService.getCurrentUser();

        List<WineBatchGrapeUsage> usages = usageRepository.list("batch.id", id);

        List<NotificationResponseDTO> pushed = new ArrayList<>();

        for (WineBatchGrapeUsage u : usages) {
            GrapeStockMovement movement = new GrapeStockMovement();
            movement.variety = u.variety;
            movement.quantityKg = Math.abs(u.quantityKg);
            movement.movementType = "IN";
            movement.createdBy = user;
            grapeStockRepository.persist(movement);

            double totalKg = grapeStockRepository.getTotalKgForVariety(u.variety.id);

            List<Notification> created = notificationService.checkGrapeLevels(u.variety, totalKg);
            for (Notification n : created) {
                pushed.add(toDto(n));
            }
        }

        batch.status = WineBatchStatus.CANCELLED;
        batch.producedLiters = 0;
        batch.bottledLiters = 0;

        WineBatchResponseDTO res = WineBatchMapper.toDTO(batch, usages);
        res.notifications = pushed;
        return res;
    }

    @POST
    @RolesAllowed("OPERATOR")
    @Transactional
    public WineBatchResponseDTO create(WineBatchCreateDTO dto) {

        WineType wineType = wineTypeRepository.findById(dto.wineTypeId);
        if (wineType == null)
            throw new NotFoundException("WineType with ID " + dto.wineTypeId + " not found.");

        AppUser user = currentUserService.getCurrentUser();

        List<WineRecipe> recipe = recipeRepository.findByWineTypeId(dto.wineTypeId);
        if (recipe.isEmpty()) {
            throw new WebApplicationException("This wine type has no recipe defined.", 400);
        }

        WineBatch batch = WineBatchMapper.fromCreateDTO(dto, wineType, user);
        batchRepository.persist(batch);

        List<NotificationResponseDTO> pushed = new ArrayList<>();

        for (WineRecipe r : recipe) {
            double kgNeeded = r.kgPerLiter * dto.plannedLiters;

            WineBatchGrapeUsage usage = new WineBatchGrapeUsage();
            usage.batch = batch;
            usage.variety = r.grapeVariety;
            usage.quantityKg = kgNeeded;
            usageRepository.persist(usage);

            GrapeStockMovement movement = new GrapeStockMovement();
            movement.variety = r.grapeVariety;
            movement.quantityKg = -kgNeeded;
            movement.movementType = "OUT";
            movement.createdBy = user;
            grapeStockRepository.persist(movement);

            double totalKg = grapeStockRepository.getTotalKgForVariety(r.grapeVariety.id);

            List<Notification> created = notificationService.checkGrapeLevels(r.grapeVariety, totalKg);
            for (Notification n : created) {
                pushed.add(toDto(n));
            }
        }

        List<WineBatchGrapeUsage> usages =
                usageRepository.list("batch.id", batch.id);

        WineBatchResponseDTO res = WineBatchMapper.toDTO(batch, usages);
        res.notifications = pushed;
        return res;
    }

    private static NotificationResponseDTO toDto(Notification n) {
        NotificationResponseDTO d = new NotificationResponseDTO();
        d.id = n.id;
        d.type = n.type;
        d.resourceType = n.resourceType;
        d.resourceId = n.resourceId;
        d.level = n.level;
        d.message = n.message;
        d.createdAt = n.createdAt != null ? n.createdAt.toString() : null;
        d.isRead = n.isRead;
        return d;
    }
}
