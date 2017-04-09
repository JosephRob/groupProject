package server;

import javafx.scene.paint.Color;
import objects.agarFood;
import objects.agarPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 100588398 on 4/1/2017.
 */
public class agar implements Runnable  {

    int port;
    private ServerSocket serverSocket;
    public static ArrayList<agarPlayer> players;
    public static ArrayList<agarFood> foods;
    public int maxFood;

    public agar(int port){
        maxFood = 500;
        this.port=port;
        players = new ArrayList<>();
        foods = new ArrayList<>();

        try {
            this.serverSocket = new ServerSocket(port);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        Runnable redo = new Runnable() {

            public void run() {
                try {
                while(true) {
                    while (foods.size() < maxFood) {
                        Color color = new Color(Math.random(), Math.random(), Math.random(), 1);
                        foods.add(new agarFood(Math.random() * 1600, Math.random() * 1200, 5, color));
                    }
                    Thread.sleep(10000);
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable redo2 = new Runnable(){

            public void run() {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        agarHandler handler =
                                new agarHandler(socket);
                        Thread handlerThread = new Thread(handler);
                        handlerThread.start();

                    }

                }catch (Exception e){
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
