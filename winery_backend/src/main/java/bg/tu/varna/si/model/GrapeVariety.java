package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class GrapeVariety extends PanacheEntity {
    public String name;
    public String category; // WHITE or BLACK
    public double yieldLitersPerKg;
    public double criticalMinKg;
}

