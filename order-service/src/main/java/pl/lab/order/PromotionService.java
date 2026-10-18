package pl.lab.order;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class PromotionService {

    record Promotion(String code, int percent) {
    }

    private final Map<String, Promotion> promotions = Map.of(
            "WELCOME10", new Promotion("WELCOME10", 10),
            "BLACKWEEK", new Promotion("BLACKWEEK", 30));

    int discountFor(String promoCode) {
        if (promoCode == null || promoCode.isBlank()) {
            return 0;
        }
        return promotions.get(promoCode).percent();
    }
}
