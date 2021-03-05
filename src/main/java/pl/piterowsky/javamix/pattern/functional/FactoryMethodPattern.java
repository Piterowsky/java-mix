/*
 * Copyright 2021 Asseco Data Systems SA. All Rights Reserved.
 */
package pl.piterowsky.javamix.pattern.functional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

/**
 * Factory method pattern helps us to hide the specifics of object creation
 * We are able to define various ways to create object by providing appropriate parameters.
 *
 * @author piotr.tatarski
 */
@Log4j2
public class FactoryMethodPattern {

    public static void main(String[] args) {
        var imperativeBus = ImperativeWay.getInstance(ImperativeWay.VehicleType.BUS, VehicleColor.GREEN);
        var functionalBus = FunctionalWay.VehicleType.BUS.factory.apply(VehicleColor.GREEN);
        log.info("Imperative bus: {}", imperativeBus);
        log.info("Functional bus: {}", functionalBus);
        log.info("Are equals: {}", imperativeBus.equals(functionalBus));
    }

    static class ImperativeWay {
        public static Vehicle getInstance(VehicleType type, VehicleColor color) {
            if (VehicleType.CAR.equals(type)) {
                return new Car(color);
            } else if (VehicleType.BUS.equals(type)) {
                return new Bus(color);
            } else if (VehicleType.TRUCK.equals(type)) {
                return new Truck(color);
            }
            throw new IllegalArgumentException("No support for type " + type);
        }

        enum VehicleType {
            CAR,
            TRUCK,
            BUS;
        }
    }

    static class FunctionalWay {
        enum VehicleType {
            CAR(Car::new),
            BUS(Truck::new),
            TRUCK(Truck::new);

            public final Function<VehicleColor, Vehicle> factory;

            VehicleType(Function<VehicleColor, Vehicle> factory) {
                this.factory = factory;
            }
        }
    }

    static class Car extends Vehicle {

        public Car(VehicleColor color) {
            super(color);
        }
    }

    static class Truck extends Vehicle {

        public Truck(VehicleColor color) {
            super(color);
        }
    }

    static class Bus extends Vehicle {

        public Bus(VehicleColor color) {
            super(color);
        }
    }

    @Data
    @AllArgsConstructor
    abstract static class Vehicle {

        private VehicleColor color;
    }

    enum VehicleColor {
        RED,
        GREEN,
        BLUE
    }

}


















