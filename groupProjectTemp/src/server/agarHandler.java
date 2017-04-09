package server;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import objects.agarPlayer;
import objects.agarFood;
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


                    //System.out.println("Connected!");

                    OutputStream os = socket.getOutputStream();
                    objOut = new ObjectOutputStream(os);

                    String name = in.readLine();
                    //System.out.print(name);
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
                if (response.equals("updateFoods")) {
                    OutputStream os = socket.getOutputStream();
                    objOut = new ObjectOutputStream(os);
                    synchronized (agar.foods){
                        objOut.writeObject(agar.foods);
                    }
                    objOut.flush();
                    //System.out.println("Pong");
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
                    //Player Collision Happens Here!
                    Iterable<agarPlayer> iterablePlayers = agar.players;
                    for(agarPlayer player1: iterablePlayers){
                        ArrayList<agarPlayer> valuesToRemove = new ArrayList<>();
                        for(agarPlayer player2: iterablePlayers){
                            double player1X = player1.getX();
                            double player1Y = player1.getY();
                            double player2X = player2.getX();
                            double player2Y = player2.getY();

                            if(player1!=player2 && CircleCollision(player1X,player1Y,player1.getSize()/2,player2X,player2Y,player2.getSize()/2)){
                                //System.out.println("Collision Happening!");
                                if(player1.getSize() > player2.getSize()*1.33){
                                    agar.players.get(agar.players.indexOf(player1)).size +=player2.getSize();
                                    valuesToRemove.add(player2);
                                }

                            }
                        }
                        agar.players.removeAll(valuesToRemove);
                        valuesToRemove.clear();

                    }
                    synchronized(agar.foods) {
                        Iterable<agarFood> iterableFood = agar.foods;
                        //Food Collision is here!

                        for (agarPlayer player : iterablePlayers) {
                            ArrayList<agarFood> valuesToRemove = new ArrayList<>();
                            for (agarFood food : iterableFood) {

                                double playerX = player.getX();
                                double playerY = player.getY();
                                double foodX = food.getX();
                                double foodY = food.getY();

                                if (CircleCollision(playerX, playerY, player.getSize() / 2, foodX, foodY, food.getSize() / 2)) {
                                    //System.out.println("Collision Happening!");


                                    valuesToRemove.add(food);
                                    agar.players.get(agar.players.indexOf(player)).size += 1;
                                }
                            }
                            agar.foods.removeAll(valuesToRemove);
                            valuesToRemove.clear();
                        }
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
    public boolean CircleCollision(double x1, double y1,double r1, double x2,double y2, double r2){


        return Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) < (((r1 + r2)) * ((r1 + r2)));
    }
}
