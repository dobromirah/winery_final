package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class WineBatchGrapeUsage extends PanacheEntity {

    @ManyToOne
    public WineBatch batch;

    @ManyToOne
    public GrapeVariety variety;

    public double quantityKg;
}
