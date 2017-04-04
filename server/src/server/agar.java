package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by 100588398 on 4/1/2017.
 */
public class agar implements Runnable  {

    int port;
    private ServerSocket serverSocket;
    public static ArrayList<agarPlayer> players;
    public static ArrayList<agarFood> foods;

    public agar(int port){

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

        try {
            while (true) {
                Socket socket = this.serverSocket.accept();
                agarHandler handler =
                        new agarHandler(socket);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
