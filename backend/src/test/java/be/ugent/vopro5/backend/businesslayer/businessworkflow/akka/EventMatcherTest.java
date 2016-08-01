package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms.EventMatchingAlgorithm;
import org.junit.Test;
import org.mockito.Mockito;

public class EventMatcherTest extends AkkaTest {

    @Test
    public void testOnReceive() {
        final JavaTestKit probe = new JavaTestKit(system);

        final Props props = EventMatcher.props(probe.getRef(), Mockito.mock(EventMatchingAlgorithm.class));
        final TestActorRef<EventMatcher> subject = TestActorRef.create(system, props, "test");
        subject.tell(new Object(), ActorRef.noSender());

        probe.expectNoMsg();
    }

}