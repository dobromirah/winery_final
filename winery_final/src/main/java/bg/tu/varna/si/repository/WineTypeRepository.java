package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.WineType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WineTypeRepository implements PanacheRepository<WineType> {

    public WineType findByName(String name) {
        return find("LOWER(name) = ?1", name.toLowerCase()).firstResult();
    }
}
