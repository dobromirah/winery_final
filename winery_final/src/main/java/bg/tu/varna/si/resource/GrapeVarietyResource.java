package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.GrapeVarietyCreateDTO;
import bg.tu.varna.si.dto.GrapeVarietyResponseDTO;
import bg.tu.varna.si.mapper.GrapeVarietyMapper;
import bg.tu.varna.si.model.GrapeVariety;
import bg.tu.varna.si.repository.GrapeVarietyRepository;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/grape-varieties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class GrapeVarietyResource {

    @Inject
    GrapeVarietyRepository repository;

    // GET all
    @GET
    @RolesAllowed({"ADMIN", "OPERATOR", "WAREHOUSE_MANAGER"})
    public List<GrapeVarietyResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(GrapeVarietyMapper::toDTO)
                .collect(Collectors.toList());
    }

    // CREATE
    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public GrapeVarietyResponseDTO create(GrapeVarietyCreateDTO dto) {
        GrapeVariety entity = GrapeVarietyMapper.fromCreateDTO(dto);
        repository.persist(entity);
        return GrapeVarietyMapper.toDTO(entity);
    }

    // GET by ID
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "OPERATOR", "WAREHOUSE_MANAGER"})
    public GrapeVarietyResponseDTO getById(@PathParam("id") Long id) {
        GrapeVariety entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Grape variety not found");
        }
        return GrapeVarietyMapper.toDTO(entity);
    }

    // UPDATE by ID
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public GrapeVarietyResponseDTO update(@PathParam("id") Long id, GrapeVarietyCreateDTO dto) {
        GrapeVariety entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Grape variety not found");
        }

        entity.name = dto.name;
        entity.category = dto.category;
        entity.yieldLitersPerKg = dto.yieldLitersPerKg;
        entity.criticalMinKg = dto.criticalMinKg;

        return GrapeVarietyMapper.toDTO(entity);
    }

    // DELETE by ID
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Grape variety not found");
        }
    }
}
