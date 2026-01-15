package bg.tu.varna.si.resource;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

//Проверявам ролята на логнатия потребител

@Path("/debug/security")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityDebugResource {

    @Inject
    SecurityIdentity identity;

    @GET
    @Authenticated // само логнати потребители
    public Map<String, Object> debug() {
        return Map.of(
                "principal", identity.getPrincipal() != null ? identity.getPrincipal().getName() : null,
                "roles", identity.getRoles(),
                "isAnonymous", identity.isAnonymous()
        );
    }
}
