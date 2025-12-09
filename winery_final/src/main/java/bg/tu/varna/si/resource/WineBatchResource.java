package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.WineBatchCreateDTO;
import bg.tu.varna.si.dto.WineBatchResponseDTO;
import bg.tu.varna.si.mapper.WineBatchMapper;
import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.*;
import bg.tu.varna.si.service.NotificationService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/wine-batches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WineBatchResource {

    @Inject WineBatchRepository batchRepository;
    @Inject WineBatchGrapeUsageRepository usageRepository;
    @Inject WineTypeRepository wineTypeRepository;
    @Inject AppUserRepository userRepository;
    @Inject WineRecipeRepository recipeRepository;
    @Inject GrapeStockMovementRepository grapeStockRepository;
    @Inject NotificationService notificationService;

    @GET
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
    @Path("/{id}")
    public WineBatchResponseDTO getById(@PathParam("id") Long id) {
        WineBatch batch = batchRepository.findById(id);
        if (batch == null)
            throw new NotFoundException("Batch not found");

        List<WineBatchGrapeUsage> usages =
                usageRepository.list("batch.id", id);

        return WineBatchMapper.toDTO(batch, usages);
    }

    @POST
    @Transactional
    public WineBatchResponseDTO create(WineBatchCreateDTO dto) {

        // Validate wine type
        WineType wineType = wineTypeRepository.findById(dto.wineTypeId);
        if (wineType == null)
            throw new NotFoundException("WineType with ID " + dto.wineTypeId + " not found.");

        // Validate user
        AppUser user = userRepository.findById(Long.valueOf(dto.createdById));
        if (user == null)
            throw new NotFoundException("User with ID " + dto.createdById + " not found.");

        // Validate recipe
        List<WineRecipe> recipe = recipeRepository.findByWineTypeId(dto.wineTypeId);
        if (recipe.isEmpty()) {
            throw new WebApplicationException("This wine type has no recipe defined.", 400);
        }

        // Create wine batch
        WineBatch batch = WineBatchMapper.fromCreateDTO(dto, wineType, user);
        batchRepository.persist(batch);

        // For each recipe row:
        // 1) Create WineBatchGrapeUsage
        // 2) Create GrapeStockMovement OUT
        // 3) Recalculate total stock and trigger notifications
        for (WineRecipe r : recipe) {
            double kgNeeded = r.kgPerLiter * dto.plannedLiters;

            // 1) Batch grape usage row
            WineBatchGrapeUsage usage = new WineBatchGrapeUsage();
            usage.batch = batch;
            usage.variety = r.grapeVariety;
            usage.quantityKg = kgNeeded;
            usageRepository.persist(usage);

            // 2) Grape stock movement OUT (negative amount)
            GrapeStockMovement movement = new GrapeStockMovement();
            movement.variety = r.grapeVariety;
            movement.quantityKg = -kgNeeded;
            movement.movementType = "OUT";
            movement.createdBy = user;
            grapeStockRepository.persist(movement);

            // 3) Recalculate total and check notifications
            double totalKg = grapeStockRepository.getTotalKgForVariety(r.grapeVariety.id);
            notificationService.checkGrapeLevels(r.grapeVariety, totalKg);
        }

        // Return full DTO
        List<WineBatchGrapeUsage> usages =
                usageRepository.list("batch.id", batch.id);

        return WineBatchMapper.toDTO(batch, usages);
    }
}