package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import org.junit.Test;

public class EventBrokerTest extends AkkaTest {

    @Test
    public void onReceive() throws Exception {
        final JavaTestKit probe = new JavaTestKit(system);

        final Props props = EventBroker.props(probe.getRef());
        final TestActorRef<EventBroker> subject = TestActorRef.create(system, props, "test");
        subject.tell(new Object(), ActorRef.noSender());

        probe.expectNoMsg();
    }
}