package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.BottleTypeCreateDTO;
import bg.tu.varna.si.dto.BottleTypeResponseDTO;
import bg.tu.varna.si.mapper.BottleTypeMapper;
import bg.tu.varna.si.model.BottleType;
import bg.tu.varna.si.repository.BottleTypeRepository;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/bottle-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BottleTypeResource {

    @Inject
    BottleTypeRepository repository;

    // -------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------
    @GET
//    @Authenticated
    public List<BottleTypeResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(BottleTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public BottleTypeResponseDTO create(BottleTypeCreateDTO dto) {
        BottleType entity = BottleTypeMapper.fromCreateDTO(dto);

        repository.persist(entity);
        return BottleTypeMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------
    @GET
    @Path("/{id}")
    public BottleTypeResponseDTO getById(@PathParam("id") Long id) {
        BottleType entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException("Bottle type not found");
        }

        return BottleTypeMapper.toDTO(entity);
    }


    // -------------------------------------------------------
    // UPDATE BY ID
    // -------------------------------------------------------
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public BottleTypeResponseDTO update(@PathParam("id") Long id, BottleTypeCreateDTO dto) {
        BottleType entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException("Bottle type not found");
        }

        entity.volumeMl = dto.volumeMl;
        entity.description = dto.description;
        entity.criticalMinQty = dto.criticalMinQty;

        return BottleTypeMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // DELETE BY ID
    // -------------------------------------------------------
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            throw new NotFoundException("Bottle type not found");
        }
    }
}
