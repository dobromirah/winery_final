package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.AppUserCreateDTO;
import bg.tu.varna.si.dto.AppUserResponseDTO;
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
    AppUserRepository repo;


    @GET
    @RolesAllowed("ADMIN")
    public List<AppUserResponseDTO> listAll() {
        return repo.listAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @POST
    @RolesAllowed("ADMIN")
    @Transactional
    public AppUserResponseDTO create(AppUserCreateDTO dto) {
        if (dto == null) throw new WebApplicationException("Body required", 400);
        if (dto.keycloakId == null || dto.keycloakId.isBlank())
            throw new WebApplicationException("keycloakId is required", 400);
        if (dto.fullName == null || dto.fullName.isBlank())
            throw new WebApplicationException("fullName is required", 400);
        if (dto.role == null || dto.role.isBlank())
            throw new WebApplicationException("role is required", 400);

        String role = dto.role.trim().toUpperCase();
        if (!role.equals("OPERATOR") && !role.equals("WAREHOUSE_MANAGER") && !role.equals("ADMIN")) {
            throw new WebApplicationException("Invalid role: " + dto.role, 400);
        }

        // Unique check (ако имаш уникален индекс по keycloakId)
        AppUser existing = repo.find("keycloakId", dto.keycloakId).firstResult();
        if (existing != null) {
            throw new WebApplicationException("User already exists for keycloakId=" + dto.keycloakId, 409);
        }

        AppUser u = new AppUser();
        u.keycloakId = dto.keycloakId;
        u.fullName = dto.fullName;
        u.role = role;

        repo.persist(u);
        return toDto(u);
    }

    private AppUserResponseDTO toDto(AppUser u) {
        AppUserResponseDTO dto = new AppUserResponseDTO();
        dto.id = u.id;
        dto.keycloakId = u.keycloakId;
        dto.fullName = u.fullName;
        dto.role = u.role;
        return dto;
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        boolean deleted = repo.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("User not found");
        }
    }
}
