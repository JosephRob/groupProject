//Joseph Robertson
//Justin Duong
//Clyve Widjaya
package client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCombination;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.ObjectInputStream;

import java.io.ObjectOutputStream;
import java.util.Random;

import java.net.Socket;

import objects.*;

/**
 * @author Joseph
 * @date 17/4/8
 */
public class shootyGameClient implements Runnable{
    Boolean inGame;
    String name;
    int hits, port;
    Canvas feild;
    Boolean click,terminate;
    double MouseX,MouseY;
    Double AX=0.0,AY=0.0;
    int hit;
    double modify;
    Label gameState;
    String IP;

    ObservableList<ShootyGameTarget> others= FXCollections.observableArrayList();
    ShootyGameDude me;
    Stage stage;

    /**
     * Default contructor for shootyGameClient class.
     * Sets up GUI and initial states of variables/
     *
     * @param port
     * @param IP
     * @param name
     */
    public shootyGameClient(int port, String IP, String name){
        inGame=false;
        this.port=port;
        this.IP=IP;
        this.name=name;
        stage=new Stage();
        terminate=false;
        hits=0;
        hit=-1;
        click=false;
        Random rand=new Random(System.currentTimeMillis());
        int k=10;

        stage.setTitle("Game");
        BorderPane back=new BorderPane();
        MenuBar menuBar=new MenuBar();
        Menu file=new Menu("File");
        MenuItem reset=new MenuItem("reset");
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //send rest signal will reset time, players , and score
                System.out.println("reset");
            }
        });
        reset.setAccelerator(KeyCombination.keyCombination("CTRL+R"));
        MenuItem quit=new MenuItem("quit");
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                terminate=true;
                stage.hide();
                stage.close();
            }
        });
        MenuItem harder=new MenuItem("Harder");
        harder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                modify=0.5;
            }
        });
        harder.setAccelerator(KeyCombination.keyCombination("CTRL+H"));
        MenuItem easier=new MenuItem("Easier");
        easier.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                modify=-0.5;
            }
        });
        easier.setAccelerator(KeyCombination.keyCombination("CTRL+E"));
        quit.setAccelerator(KeyCombination.keyCombination("CTRL+Q"));
        file.getItems().addAll(reset,harder,easier,quit);
        menuBar.getMenus().add(file);
        back.setTop(menuBar);

        feild=new Canvas(700,500);
        feild.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MouseX=mouseEvent.getX();
                MouseY=mouseEvent.getY();
            }
        });
        feild.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                click=true;
            }
        });
        feild.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //System.out.println("key: "+event.getCode());
                if (inGame) {
                    switch (event.getCode()) {
                        case RIGHT:
                        case D:
                            AX = 10.0;
                            break;
                        case LEFT:
                        case A:
                            AX = -10.0;
                            break;
                        case UP:
                        case W:
                            AY = -10.0;
                            break;
                        case DOWN:
                        case S:
                            AY = 10.0;
                            break;
                    }
                }
            }
        });
        feild.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case RIGHT:
                    case D:
                        AX=0.0;
                        break;
                    case LEFT:
                    case A:
                        AX=0.0;
                        break;
                    case UP:
                    case W:
                        AY=0.0;
                        break;
                    case DOWN:
                    case S:
                        AY=0.0;
                        break;
                }
            }
        });
        back.setLeft(feild);

        gameState =new Label("");

        back.setRight(gameState);

        stage.setScene(new Scene(back, 800, 500));
        stage.show();
        me=new ShootyGameDude(feild.getWidth()/2,feild.getHeight()/2);

        feild.requestFocus();
    }

    /**
     * Sends other player info to server, get other players from server, gets targets from server
     */
    @Override
    public void run() {
        try{
            while (terminate==false){
                Thread.sleep(100);

                Socket socket=new Socket(IP,port);
                ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());

                int state=(int)in.readObject();
                if (state==0) {

                    ShootyGameTarget read;
                    others.clear();
                    while ((read = (ShootyGameTarget) in.readObject()) != null) {
                        others.add(read);
                    }
                    out.writeObject(hit);
                    hit = -1;
                    out.writeObject(modify);
                    modify = 0.0;
                    out.flush();

                    out.writeObject(name);
                    out.writeObject(me);
                    out.flush();
                    inGame=(Boolean)in.readObject();

                    final String content=in.readObject()+"";
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            gameState.setText(content);

                            GraphicsContext gc = feild.getGraphicsContext2D();
                            drawBack(gc);
                        }
                    });

                    if (inGame) {
                        ShootyGameDude temp;
                        while ((temp = (ShootyGameDude) in.readObject()) != null) {
                            final ShootyGameDude drawDude = temp;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    GraphicsContext gc = feild.getGraphicsContext2D();
                                    gc.setFill(Color.BLUE);
                                    gc.fillOval(drawDude.X, drawDude.Y, 20, 20);
                                }
                            });
                        }
                    }

                    socket.close();

                    move();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            GraphicsContext gc = feild.getGraphicsContext2D();
                            drawOthers(gc);
                            drawDude(gc);
                        }
                    });
                }
                else{
                    inGame=false;
                    me=new ShootyGameDude(feild.getWidth()/2,feild.getHeight()/2);
                    out.writeObject(name);
                    out.writeObject(me);
                    out.flush();

                    final String text=in.readObject()+"";

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            drawBack(feild.getGraphicsContext2D());
                            gameState.setText(text);
                        }
                    });
                }
                socket.close();
            }
        }
        catch (Exception e){}
    }

    /**
     * moves the current player using velocity determined by existing values and keys pressed(direction)
     */
    private void move(){
        if (inGame) {
            me.VX = (AX + 4 * me.VX) / 5;
            me.VY = (AY + 4 * me.VY) / 5;
            me.X += me.VX;
            me.Y += me.VY;
            if (me.X > 700) {
                me.X = 0.0;
            } else if (me.X < 0) {
                me.X = 700.0;
            }
            if (me.Y > 500) {
                me.Y = 0.0;
            } else if (me.Y < 0) {
                me.Y = 500.0;
            }
            for (ShootyGameTarget bump : others) {
                if (distance(me.X, me.Y, bump.x, bump.y) < 20) {
                    me.score -= 2;
                    hit = others.indexOf(bump);
                }
            }
        }
    }

    /**
     * draws empty background
     *
     * @param gc
     */
    private void drawBack(GraphicsContext gc){
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0,0,feild.getWidth(),feild.getHeight());
    }

    /**
     * draws the set of targets
     *
     * @param gc
     */
    private void drawOthers(GraphicsContext gc){
        gc.setFill(Color.RED);
        for (int x=0;x<others.size();x++)
            gc.fillOval(others.get(x).x%700-6,others.get(x).y%500-6,12,12);
    }

    /**
     * Draws current player, and diretion facing or shot(red line to target)
     *
     * @param gc
     */
    private void drawDude(GraphicsContext gc){
        if(inGame) {
            gc.setFill(Color.GREEN);
            gc.fillOval(me.X, me.Y, 20, 20);
            gc.setStroke(Color.DARKGREEN);
            gc.strokeOval(me.X, me.Y, 20, 20);
            if (click) {
                gc.setStroke(Color.RED);
                gc.strokeLine(me.X + 10, me.Y + 10, MouseX, MouseY);
                click = false;
                hits();
            } else {
                gc.setStroke(Color.BLACK);
                double x, y;
                double mag = Math.pow(Math.pow(MouseX - me.X, 2) + Math.pow(MouseY - me.Y, 2), 0.5);
                //System.out.println(mag);
                x = 20 * (MouseX - me.X) / mag;
                y = 20 * (MouseY - me.Y) / mag;

                gc.strokeLine(me.X + 10, me.Y + 10, me.X + x + 10, me.Y + y + 10);
            }
        }
    }

    /**
     * checks to see if a point clicked is in side of a target
     */
    private void hits(){
        for (int x=0;x<others.size();x++){
            if (distance(others.get(x).x+5,others.get(x).y+5,MouseX,MouseY)<6){
                hits++;

                me.score++;

                hit=x;
            }
        }
    }

    /**
     * Calculates the distance between two points
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return  distance between points
     */
    private double distance(double x1,double y1,double x2,double y2){
        return Math.pow((Math.pow((x1%700)-(x2%700),2)+Math.pow((y1%500)-(y2%500),2)),0.5);
    }
}
