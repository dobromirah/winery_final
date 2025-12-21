package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.BottleStockMovement;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BottleStockMovementRepository implements PanacheRepository<BottleStockMovement> {

    public int getTotalQuantityForBottle(Long bottleTypeId) {
        return list("bottleType.id", bottleTypeId)
                .stream()
                .mapToInt(m -> m.quantity)
                .sum();
    }
}
