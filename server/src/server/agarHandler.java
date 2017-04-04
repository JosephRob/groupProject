package server;

import javafx.scene.paint.Color;

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
    public agarPlayer yourPlayer;


    public agarHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try {
            InputStream is = socket.getInputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(is)
            );

            System.out.println("Connected!");
            OutputStream os = socket.getOutputStream();
            out = new DataOutputStream(os);
            objOut = new ObjectOutputStream(os);

            String name = in.readLine();
            System.out.print(name);
            yourPlayer = new agarPlayer(0,0,10, Color.RED,name);
            agar.players.add(yourPlayer);
            objOut.writeObject(yourPlayer);
            objOut.flush();
            out.writeBytes(name + "\n");
            while (true) {

                String mouseX = in.readLine();
                System.out.println("x:" + mouseX);
                String mouseY = in.readLine();
                System.out.println("y:" + mouseY);

                //Calculate new object

                objOut.writeObject(yourPlayer);




            }
        } catch(Exception e ){
            e.printStackTrace();
        }
    }

}
