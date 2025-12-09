package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class BottleType extends PanacheEntity {
    public int volumeMl;    // 750, 375, 200, 187, 1500
    public String description;
    public int criticalMinQty;
}
