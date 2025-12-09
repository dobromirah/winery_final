package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.GrapeVariety;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GrapeVarietyRepository implements PanacheRepository<GrapeVariety> {
}
