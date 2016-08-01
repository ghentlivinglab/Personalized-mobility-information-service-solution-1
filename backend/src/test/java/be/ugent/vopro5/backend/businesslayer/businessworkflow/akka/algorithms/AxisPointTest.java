package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.springframework.util.Assert.notNull;

/**
 * Created by Lukas on 13/04/2016.
 */
public class AxisPointTest {

    private static final Random random = new Random();

    @Test
    public void testConstructor() {
        AxisPoint aP = new AxisPoint(0.0, 0.0);
        notNull(aP);
    }

    @Test
    public void testGetters()  {
        double x = random.nextDouble();
        double y = random.nextDouble();
        AxisPoint aP = new AxisPoint (x,y);
        notNull(aP);

        assertEquals(0, Double.compare(x,aP.getX()));
        assertEquals(0, Double.compare(y,aP.getY()));
    }


}
