package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class BottledWine extends PanacheEntity {

    @ManyToOne
    public WineBatch batch;

    @ManyToOne
    public BottleType bottleType;

    public int quantityBottles; // number of bottles filled
}

