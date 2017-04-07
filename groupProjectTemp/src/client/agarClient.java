package client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import objects.agarFood;
import objects.agarPlayer;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by 100588398 on 4/1/2017.
 */
public class agarClient implements Runnable {

    public int port;
    public String IP;
    public String username;
    public boolean terminate;
    public Stage stage;
    public BufferedWriter out;
    //public BufferedWriter outTemp;
    public ObjectInputStream ois;
    public agarPlayer yourPlayer;
    private  ArrayList<agarPlayer> players;
    private ArrayList<agarFood> foods;
    public int index;
    public Canvas canvas;

    public double offsetMaxX;
    public double offsetMaxY;
    public double offsetMinX;
    public double offsetMinY;
    public double WORLD_SIZE_X = 1600;
    public double WORLD_SIZE_Y = 1200;
    public double VIEWPORT_SIZE_X;
    public double VIEWPORT_SIZE_Y;
    public GraphicsContext gc;
    public double camX;
    public double camY;

    public agarClient(final int  port, String IPA, String username){
        terminate=false;
        this.username=username;
        this.port=port;
        this.IP=IPA;

        stage=new Stage();
        stage.setTitle("Agar "+(port-1999));
        players = new ArrayList<>();



        try {

            Socket socket = new Socket(IP, port);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("HELLO");

            out.write("init"+"\n");
            out.flush();
            out.write(username+"\n");
            out.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            players = (ArrayList<objects.agarPlayer>)ois.readObject();
            foods = new ArrayList<>();

            yourPlayer = (objects.agarPlayer)ois.readObject();
            players.add(yourPlayer);
            index = players.indexOf(yourPlayer);

            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        canvas = new Canvas(WORLD_SIZE_X, WORLD_SIZE_Y);
        Group root = new Group();
        root.getChildren().add(canvas);
        gc=canvas.getGraphicsContext2D();
        VIEWPORT_SIZE_X = 800;
        VIEWPORT_SIZE_Y = 600;
        offsetMaxX = WORLD_SIZE_X - VIEWPORT_SIZE_X;
        offsetMaxY = WORLD_SIZE_Y -VIEWPORT_SIZE_Y;
        offsetMinX = 0;
        offsetMinY = 0;
        camX = (yourPlayer.getX()-yourPlayer.getSize()/2- (VIEWPORT_SIZE_X/2));
        camY = (yourPlayer.getY()-yourPlayer.getSize()/2- (VIEWPORT_SIZE_Y/2));
        if (camX > offsetMaxX) {
            camX = offsetMaxX;
        }
        else if (camX < offsetMinX) {
            camX = offsetMinX;
        }
        if (camY > offsetMaxY) {
            camY = offsetMaxY;
        }
        else if (camY < offsetMinY) {
            camY = offsetMinY;
        }
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e){
                try {
                    Socket socket = new Socket(IP, port);
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write("mouse"+"\n");
                    out.write(username+"\n");
                    out.write((e.getX()+camX)+"\n");
                   // System.out.println(e.getX());
                    out.write((e.getY()+camY)+"\n");
                    //System.out.println(e.getY());
                    out.flush();
                    socket.close();
                }catch (IOException error){
                    error.printStackTrace();
                }
            }
        });


        stage.setScene(new Scene(root,800,600));
        stage.show();

    }
    public void updateShapes(){
        Platform.runLater(new Runnable() {
            public void run() {
                canvas.setTranslateX(-camX);
                canvas.setTranslateY(-camY);

                //gc.translate(-camX/10,-camY/10);

                Iterable<agarPlayer> iterable = players;



                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,1600,1200);
                gc.setFill(Color.WHITE);
                gc.fillRect(camX,camY,VIEWPORT_SIZE_X,VIEWPORT_SIZE_Y);

                for (agarPlayer player : iterable) {
                    gc.setFill(player.color);

                    double playerX = player.getX()-player.getSize()/2;
                    double playerY = player.getY()-player.getSize()/2;

                    if(player.getName().equals(username)) {
                        camX = playerX - (VIEWPORT_SIZE_X/2);
                        camY = playerY - (VIEWPORT_SIZE_Y/2);
                        if (camX > offsetMaxX) {
                            camX = offsetMaxX;
                        }
                        else if (camX < offsetMinX) {
                            camX = offsetMinX;
                        }
                        if (camY > offsetMaxY) {
                            camY = offsetMaxY;
                        }
                        else if (camY < offsetMinY) {
                            camY = offsetMinY;
                        }


                        gc.fillOval(playerX, playerY, player.size, player.size);
                    }
                    else{
                        gc.fillOval(playerX, playerY, player.size, player.size);
                    }

                }
                Iterable<agarFood> iterable2 = foods;
                for(agarFood food : iterable2){
                        if(food.getX()>camX && food.getX()<(camX+VIEWPORT_SIZE_X) && food.getY()>camY && food.getY()<(camY+VIEWPORT_SIZE_Y)) {
                            gc.fillOval(food.getX() - food.getSize() / 2, food.getY() - food.getSize() / 2, food.getSize(), food.getSize());
                    }
                }
            }
        });

    }
    public void updatePlayers(){
        try {
            Socket socket = new Socket(IP, port);
            BufferedWriter outTemp = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            outTemp.write("updatePlayers"+"\n");
            outTemp.write(username+"\n");
            outTemp.flush();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            players = (ArrayList<objects.agarPlayer>)ois.readObject();
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateFoods(){
        try {

            Socket socket = new Socket(IP, port);
            BufferedWriter outTemp = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            outTemp.write("updateFoods"+"\n");
            outTemp.flush();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            foods = (ArrayList<objects.agarFood>)ois.readObject();
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void run(){
        Runnable redo = new Runnable() {

            public void run() {
                try {
                    while (true) {
                        Thread.sleep(10);
                        //System.out.println("This is going still");
                        updatePlayers();
                        updateShapes();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable redo2 = new Runnable() {

            public void run() {
                try {
                    while (true) {
                        Thread.sleep(100);
                        //System.out.println("This is going still");
                        updateFoods();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread keepRunningThread = new Thread(redo);
        keepRunningThread.start();
        Thread keepRunningThread2 = new Thread(redo2);
        keepRunningThread2.start();
    }
}
