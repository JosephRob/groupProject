package server;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import storage.*;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by lex on 04/04/17.
 */
public class ShootyGameServer implements Runnable{
    int port;
    ObservableList<ShootyGameTarget> targets=FXCollections.observableArrayList();
    HashMap<String, ShootyGameDude> players=new HashMap<>();
    HashMap<String, Integer> scores=new HashMap<>();
    double difficulty;
    double startTime;

    public ShootyGameServer(int Port){
        startTime=System.currentTimeMillis();
        difficulty=5.0;
        this.port=Port;
        Random rand=new Random(System.currentTimeMillis());
        for (int x=0;x<30 ;x++){
            //System.out.println(x);
            targets.add(new ShootyGameTarget(700*rand.nextDouble(),500*rand.nextDouble(),difficulty));
        }
        Thread moving=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException i){System.err.println(i);}
                    move();
                }
            }
        });
        moving.start();
    }
    private void move(){
        int take=0;
        for (int x=0;x<targets.size();x++){
            targets.get(x).move();
            if(System.currentTimeMillis()-targets.get(x).creation<45) {
                targets.remove(x);
                take++;
            }
        }
        Random rand=new Random(System.nanoTime());
        for (int x=0;x<take;x++)
            targets.add(new ShootyGameTarget(rand.nextDouble()*700,rand.nextDouble()*500,difficulty));
    }
    @Override
    public void run() {
        while (true){
            try {
                ServerSocket serverSocket=new ServerSocket(port);
                while (true){
                    Socket socket=serverSocket.accept();
                    double time=((System.currentTimeMillis()-startTime)/1000)%90;
                    //System.out.println(time);
                    if (time>=30) {

                        if (targets.isEmpty()) {
                            Random rand=new Random(System.nanoTime());
                            for (int x = 0; x < 10; x++) {
                                //System.out.println(x);
                                targets.add(new ShootyGameTarget(700 * rand.nextDouble(), 500 * rand.nextDouble(), difficulty));
                            }
                        }

                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                        out.writeObject(0);
                        //System.out.println("send targets");
                        for (int x = 0; x < targets.size(); x++) {
                            //System.out.println(x);
                            out.writeObject(targets.get(x));
                            //out.flush();
                        }
                        out.writeObject(null);
                        out.flush();

                        int hit = (int) in.readObject();
                        if (hit != -1) {
                            targets.remove(hit);
                            targets.addAll(new ShootyGameTarget(difficulty), new ShootyGameTarget(difficulty));
                        }

                        difficulty += (double) in.readObject();
                        for (ShootyGameTarget a : targets) {
                            a.V = difficulty;
                        }
                        //System.out.println(difficulty);

                        String currentUser = (String) in.readObject();
                        ShootyGameDude current = (ShootyGameDude) in.readObject();

                        if(players.containsKey(currentUser))players.put(currentUser,current);

                        String table="";
                        for (String user:players.keySet()){
                            table=table+user+": "+players.get(user).score+"\n";
                        }
                        out.writeObject(new DecimalFormat(".##").format(90-time)+" seconds remain\n"+table);
                        out.flush();

                        for (String dude:players.keySet()){
                            if (dude.equals(currentUser)!=true) {
                                out.writeObject(players.get(dude));
                                out.flush();
                            }
                        }
                        out.writeObject(null);
                        out.flush();

                        socket.close();
                    }
                    else{//this is where we join
                        //System.out.println("start");
                        if (players.size()!=0){
                            if(new File("best.txt").exists()) {
                                BufferedReader br = new BufferedReader(new FileReader(new File("best.txt")));
                                String bestName=br.readLine();
                                int bestScore=Integer.parseInt(br.readLine());
                                for (String name:players.keySet()){
                                    if(bestScore<players.get(name).score){
                                        PrintWriter out=new PrintWriter(new FileWriter(new File("best.txt")));
                                        out.println(name);
                                        out.println(""+players.get(name).score);
                                        out.flush();
                                    }
                                }
                            }
                            else{
                                PrintWriter out=new PrintWriter(new FileWriter(new File("best.txt")));
                                out.println(players.keySet().toArray()[0]);
                                out.println(players.get(players.keySet().toArray()[0]).score);
                                out.flush();
                            }
                        }
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                        out.writeObject(1);
                        out.flush();

                        String name=""+in.readObject();
                        ShootyGameDude temp=(ShootyGameDude)in.readObject();

                        if (scores.containsKey(name)==false) {
                            scores.put(name,0);
                            players.put(name,temp);
                        }
                        BufferedReader br=new BufferedReader(new FileReader(new File("best.txt")));
                        String highName=br.readLine();
                        int highScore=Integer.parseInt(br.readLine());
                        out.writeObject(new DecimalFormat(".##").format(30-time)+" seconds \nuntil start\nhighscore\n"+highName+"\n"+highScore);
                        out.flush();
                        //System.out.println(time);
                    }
                }
            }
            catch (IOException io){System.err.println(io);}
            catch (ClassNotFoundException C){System.err.println(C);}
        }
    }
}
