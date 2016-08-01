package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms;

/**
 * Created by Lukas on 13/04/2016.
 */
public class AxisPoint {
    private double x, y;

    /**
     * Create a new AxisPoit
     * @param x
     * @param y
     */
    public AxisPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return
     */
    public double getX() {
        return x;
    }


    /**
     * @return
     */
    public double getY() {
        return y;
    }
}
