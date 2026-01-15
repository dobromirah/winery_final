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

    public PlanResult plan(
            double remainingLiters,
            List<BottleType> allTypes,
            Long preferredBottleTypeId,
            List<Long> allowedBottleTypeIds
    ) {
        PlanResult out = new PlanResult();

        if (remainingLiters <= 0) {
            out.leftoverLiters = 0;
            out.plannedBottledLiters = 0;
            return out;
        }

        List<BottleType> types = allTypes;
        if (allowedBottleTypeIds != null && !allowedBottleTypeIds.isEmpty()) {
            Set<Long> allowed = new HashSet<>(allowedBottleTypeIds);
            types = allTypes.stream()
                    .filter(t -> t != null && allowed.contains(t.id))
                    .collect(Collectors.toList());
        }
        types = types.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing((BottleType t) ->
                                preferredBottleTypeId != null && t.id != null && t.id.equals(preferredBottleTypeId) ? 0 : 1
                        )
                        .thenComparing((BottleType t) -> t.volumeMl, Comparator.reverseOrder())
                )
                .collect(Collectors.toList());

        long remainingMl = Math.round(remainingLiters * 1000.0);

        for (BottleType t : types) {
            out.bottleCounts.put(t, 0);
        }

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
