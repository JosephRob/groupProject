package storage;

import java.io.Serializable;

/**
 * Created by lex on 04/04/17.
 */
public class ShootyGameDude implements Serializable{
    public Double X,Y,VX,VY;
    public int score;
    public ShootyGameDude(double X, double Y){
        this.X=X;
        this.Y=Y;
        VX=0.0;
        VY=0.0;
        score=0;
    }

}
