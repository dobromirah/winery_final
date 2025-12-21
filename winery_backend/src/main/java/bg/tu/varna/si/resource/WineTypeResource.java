package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.WineTypeCreateDTO;
import bg.tu.varna.si.dto.WineTypeMaxLitersDTO;
import bg.tu.varna.si.dto.WineTypeResponseDTO;
import bg.tu.varna.si.mapper.WineTypeMapper;
import bg.tu.varna.si.model.WineType;
import bg.tu.varna.si.repository.GrapeStockMovementRepository;
import bg.tu.varna.si.repository.WineRecipeRepository;
import bg.tu.varna.si.repository.WineTypeRepository;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/wine-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class WineTypeResource {

    @Inject
    WineTypeRepository repository;
    @Inject
    WineRecipeRepository recipeRepository;
    @Inject
    GrapeStockMovementRepository grapeStockRepository;


    // -------------------------------------------------------
    // GET ALL WINE TYPES
    // -------------------------------------------------------
    @GET
    @RolesAllowed({"ADMIN", "OPERATOR", "WAREHOUSE_MANAGER"})
    public List<WineTypeResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(WineTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "OPERATOR", "WAREHOUSE_MANAGER"})
    public WineTypeResponseDTO getById(@PathParam("id") Long id) {
        WineType entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("WineType not found");
        }
        return WineTypeMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public WineTypeResponseDTO create(WineTypeCreateDTO dto) {
        WineType entity = WineTypeMapper.fromCreateDTO(dto);
        repository.persist(entity);
        return WineTypeMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public WineTypeResponseDTO update(@PathParam("id") Long id, WineTypeCreateDTO dto) {
        WineType entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("WineType not found");
        }

        entity.name = dto.name;
        entity.color = dto.color;
        entity.description = dto.description;

        return WineTypeMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("WineType not found");
        }
    }

    @GET
    @Path("/{id}/max-planned-liters")
    @RolesAllowed({"ADMIN", "OPERATOR", "WAREHOUSE_MANAGER"})
    public WineTypeMaxLitersDTO getMaxPlannedLiters(@PathParam("id") Long id) {
        WineType wineType = repository.findById(id);
        if (wineType == null) throw new NotFoundException("WineType not found");

        var recipe = recipeRepository.findByWineTypeId(id);
        if (recipe.isEmpty()) {
            throw new WebApplicationException("This wine type has no recipe defined.", 400);
        }

        WineTypeMaxLitersDTO out = new WineTypeMaxLitersDTO();
        out.wineTypeId = id;
        out.limits = new java.util.ArrayList<>();

        double maxLiters = Double.POSITIVE_INFINITY;
        String limitingName = null;

        for (var r : recipe) {
            double availableKg = grapeStockRepository.getTotalKgForVariety(r.grapeVariety.id);
            double kgPerLiter = r.kgPerLiter;

            double litersForThisVariety = (kgPerLiter <= 0) ? 0 : (availableKg / kgPerLiter);

            WineTypeMaxLitersDTO.VarietyLimitDTO row = new WineTypeMaxLitersDTO.VarietyLimitDTO();
            row.varietyId = r.grapeVariety.id;
            row.varietyName = r.grapeVariety.name;
            row.availableKg = availableKg;
            row.kgPerLiter = kgPerLiter;
            row.maxLitersForVariety = litersForThisVariety;
            out.limits.add(row);

            if (litersForThisVariety < maxLiters) {
                maxLiters = litersForThisVariety;
                limitingName = r.grapeVariety.name;
            }
        }

        if (maxLiters == Double.POSITIVE_INFINITY) maxLiters = 0;

        out.maxLiters = Math.max(0, maxLiters);
        out.limitingVarietyName = limitingName;
        return out;
    }

}
