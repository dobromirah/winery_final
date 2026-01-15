package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.BottleStockMovementCreateDTO;
import bg.tu.varna.si.dto.BottleStockMovementResponseDTO;
import bg.tu.varna.si.dto.NotificationResponseDTO;
import bg.tu.varna.si.mapper.BottleStockMovementMapper;
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

@Path("/bottle-stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BottleStockMovementResource {

    @Inject
    BottleStockMovementRepository repository;

    @Inject
    BottleTypeRepository bottleTypeRepository;

    @Inject
    AppUserRepository userRepository;

    @Inject
    NotificationService notificationService;

    @Inject
    CurrentUserService currentUserService;

    @GET
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public List<BottleStockMovementResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(BottleStockMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @POST
    @RolesAllowed({"WAREHOUSE_MANAGER"})
    @Transactional
    public BottleStockMovementResponseDTO create(BottleStockMovementCreateDTO dto) {

        BottleType bottleType = bottleTypeRepository.findById(dto.bottleTypeId);
        if (bottleType == null)
            throw new NotFoundException("BottleType with ID " + dto.bottleTypeId + " not found");

        AppUser user = currentUserService.getCurrentUser(); // от JWT

        BottleStockMovement movement =
                BottleStockMovementMapper.fromCreateDTO(dto, bottleType, user);

        repository.persist(movement);

        int totalQty = repository.getTotalQuantityForBottle(bottleType.id);

        List<NotificationResponseDTO> pushed = new ArrayList<>();
        List<Notification> created = notificationService.checkBottleLevels(bottleType, totalQty);
        for (Notification n : created) {
            pushed.add(toDto(n));
        }

        BottleStockMovementResponseDTO res = BottleStockMovementMapper.toDTO(movement);
        res.notifications = pushed;
        return res;
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("WAREHOUSE_MANAGER")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            throw new NotFoundException("Bottle stock movement not found");
        }
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
