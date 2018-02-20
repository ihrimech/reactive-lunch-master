package cc.xuloo.akka;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static akka.actor.SupervisorStrategy.*;

public class ActorSupervisionExample {

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("FlightBookingSystem");

        ActorRef supervisor = system.actorOf(Supervisor.props(), "supervisor");

        supervisor.tell(new UsualException(), ActorRef.noSender());
        supervisor.tell(new CustomException(), ActorRef.noSender());
        supervisor.tell(new CrazyException(), ActorRef.noSender());

        supervisor = system.actorOf(Supervisor.props(), "supervisor");

        supervisor.tell(new UsualException(), ActorRef.noSender());
    }

    public static class Supervisor extends AbstractActor {

        public static Props props() {
            return Props.create(Supervisor.class, Supervisor::new);
        }

        private ActorRef a;

        private ActorRef b;

        @Override
        public SupervisorStrategy supervisorStrategy() {
            return new AllForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES), DeciderBuilder.
                    match(UsualException.class, e -> resume()).
                    match(CustomException.class, e -> restart()).
                    match(CrazyException.class, e -> stop()).
                    matchAny(o -> escalate()).build());

        }

        @Override
        public void preStart() {
            a = getContext().actorOf(McCauleyCulkin.props("First Child"));
            b = getContext().actorOf(McCauleyCulkin.props("Second Child"));
        }

        @Override
        public void postStop() {
            System.out.println("Supervisor Stopped");
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Exception.class, msg -> a.tell(msg, getSelf()))
                    .build();
        }
    }

    /**
     * Child Actor ;)
     */
    public static class McCauleyCulkin extends AbstractActor {

        public static Props props(String key) {
            return Props.create(McCauleyCulkin.class, () -> new McCauleyCulkin(key));
        }

        private final String key;

        public McCauleyCulkin(String key) {
            this.key = key;
        }

        @Override
        public void preStart() {
            System.out.println(key + " Started");
        }

        @Override
        public void postStop() {
            System.out.println(key + " Stopped");
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Exception.class, msg -> {
                        throw msg;
                    })
                    .build();
        }
    }

    public static class UsualException extends Exception {}

    public static class CustomException extends Exception {}

    public static class CrazyException extends Exception {}

    public static class FatalExeption extends Exception {}
}
