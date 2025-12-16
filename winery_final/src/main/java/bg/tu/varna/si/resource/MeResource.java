package bg.tu.varna.si.resource;

import bg.tu.varna.si.model.AppUser;
import bg.tu.varna.si.repository.AppUserRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public class MeResource {

    @Inject JsonWebToken jwt;
    @Inject AppUserRepository userRepository;

    @GET
    @PermitAll
    public Map<String, Object> me() {
        String sub = jwt.getSubject();

        AppUser user = userRepository.find("keycloakId", sub).firstResult();

        return Map.of(
                "sub", sub,
                "preferred_username", jwt.getClaim("preferred_username"),
                "azp", jwt.getClaim("azp"),
                "aud", jwt.getClaim("aud"),
                "realm_roles", jwt.getClaim("realm_access"),
                "resource_access", jwt.getClaim("resource_access"),
                "appUserFound", user != null,
                "appUserId", user != null ? user.id : null,
                "appUserRole", user != null ? user.role : null
        );
    }
}
