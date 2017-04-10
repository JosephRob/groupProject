//Joseph Robertson
//Justin Duong
//Clyve Widjaya
package objects;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 100588398 on 4/2/2017.
 */
public class agarFood implements Serializable {

    public double x;
    public double y;
    public int size;
    transient public Color color;


    public agarFood(double x,double y,int size,Color color){
        this.x =x;
        this.y = y;
        this.size = size;
        this.color = color;
    }
    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        stream.writeDouble(color.getRed());
        stream.writeDouble(color.getGreen());
        stream.writeDouble(color.getBlue());
        stream.writeDouble(color.getOpacity());
    }
    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        double red = stream.readDouble();
        double green = stream.readDouble();
        double blue = stream.readDouble();
        double opacity = stream.readDouble();
        color = Color.color(red, green, blue, opacity);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getSize(){
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
