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
            if(response!=null) {
                if (response.equals("init")) {
                    //System.out.print(response);


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

                } else if (response.equals("mouse")) {
                    String testName = in.readLine();
                    agarPlayer tempPlayer = null;
                    //System.out.println(agar.players.size());
                    for (agarPlayer player : agar.players) {
                        if (player.getName().equals(testName)) {
                            tempPlayer = player;
                        }
                    }

                    //System.out.println(testName);
                    mouseX = in.readLine();

                    mouseY = in.readLine();

                    tempPlayer.mouseX = Double.parseDouble(mouseX);
                    tempPlayer.mouseY = Double.parseDouble(mouseY);
                    //System.out.println(mouseY);


                }
                else if (response.equals("updatePlayers")) {
                    String testName = in.readLine();
                    agarPlayer tempPlayer = null;
                    //System.out.println(agar.players.size());

                    for (agarPlayer player : agar.players) {
                        if (player.getName().equals(testName)) {
                            tempPlayer = player;
                        }
                    }

                    if (tempPlayer != null) {
                        System.out.println(tempPlayer.mouseX);
                        tempPlayer.x = (tempPlayer.x * 99 * (1 + tempPlayer.size / 200) + tempPlayer.mouseX) / (1 + 99 * (1 + tempPlayer.size / 200));
                        tempPlayer.y = (tempPlayer.y * 99 * (1 + tempPlayer.size / 200) + tempPlayer.mouseY) / (1 + 99 * (1 + tempPlayer.size / 200));
                    }

                    OutputStream os = socket.getOutputStream();
                    objOut = new ObjectOutputStream(os);

                    objOut.writeObject(agar.players);
                    objOut.flush();
                }
            }
            socket.close();


        } catch(Exception e ){
            e.printStackTrace();
        }
    }

}
