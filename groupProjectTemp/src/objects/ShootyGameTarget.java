package objects;

import java.io.Serializable;

import java.util.Random;

/**
 * @author Joseph
 * @date 17/4/8
 */
public class ShootyGameTarget implements Serializable{
    public Double x,y;
    public Double V, d;
    public double creation;

    /**
     * default constructor of ShootyGameTarget
     * sets all x, y, and velocity
     *
     * @param X
     * @param Y
     * @param V
     */
    public ShootyGameTarget(double X, double Y, double V){
        creation=System.currentTimeMillis();
        this.x=X;
        this.y=Y;
        this.V=V;
        d=new Random(System.currentTimeMillis()).nextDouble()*2*Math.PI;
    }

    /**
     * Sets velocity and calls set()
     *
     * @param V
     */
    public ShootyGameTarget(double V){
        set();
        this.V=V;
    }

    /**
     * Randomises direction x and y.
     */
    private void set(){
        Random random=new Random(System.nanoTime());
        x=random.nextDouble()*700;
        y=random.nextDouble()*500;
        d=random.nextDouble();
    }

    /**
     * Randomly turns.
     */
    private void turn(){
        double by=new Random().nextDouble()-0.5;
        d+=by;
    }

    /**
     * Updates x any y from velocity and direction.
     */
    public void move(){
        turn();
        x+=V*Math.cos(d);
        y+=V*Math.sin(d);
    }
}
