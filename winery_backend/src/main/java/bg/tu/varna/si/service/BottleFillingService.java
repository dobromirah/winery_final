package bg.tu.varna.si.service;

import bg.tu.varna.si.model.BottleType;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class BottleFillingService {

    public static class BottleFillResult {
        public Map<BottleType, Integer> bottleCounts = new LinkedHashMap<>();
        public double leftoverLiters;
    }

    public BottleFillResult fillOptimally(double liters, List<BottleType> bottleTypes) {

        bottleTypes.sort(Comparator.comparingInt(bt -> -bt.volumeMl));

        BottleFillResult result = new BottleFillResult();
        double remaining = liters * 1000;

        for (BottleType bt : bottleTypes) {

            int count = (int)(remaining / bt.volumeMl);

            if (count > 0) {
                result.bottleCounts.put(bt, count);
                remaining -= count * bt.volumeMl;
            }
        }

        result.leftoverLiters = remaining / 1000.0;
        return result;
    }
}
