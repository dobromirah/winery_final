package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.AppUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AppUserRepository implements PanacheRepository<AppUser> {

    public AppUser findByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).firstResult();
    }
}
