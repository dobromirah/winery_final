package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.WineBatch;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WineBatchRepository implements PanacheRepository<WineBatch> {
}
