package sample;

import com.sun.corba.se.spi.activation.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    String name = "Saya";
    //String answered = "no";
    boolean answered = false;
    String realAnaswer = "";
    String hostName = "";
    ArrayList<String> players;
    String historyOfText = "";
    Thread updtChat;

    @Override
    public void start(Stage primaryStage) throws Exception{
        int i = 1;
        int playerIndex = 0;
        ServerSocket serverSocket = new ServerSocket(1997);
        ServerSocket chatSocket = new ServerSocket(9053);
        Runnable takingPeople = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Socket client = serverSocket.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String name = br.readLine();
                        String sent = br.readLine();
                        historyOfText += sent + "\n";
                        //someOneJoined(sent);
                        //br.close();
                        client.close();
                    } catch (IOException ex){}
                }
            }
        };

        Thread ppl = new Thread(takingPeople);
        ppl.start();

        Runnable chat = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Socket client = chatSocket.accept();
                        PrintWriter out = new PrintWriter(client.getOutputStream());
                        out.println(historyOfText);
                        out.close();
                        client.close();
                        updtChat.sleep(100);
                    } catch (IOException ex){

                    } catch (InterruptedException e){

                    }
                }
            }
        };

        updtChat = new Thread(chat);
        updtChat.start();
        /*
        serverSocket.setSoTimeout(30000);
        while(i < 6 && (!serverSocket.isClosed())){
            try{
                System.out.print("Player " + i + " = ");
                Socket client = serverSocket.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String name = br.readLine();
                System.out.print(name + ": ");
                System.out.println(client.getLocalSocketAddress() + ": ");
                //System.out.println(client.get);
                i++;
                br.close();
                client.close();
            } catch (IOException ex){

            }
        }
        i = 1;
        System.out.println(serverSocket.isClosed());
        ServerSocket trySocket = new ServerSocket(2009);
        trySocket.setSoTimeout(30000);
        while(i < 6 && (!trySocket.isClosed())){
            try{
                System.out.print("Player " + i + " = ");
                Socket client = trySocket.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String name = br.readLine();
                System.out.print(name + ": ");
                System.out.print(client.getRemoteSocketAddress() + ": ");
                //System.out.println(client.get);
                i++;
                br.close();
                client.close();
            } catch (IOException ex){

            }
        }
        */
        //if (i == 5 || serverSocket.isClosed()){

        //}
        //connected = new ArrayList<String>();
        /*
        Runnable gameStart = new Runnable() {
            @Override
            public void run() {
                while(true){
                    while(answered != true){
                        try{
                            while(true){
                                Socket client = serverSocket.accept();
                                connected.add()


                            }
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
*/
        //Thread runGame = new Thread(gameStart);
        //runGame.start();
/*
        //Listen for real answers
        try{
            ServerSocket forAns = new ServerSocket(1234);
            Socket ansSoc = forAns.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(ansSoc.getInputStream()));
            realAnaswer = br.readLine();
            br.close();
            ansSoc.close();
            forAns.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
*/

    }

    public static void main(String[] args) {
        launch(args);
    }
}
