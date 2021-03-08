package pl.piterowsky.javamix.pattern.functional;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

public class ChainOfResponsibilityPattern {

    public static void main(String[] args) {
        // Imperative way
        var imperativeSubject = new Subject();
        var imperativeChain = new ImperativeWay.Step1();
        imperativeChain.andThen(new ImperativeWay.Step2()).andThen(new ImperativeWay.Step3());
        imperativeChain.applyTo(imperativeSubject);

        // Functional way
        var functionalSubject = new Subject();
        Function<Subject, Subject> initial = s -> new Subject();
        Function<Subject, Subject> functionalChain = initial
                .andThen(FunctionalWay::doStep1)
                .andThen(FunctionalWay::doStep2)
                .andThen(FunctionalWay::doStep3);
        functionalChain.apply(functionalSubject);
    }

    @Log4j2(topic = "FunctionalWay")
    static class FunctionalWay {

        private static Subject doStep1(Subject s) {
            var newState = State.STATE_1;
            log.info("Initial state {}", newState);
            s.setCurrentState(newState);
            return s;
        }

        private static Subject doStep2(Subject s) {
            var newState = State.STATE_2;
            log.info("Transition from {} to {}", s.getCurrentState(), newState);
            s.setCurrentState(newState);
            return s;
        }

        private static Subject doStep3(Subject s) {
            var newState = State.STATE_3;
            log.info("Transition from {} to {}", s.getCurrentState(), newState);
            s.setCurrentState(newState);
            return s;
        }

    }

    @Log4j2(topic = "ImperativeWay")
    static class ImperativeWay {

        static abstract class AbstractStep {

            private AbstractStep nextStep;

            public AbstractStep andThen(AbstractStep nextStep) {
                this.nextStep = nextStep;
                return nextStep;
            }

            protected abstract Subject doStep(Subject subject);

            public Subject applyTo(Subject subject) {
                var updatedSubject = doStep(subject);
                return nextStep != null ? nextStep.applyTo(updatedSubject) : updatedSubject;
            }

        }

        static class Step1 extends AbstractStep {

            @Override
            protected Subject doStep(Subject subject) {
                var newState = State.STATE_1;
                log.info("Initial state {}", newState);
                subject.setCurrentState(State.STATE_1);
                return subject;
            }

        }

        static class Step2 extends AbstractStep {

            @Override
            protected Subject doStep(Subject subject) {
                var newState = State.STATE_2;
                log.info("Transition from {} to {}", subject.getCurrentState(), newState);
                subject.setCurrentState(newState);
                return subject;
            }

        }

        static class Step3 extends AbstractStep {

            @Override
            protected Subject doStep(Subject subject) {
                var newState = State.STATE_3;
                log.info("Transition from {} to {}", subject.getCurrentState(), newState);
                subject.setCurrentState(State.STATE_3);
                return subject;
            }

        }
    }

    @Data
    static class Subject {

        private State currentState;

    }

    enum State {
        STATE_1, STATE_2, STATE_3
    }


}
