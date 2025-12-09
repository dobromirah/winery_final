package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class AppUser extends PanacheEntity {

    public String keycloakId;   // UUID from Keycloak
    public String fullName;     // optional, for UI
    public String role;         // ADMIN / OPERATOR / WAREHOUSE_MANAGER
}
