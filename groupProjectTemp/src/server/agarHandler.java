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
            //System.out.println(response);
            if(response!=null) {
                if (response.equals("init")) {
                    //System.out.print(response);


                    System.out.println("Connected!");

                    OutputStream os = socket.getOutputStream();
                    objOut = new ObjectOutputStream(os);

                    String name = in.readLine();
                    System.out.print(name);
                    yourPlayer = new agarPlayer(0, 0, 50, new Color(Math.random(),Math.random(),Math.random(),1), name);
                    agar.players.add(yourPlayer);

                    objOut.writeObject(agar.players);

                    objOut.writeObject(yourPlayer);
                    objOut.flush();
                    socket.close();

                } if (response.equals("mouse")) {
                    String testName = in.readLine();
                    //System.out.println("testName:"+testName);



                    mouseX = in.readLine();
                    //System.out.println("mouseX:"+mouseX);
                    mouseY = in.readLine();

                    if (mouseX != null && mouseY != null) {
                        agarPlayer tempPlayer = null;
                        //System.out.println(agar.players.size());
                        for (int i = 0;i<agar.players.size();i++){
                            //System.out.println("name: "+agar.players.get(i).getName());
                            //System.out.println("X: "+agar.players.get(i).getX());
                            //System.out.println("Y: "+agar.players.get(i).getY());
                            if (agar.players.get(i).getName().equals(testName)) {

                                agar.players.get(i).setMouseX(Double.parseDouble(mouseX));
                                agar.players.get(i).setMouseY(Double.parseDouble(mouseY));
                            }
                        }

                        //System.out.println("mouseY:" + tempPlayer.mouseY);
                    }
                    socket.close();


                }
                if (response.equals("updatePlayers")) {
                    //System.out.println("Ping");
                    String testName = in.readLine();
                    //System.out.println("testName:"+testName);
                    agarPlayer tempPlayer = null;
                    //System.out.println(agar.players.size());

                    for (agarPlayer player : agar.players) {
                        if (player.getName().equals(testName)) {
                            tempPlayer = player;
                        }
                    }

                    if (tempPlayer != null) {
                        //System.out.println(tempPlayer.mouseX);
                        tempPlayer.x = (tempPlayer.x * 99 * (1 + tempPlayer.size / 200) + tempPlayer.mouseX) / (1 + 99 * (1 + tempPlayer.size / 200));
                        tempPlayer.y = (tempPlayer.y * 99 * (1 + tempPlayer.size / 200) + tempPlayer.mouseY) / (1 + 99 * (1 + tempPlayer.size / 200));
                    }

                    OutputStream os = socket.getOutputStream();
                    objOut = new ObjectOutputStream(os);

                    objOut.writeObject(agar.players);
                    objOut.flush();
                    //System.out.println("Pong");
                    socket.close();
                }
            }
            socket.close();


        } catch(Exception e ){
            e.printStackTrace();
        }
    }

}
