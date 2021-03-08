package pl.piterowsky.javamix.pattern.functional;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Choose different behaviour based on provided input
 *
 * @author piterowsky
 */
@Log4j2
public class StrategyPattern {

    public static void main(String[] args) {
        var item = new Item(1L, BigDecimal.valueOf(120.99));
        log.info("Imperative calc - basic price: {}", new ImperativeWay.BasicDeliveryPriceCalculator().priceFor(item));
        log.info("Functional calc - basic price: {}", FunctionalWay.Plan.BASIC.deliveryPrice.apply(item));
    }

    static class FunctionalWay {

        public enum Plan {
            BASIC(deliveryPrice("0.025")),
            PREMIUM(deliveryPrice("0"));

            Plan(Function<Item, BigDecimal> deliveryPrice) {
                this.deliveryPrice = deliveryPrice;
            }

            public final Function<Item, BigDecimal> deliveryPrice;

            private static Function<Item, BigDecimal> deliveryPrice(String percentage) {
                return item -> item.getPrice().multiply(new BigDecimal(percentage)).add(new BigDecimal("1.0"));
            }
        }

    }

    static class ImperativeWay {

        static class BasicDeliveryPriceCalculator implements DeliveryPriceCalculator {

            @Override
            public BigDecimal priceFor(Item item) {
                return item.getPrice().multiply(new BigDecimal("0.025")).add(new BigDecimal("1.0"));
            }

        }

        static class PremiumDeliveryPriceCalculator implements DeliveryPriceCalculator {

            @Override
            public BigDecimal priceFor(Item item) {
                return new BigDecimal("1.0");
            }

        }

    }

    interface DeliveryPriceCalculator {

        BigDecimal priceFor(Item item);

    }

    @Data
    static class Item {

        private final Long id;
        private final BigDecimal price;

    }

}
