package bg.tu.varna.si.repository;

import bg.tu.varna.si.model.WineRecipe;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class WineRecipeRepository implements PanacheRepository<WineRecipe> {

    public List<WineRecipe> findByWineTypeId(Long wineTypeId) {
        return list("wineType.id", wineTypeId);
    }
}
