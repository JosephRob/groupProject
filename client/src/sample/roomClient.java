package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.Socket;

/**
 * Created by lex on 16/03/17.
 */
public class roomClient implements  Runnable{
    int port;
    String IP,userID;
    boolean left,right,up,down;
    int x,y,v,size;

    @FXML
    private Canvas base;

    public roomClient(int port, String IP, String username){
        this.port=port;
        this.IP=IP;
        this.userID=username;
        size=50;

        base=new Canvas();
        base.setWidth(400);
        base.setHeight(base.getWidth());

        x=(int)base.getWidth()/10;
        y=(int)(3*base.getHeight()/4);
        v=0;

        Group root=new Group();
        root.getChildren().add(base);

        Scene scene=new Scene(root,400,400);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                        //System.out.println("left");
                        left=true;
                        break;
                    case RIGHT:
                        //System.out.println("right");
                        right=true;
                        break;
                    case UP:
                        //System.out.println("up");
                        up=true;
                        break;
                    case DOWN:
                        //System.out.println("down");
                        down=true;
                        break;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                        //System.out.println("left");
                        left=false;
                        break;
                    case RIGHT:
                        //System.out.println("right");
                        right=false;
                        break;
                    case UP:
                        //System.out.println("up");
                        up=false;
                        break;
                    case DOWN:
                        //System.out.println("down");
                        down=false;
                        break;
                }
            }
        });

        Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void run() {
        while (true) {try {
            while(true) {
                Thread.sleep(10);
                Socket socket=new Socket(IP,port);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        GraphicsContext gc = base.getGraphicsContext2D();
                        drawBack(gc);
                        gc.setFill(Color.BLACK);
                        gc.fillRect(x - size, y - size, size, size);
                    }
                });

                if (up) {
                    if (y==(int)(3*base.getHeight()/4))
                        v=-100;
                }
                v=v+4;
                y = y + (int)(((double)v)/10);
                if (down) {
                    //i do nothing for now
                }
                if (left) {
                    x = x - 5;
                }
                if (right) {
                    x = x + 5;
                }
                if (x<size)
                    x=size;
                else if (x>base.getWidth())
                    x=(int)base.getWidth();
                if (y<size)
                    y=size;
                else if(y>(3*base.getHeight()/4))
                    y=(int)(3*base.getHeight()/4);

                socket.close();
            }
        }catch (Exception e){System.err.println(e);}}
    }
    private void drawBack(GraphicsContext gc){
        gc.setFill(Color.DEEPSKYBLUE);
        gc.fillRect(0,0,base.getWidth(),base.getHeight());
        gc.setFill(Color.LAWNGREEN);
        gc.fillRect(0,3*base.getHeight()/4,base.getWidth(),base.getHeight());
        gc.setFill(Color.YELLOW);
        gc.fillOval(base.getWidth()*3/4,base.getHeight()/4,size,size);
    }
}
