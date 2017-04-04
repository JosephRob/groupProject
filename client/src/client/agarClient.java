package client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
    public OutputStreamWriter out;
    public ObjectInputStream ois;
    public agarPlayer yourPlayer;
    private static ArrayList<agarPlayer> players;
    private static ArrayList<agarFood> foods;

    public agarClient(final int  port, String IPA, String username){
        terminate=false;
        this.username=username;
        this.port=port;
        this.IP=IPA;

        stage=new Stage();
        stage.setTitle("Agar "+(port-1999));
        BorderPane root=new BorderPane();
        try {
            Socket socket = new Socket(IP, port);
            out = new OutputStreamWriter(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            out.write(username+"\n");
            out.flush();

            yourPlayer = (agarPlayer)ois.readObject();
            players.add(yourPlayer);

            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e){
                try {
                    out.write(e.getX() + "\n");
                    System.out.println(e.getX());
                    out.write(e.getY() + "\n");
                    System.out.println(e.getY());

                    out.flush();
                }
                catch (IOException error){
                    error.printStackTrace();
                }
            }
        });
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        stage.setScene(new Scene(root,800,600));
        stage.show();

    }
    public void updateShapes(GraphicsContext gc){
        Iterable<agarPlayer> iterable = players;
        for(agarPlayer player : iterable){
            gc.setFill(player.color);
            gc.fillOval(player.x,player.y,player.size,player.size);

        }

    }
    public void run(){
        if (terminate) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            Socket socket = new Socket(IP, port);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ois = new ObjectInputStream(socket.getInputStream());
            out = new OutputStreamWriter(socket.getOutputStream());



            String input = br.readLine();
            System.out.println(input);

            yourPlayer = (agarPlayer)ois.readObject();


        }
        catch (Exception e){
            e.printStackTrace();
        }



    }
}
