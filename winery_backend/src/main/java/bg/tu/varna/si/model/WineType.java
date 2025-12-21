package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class WineType extends PanacheEntity {
    public String name;
    public String color; // WHITE, RED, ROSE
    public String description;
}
