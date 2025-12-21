package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.BottleType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BottleTypeRepository implements PanacheRepository<BottleType> {
}
