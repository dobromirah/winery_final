package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.GrapeStockMovement;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GrapeStockMovementRepository implements PanacheRepository<GrapeStockMovement> {

    public double getTotalKgForVariety(Long varietyId) {
        var movements = list("variety.id", varietyId);

        return movements.stream()
                .mapToDouble(m -> m.quantityKg)
                .sum();
    }
}
