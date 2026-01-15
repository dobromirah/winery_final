package bg.tu.varna.si.service;

import bg.tu.varna.si.model.AppUser;
import bg.tu.varna.si.repository.AppUserRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class CurrentUserService {

    @Inject
    JsonWebToken jwt;

    @Inject
    AppUserRepository userRepository;

    public AppUser getCurrentUser() {
        String keycloakId = jwt.getSubject();

        AppUser user = userRepository.find("keycloakId", keycloakId).firstResult();
        if (user == null) {
            throw new ForbiddenException(
                    "User not registered in AppUser table (keycloakId=" + keycloakId + ")"
            );
        }
        return user;
    }
}
