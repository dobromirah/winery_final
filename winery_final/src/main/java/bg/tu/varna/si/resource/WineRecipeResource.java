package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.WineRecipeCreateDTO;
import bg.tu.varna.si.dto.WineRecipeResponseDTO;
import bg.tu.varna.si.mapper.WineRecipeMapper;
import bg.tu.varna.si.model.GrapeVariety;
import bg.tu.varna.si.model.WineRecipe;
import bg.tu.varna.si.model.WineType;
import bg.tu.varna.si.repository.GrapeVarietyRepository;
import bg.tu.varna.si.repository.WineRecipeRepository;
import bg.tu.varna.si.repository.WineTypeRepository;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/wine-recipes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WineRecipeResource {

    @Inject
    WineRecipeRepository repository;

    @Inject
    WineTypeRepository wineTypeRepository;

    @Inject
    GrapeVarietyRepository varietyRepository;

    // -------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------
    @GET
    @RolesAllowed({"ADMIN", "OPERATOR"})
    public List<WineRecipeResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(WineRecipeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET ALL RECIPES FOR A WINE TYPE
    // Example: GET /wine-recipes/wine-type/3
    // -------------------------------------------------------
    @GET
    @Path("/wine-type/{wineTypeId}")
    @RolesAllowed({"ADMIN", "OPERATOR"})
    public List<WineRecipeResponseDTO> getByWineType(@PathParam("wineTypeId") Long wineTypeId) {
        return repository.findByWineTypeId(wineTypeId)
                .stream()
                .map(WineRecipeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // CREATE NEW RECIPE ENTRY
    // -------------------------------------------------------
    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public WineRecipeResponseDTO create(WineRecipeCreateDTO dto) {

        WineType wineType = wineTypeRepository.findById(dto.wineTypeId);
        if (wineType == null) {
            throw new NotFoundException("WineType with ID " + dto.wineTypeId + " not found");
        }

        GrapeVariety variety = varietyRepository.findById(dto.grapeVarietyId);
        if (variety == null) {
            throw new NotFoundException("GrapeVariety with ID " + dto.grapeVarietyId + " not found");
        }

        WineRecipe entity = WineRecipeMapper.fromCreateDTO(dto, wineType, variety);

        repository.persist(entity);

        return WineRecipeMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // UPDATE RECIPE ROW
    // -------------------------------------------------------
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public WineRecipeResponseDTO update(@PathParam("id") Long id, WineRecipeCreateDTO dto) {
        WineRecipe entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Recipe not found");
        }

        WineType wineType = wineTypeRepository.findById(dto.wineTypeId);
        if (wineType == null) {
            throw new NotFoundException("WineType with ID " + dto.wineTypeId + " not found");
        }

        GrapeVariety variety = varietyRepository.findById(dto.grapeVarietyId);
        if (variety == null) {
            throw new NotFoundException("GrapeVariety with ID " + dto.grapeVarietyId + " not found");
        }

        entity.wineType = wineType;
        entity.grapeVariety = variety;
        entity.kgPerLiter = dto.kgPerLiter;

        return WineRecipeMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // DELETE RECIPE ROW
    // -------------------------------------------------------
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Recipe not found");
        }
    }
}
