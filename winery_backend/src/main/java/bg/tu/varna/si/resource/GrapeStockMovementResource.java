package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.GrapeStockMovementCreateDTO;
import bg.tu.varna.si.dto.GrapeStockMovementResponseDTO;
import bg.tu.varna.si.dto.NotificationResponseDTO;
import bg.tu.varna.si.mapper.GrapeStockMovementMapper;
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

@Path("/grape-stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GrapeStockMovementResource {

    @Inject
    GrapeStockMovementRepository repository;

    @Inject
    GrapeVarietyRepository varietyRepository;

    @Inject
    AppUserRepository userRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public List<GrapeStockMovementResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(GrapeStockMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public GrapeStockMovementResponseDTO getById(@PathParam("id") Long id) {
        GrapeStockMovement entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException("Grape stock movement not found");
        }

        return GrapeStockMovementMapper.toDTO(entity);
    }

    @Inject
    CurrentUserService currentUserService;

    @POST
    @RolesAllowed({"WAREHOUSE_MANAGER"})
    @Transactional
    public GrapeStockMovementResponseDTO create(GrapeStockMovementCreateDTO dto) {

        GrapeVariety variety = varietyRepository.findById(dto.varietyId);
        if (variety == null) {
            throw new NotFoundException("Grape variety with ID " + dto.varietyId + " not found");
        }

        AppUser user = currentUserService.getCurrentUser();

        GrapeStockMovement entity = new GrapeStockMovement();
        entity.variety = variety;
        entity.quantityKg = dto.quantityKg;
        entity.movementType = dto.movementType;
        entity.createdBy = user;

        repository.persist(entity);

        double totalKg = repository.getTotalKgForVariety(variety.id);
        List<NotificationResponseDTO> pushed = new ArrayList<>();
        List<Notification> created = notificationService.checkGrapeLevels(variety, totalKg);
        for (Notification n : created) {
            pushed.add(toDto(n));
        }

        GrapeStockMovementResponseDTO res = GrapeStockMovementMapper.toDTO(entity);
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
