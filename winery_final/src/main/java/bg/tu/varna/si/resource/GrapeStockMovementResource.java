package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.GrapeStockMovementCreateDTO;
import bg.tu.varna.si.dto.GrapeStockMovementResponseDTO;
import bg.tu.varna.si.mapper.GrapeStockMovementMapper;
import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.*;
import bg.tu.varna.si.service.NotificationService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

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

    // -------------------------------------------------------
    // GET ALL MOVEMENTS
    // -------------------------------------------------------
    @GET
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public List<GrapeStockMovementResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(GrapeStockMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------
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

    // -------------------------------------------------------
    // CREATE STOCK MOVEMENT
    // -------------------------------------------------------
    @POST
    @RolesAllowed("WAREHOUSE_MANAGER")
    @Transactional
    public GrapeStockMovementResponseDTO create(GrapeStockMovementCreateDTO dto) {

        GrapeVariety variety = varietyRepository.findById(dto.varietyId);
        if (variety == null) {
            throw new NotFoundException("Grape variety with ID " + dto.varietyId + " not found");
        }

        AppUser user = userRepository.findById(dto.createdById);
        if (user == null) {
            throw new NotFoundException("User with ID " + dto.createdById + " not found");
        }

        // Create entity
        GrapeStockMovement entity = new GrapeStockMovement();
        entity.variety = variety;
        entity.quantityKg = dto.quantityKg;
        entity.movementType = dto.movementType;
        entity.createdBy = user;

        repository.persist(entity);

        // Recalculate stock and trigger notifications
        double totalKg = repository.getTotalKgForVariety(variety.id);
        notificationService.checkGrapeLevels(variety, totalKg);

        return GrapeStockMovementMapper.toDTO(entity);
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
            throw new NotFoundException("Grape stock movement not found");
        }
    }
}
