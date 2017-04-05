package objects;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 100588398 on 4/2/2017.
 */
public class agarPlayer implements Serializable {

    public double x;
    public double mouseX;
    public double mouseY;
    public double y;
    public double size;
    transient public Color color;
    public String name;

    public agarPlayer(double x,double y,double size,Color color,String name){
        this.x =x;
        this.mouseX = x;
        this.y = y;
        this.mouseY = y;
        this.size = size;
        this.color = color;
        this.name = name;
    }
    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        stream.writeDouble(color.getRed());
        stream.writeDouble(color.getGreen());
        stream.writeDouble(color.getBlue());
        stream.writeDouble(color.getOpacity());
        stream.writeObject(name);
    }
    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        double red = stream.readDouble();
        double green = stream.readDouble();
        double blue = stream.readDouble();
        double opacity = stream.readDouble();
        color = Color.color(red, green, blue, opacity);
        name = (String) stream.readObject();
    }

    public double getMouseX() {
        return mouseX;
    }

    public void setMouseX(double mouseX) {
        this.mouseX = mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public void setMouseY(double mouseY) {
        this.mouseY = mouseY;
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

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
