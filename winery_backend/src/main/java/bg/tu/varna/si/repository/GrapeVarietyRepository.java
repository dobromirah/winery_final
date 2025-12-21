package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.GrapeVariety;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GrapeVarietyRepository implements PanacheRepository<GrapeVariety> {

    public boolean existsByName(String name) {
        if (name == null) return false;
        return count("lower(name) = lower(?1)", name.trim()) > 0;
    }
}
