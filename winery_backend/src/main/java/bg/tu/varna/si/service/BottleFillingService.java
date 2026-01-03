package bg.tu.varna.si.service;

import bg.tu.varna.si.model.BottleType;
import bg.tu.varna.si.repository.BottleStockMovementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class BottleFillingService {

    public static class PlanResult {
        public Map<BottleType, Integer> bottleCounts = new LinkedHashMap<>();
        public double leftoverLiters;
        public double plannedBottledLiters;
    }

    @Inject
    BottleStockMovementRepository stockRepo;

    /**
     * Stock-aware plan.
     * - prioritizes 750ml
     * - uses remainingLiters (produced - bottled)
     * - respects allowedBottleTypeIds if provided
     * - prefers preferredBottleTypeId if provided
     */
    public PlanResult plan(
            double remainingLiters,
            List<BottleType> allTypes,
            Long preferredBottleTypeId,
            List<Long> allowedBottleTypeIds
    ) {
        if (remainingLiters <= 0) {
            PlanResult r = new PlanResult();
            r.leftoverLiters = 0;
            r.plannedBottledLiters = 0;
            return r;
        }

        // filter allowed
        List<BottleType> types = allTypes;
        if (allowedBottleTypeIds != null && !allowedBottleTypeIds.isEmpty()) {
            Set<Long> allowed = new HashSet<>(allowedBottleTypeIds);
            types = allTypes.stream().filter(t -> allowed.contains(t.id)).collect(Collectors.toList());
        }

        // preferred-only mode (ако искаш: ако user е избрал preferred и е подал allowed само него — пак работи)
        if (preferredBottleTypeId != null) {
            // не режем до 1 тип, само го приоритизираме (за "само 750" подай allowed=[id])
        }

        // sort: preferred first, then 750 first, then bigger volumes first
        types = types.stream()
                .sorted(Comparator
                        .comparing((BottleType t) -> preferredBottleTypeId != null && t.id.equals(preferredBottleTypeId) ? 0 : 1)
                        .thenComparing(t -> t.volumeMl == 750 ? 0 : 1)
                        .thenComparing((BottleType t) -> t.volumeMl, Comparator.reverseOrder())
                )
                .collect(Collectors.toList());

        long remainingMl = Math.round(remainingLiters * 1000.0);

        PlanResult out = new PlanResult();

        for (BottleType t : types) {
            if (remainingMl <= 0) break;

            int cap = t.volumeMl;
            if (cap <= 0) continue;

            int available = stockRepo.getTotalQuantityForBottle(t.id);
            if (available <= 0) continue;

            int maxCount = (int) Math.min(available, remainingMl / cap);
            if (maxCount <= 0) continue;

            out.bottleCounts.put(t, maxCount);
            remainingMl -= (long) maxCount * cap;
        }

        out.leftoverLiters = remainingMl / 1000.0;
        out.plannedBottledLiters = remainingLiters - out.leftoverLiters;
        return out;
    }
}
