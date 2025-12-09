package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.AppUserCreateDTO;
import bg.tu.varna.si.dto.AppUserResponseDTO;
import bg.tu.varna.si.mapper.AppUserMapper;
import bg.tu.varna.si.model.AppUser;
import bg.tu.varna.si.repository.AppUserRepository;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppUserResource {

    @Inject
    AppUserRepository repository;

    // -------------------------------------------------------
    // GET ALL USERS
    // -------------------------------------------------------
    @GET
    @RolesAllowed("ADMIN")
    public List<AppUserResponseDTO> listAll() {
        return repository.listAll()
                .stream()
                .map(AppUserMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET USER BY ID
    // -------------------------------------------------------
    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public AppUserResponseDTO getById(@PathParam("id") Long id) {
        AppUser entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("User not found");
        }
        return AppUserMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // GET USERS BY ROLE
    // Example: GET /users/role/ADMIN
    // -------------------------------------------------------
    @GET
    @Path("/role/{role}")
    @RolesAllowed("ADMIN")
    public List<AppUserResponseDTO> getByRole(@PathParam("role") String role) {
        return repository.find("role", role)
                .stream()
                .map(AppUserMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // CREATE USER
    // -------------------------------------------------------
    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public AppUserResponseDTO create(AppUserCreateDTO dto) {
        AppUser entity = AppUserMapper.fromCreateDTO(dto);
        repository.persist(entity);
        return AppUserMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // UPDATE USER BY ID
    // -------------------------------------------------------
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public AppUserResponseDTO update(@PathParam("id") Long id, AppUserCreateDTO dto) {
        AppUser entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("User not found");
        }

        entity.keycloakId = dto.keycloakId;
        entity.fullName = dto.fullName;
        entity.role = dto.role;

        return AppUserMapper.toDTO(entity);
    }

    // -------------------------------------------------------
    // DELETE USER
    // -------------------------------------------------------
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("User not found");
        }
    }
}
