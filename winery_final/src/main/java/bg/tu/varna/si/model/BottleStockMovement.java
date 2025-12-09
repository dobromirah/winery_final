package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BottleStockMovement extends PanacheEntity {

    @ManyToOne
    public BottleType bottleType;

    public int quantity;        // +IN, -OUT
    public String movementType; // IN, OUT, ADJUSTMENT

    public LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    public AppUser createdBy;   // operator or warehouse manager
}
