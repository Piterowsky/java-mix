package pl.piterowsky.javamix.pattern.functional;

import lombok.extern.log4j.Log4j2;

/**
 * Allows to define common steps in algorithms
 */
@Log4j2
public class TemplateMethodPattern {

    public static void main(String[] args) {
        var imperativeBus = new ImperativeWay.Bus();
        imperativeBus.start();

        var functionalBus = new FunctionalWay.Bus();
        functionalBus.start(() -> log.info("Functional - Pre start"));
    }

    private static class ImperativeWay {

        private interface Vehicle {

            void start();

        }

        private static abstract class AbstractVehicle implements ImperativeWay.Vehicle {

            @Override
            public void start() {
                preStart();
                log.info("Imperative - Start");
            }

            protected abstract void preStart();

        }

        private static class Bus extends AbstractVehicle {

            @Override
            protected void preStart() {
                log.info("Imperative - Pre start");
            }

        }

    }

    @Log4j2
    private static class FunctionalWay {

        private interface Vehicle {

            default void start(Runnable preStart) {
                preStart.run();
                log.info("Functional - Start");
            }

        }

        private static class Bus implements FunctionalWay.Vehicle {

        }

    }

}
