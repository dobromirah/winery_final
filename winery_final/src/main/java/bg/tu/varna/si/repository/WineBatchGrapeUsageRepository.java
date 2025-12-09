package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.WineBatchGrapeUsage;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WineBatchGrapeUsageRepository implements PanacheRepository<WineBatchGrapeUsage> {
}
