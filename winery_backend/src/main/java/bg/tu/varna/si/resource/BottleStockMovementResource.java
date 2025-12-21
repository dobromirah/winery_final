package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.BottleStockMovementCreateDTO;
import bg.tu.varna.si.dto.BottleStockMovementResponseDTO;
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

    // -------------------------------------------------------
    // GET ALL MOVEMENTS
    // -------------------------------------------------------
    @GET
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public List<BottleStockMovementResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(BottleStockMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public BottleStockMovementResponseDTO getById(@PathParam("id") Long id) {
        BottleStockMovement entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException("Bottle stock movement not found");
        }

        return BottleStockMovementMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // CREATE STOCK MOVEMENT
    // -------------------------------------------------------

    @Inject
    CurrentUserService currentUserService;

    @POST
    @RolesAllowed({"WAREHOUSE_MANAGER", "ADMIN"}) // временно за тест
    @Transactional
    public BottleStockMovementResponseDTO create(BottleStockMovementCreateDTO dto) {

        BottleType bottleType = bottleTypeRepository.findById(dto.bottleTypeId);
        if (bottleType == null)
            throw new NotFoundException("BottleType with ID " + dto.bottleTypeId + " not found");

        AppUser user = currentUserService.getCurrentUser(); // ✅ от JWT

        BottleStockMovement movement =
                BottleStockMovementMapper.fromCreateDTO(dto, bottleType, user);

        repository.persist(movement);

        int totalQty = repository.getTotalQuantityForBottle(bottleType.id);
        notificationService.checkBottleLevels(bottleType, totalQty);

        return BottleStockMovementMapper.toDTO(movement);
    }


    // -------------------------------------------------------
    // DELETE MOVEMENT
    // -------------------------------------------------------
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
}
