package storage;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by lex on 04/04/17.
 */
public class ShootyGameTarget implements Serializable{
    public Double x,y;
    public Double V, d;
    public double creation;
    public ShootyGameTarget(double X, double Y, double V){
        creation=System.currentTimeMillis();
        this.x=X;
        this.y=Y;
        this.V=V;
        d=new Random(System.currentTimeMillis()).nextDouble()*2*Math.PI;
    }
    public ShootyGameTarget(double V){
        set();
        this.V=V;
    }
    private void set(){
        Random random=new Random(System.nanoTime());
        x=random.nextDouble()*700;
        y=random.nextDouble()*500;
        d=random.nextDouble();
    }
    private void turn(){
        double by=new Random().nextDouble()-0.5;
        d+=by;
    }
    public void move(){
        turn();
        x+=V*Math.cos(d);
        y+=V*Math.sin(d);
    }
}
