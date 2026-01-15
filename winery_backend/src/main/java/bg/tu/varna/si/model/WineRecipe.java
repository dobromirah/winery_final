package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class WineRecipe extends PanacheEntity {

    @ManyToOne
    public WineType wineType;
    @ManyToOne
    public GrapeVariety grapeVariety;
    public double kgPerLiter; // колко килограма от това грозде са нужни за направата на 1л вино
}
