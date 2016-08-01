package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AkkaTest {

    protected static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

}
