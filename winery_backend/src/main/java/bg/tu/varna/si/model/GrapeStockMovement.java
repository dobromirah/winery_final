package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GrapeStockMovement extends PanacheEntity {

    @ManyToOne
    public GrapeVariety variety;

    public double quantityKg;    // +IN, -OUT
    public String movementType;  // IN, OUT, ADJUSTMENT

    public LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    public AppUser createdBy;
}

