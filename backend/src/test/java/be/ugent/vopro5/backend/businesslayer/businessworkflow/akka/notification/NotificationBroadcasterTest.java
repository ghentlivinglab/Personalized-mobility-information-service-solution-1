package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.AkkaTest;
import org.junit.Test;

public class NotificationBroadcasterTest extends AkkaTest {
    @Test
    public void onReceive() throws Exception {
        final JavaTestKit wsProbe = new JavaTestKit(system);
        final JavaTestKit mailProbe = new JavaTestKit(system);
        final JavaTestKit gcmProbe = new JavaTestKit(system);
        final JavaTestKit facebookProbe = new JavaTestKit(system);

        final TestActorRef subject = TestActorRef.create(system, NotificationBroadcaster.props(wsProbe.getRef(), mailProbe.getRef(),
                gcmProbe.getRef(), facebookProbe.getRef()), "test");
        subject.tell(new Object(), ActorRef.noSender());

        wsProbe.expectNoMsg();
        mailProbe.expectNoMsg();
        gcmProbe.expectNoMsg();
        facebookProbe.expectNoMsg();
    }

}