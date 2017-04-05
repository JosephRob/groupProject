package server;

import javafx.scene.paint.Color;
import objects.agarPlayer;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by 100588398 on 4/3/2017.
 */
public class agarHandler implements Runnable{

    private Socket socket;
    private DataOutputStream out;
    private ObjectOutputStream objOut;
    private ObjectInputStream ois;
    public agarPlayer yourPlayer;
    public String mouseX;
    public String mouseY;



    public agarHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try {

            InputStream is = socket.getInputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(is)
            );
            String response = in.readLine();
                    System.out.println(response);
                if (response.equals("init")) {
                    System.out.print(response);


                    System.out.println("Connected!");

                    OutputStream os = socket.getOutputStream();
                    objOut = new ObjectOutputStream(os);

                    String name = in.readLine();
                    System.out.print(name);
                    yourPlayer = new agarPlayer(0, 0, 10, Color.RED, name);
                    agar.players.add(yourPlayer);

                    objOut.writeObject(agar.players);

                    objOut.writeObject(yourPlayer);
                    objOut.flush();
                    System.out.println("Ping!");
                }

                else {
                    Runnable redo = new Runnable() {
                        public void run() {
                            if (mouseX != null && mouseY != null) {
                                try {

                                    Thread.sleep(10);
                                    double X = (yourPlayer.x * 99 * (1 + yourPlayer.size / 200) + Double.parseDouble(mouseX)) / (1 + 99 * (1 + yourPlayer.size / 200));
                                    double Y = (yourPlayer.y * 99 * (1 + yourPlayer.size / 200) + Double.parseDouble(mouseY)) / (1 + 99 * (1 + yourPlayer.size / 200));


                                    yourPlayer = new agarPlayer(X, Y, yourPlayer.size, yourPlayer.color, yourPlayer.name);
                                    objOut.writeObject(agar.players);
                                    objOut.writeObject(yourPlayer);
                                    objOut.flush();
                                    socket.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    };

                    Runnable redo2 = new Runnable() {
                        public void run() {
                            try {
                                System.out.println("x:" + "CHECK");
                                System.out.println(response);
                                if (response != null) {
                                    if (response.equals("mouse")) {
                                        System.out.println("x:" + mouseX);
                                        mouseY = in.readLine();

                                        mouseX = in.readLine();
                                        //System.out.println("y:" + mouseY);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    Thread keepRunningThread = new Thread(redo2);
                    keepRunningThread.start();
                    Thread keepRunningThread2 = new Thread(redo);
                    keepRunningThread2.start();
                }

        } catch(Exception e ){
            e.printStackTrace();
        }
    }

}
