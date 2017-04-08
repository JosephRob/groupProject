package objects;

import java.io.Serializable;

/**
 * @author Joseph
 * @date 17/4/8
 */
public class ShootyGameDude implements Serializable{
    public Double X,Y,VX,VY;
    public int score;

    /**
     * default counstructor of ShootyGameDude.
     * x and y are provided and velocity and score are zero
     * @param X
     * @param Y
     */
    public ShootyGameDude(double X, double Y){
        this.X=X;
        this.Y=Y;
        VX=0.0;
        VY=0.0;
        score=0;
    }
}
