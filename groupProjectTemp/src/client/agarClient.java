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
    public BufferedWriter outTemp;
    public ObjectInputStream ois;
    public ObjectOutputStream objOut;
    public agarPlayer yourPlayer;
    private  ArrayList<agarPlayer> players;
    //private static ArrayList<agarFood> foods;
    public int index;
    public Canvas canvas;
    public GraphicsContext gc;

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

            yourPlayer = (objects.agarPlayer)ois.readObject();
            players.add(yourPlayer);
            index = players.indexOf(yourPlayer);

            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        canvas = new Canvas(800, 600);
        Group root = new Group();
        root.getChildren().add(canvas);
        GraphicsContext gc=canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(50,50,100,100);
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e){
                try {
                    Socket socket = new Socket(IP, port);
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write("mouse"+"\n");
                    out.write(username+"\n");
                    out.write(""+e.getX()+"\n");
                    //System.out.println(e.getX());
                    out.write(""+e.getY()+"\n");
                   // System.out.println(e.getY());
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
                Iterable<agarPlayer> iterable = players;
                GraphicsContext gc=canvas.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
                for (agarPlayer player : iterable) {
                    gc.setFill(player.color);
                    gc.fillOval(player.x-player.size/2,player.y-player.size/2, player.size, player.size);
                }
            }
        });

    }
    public void updatePlayers(){
        try {
            Socket socket = new Socket(IP, port);
            outTemp = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

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

    public void run(){
        while (true){
            try {
                //System.out.println("This is going still");
                Thread.sleep(10);
                updatePlayers();
                updateShapes();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
