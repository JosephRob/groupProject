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
    List<String> connected;

    @Override
    public void start(Stage primaryStage) throws Exception{
        ServerSocket serverSocket = new ServerSocket(2819);
        connected = new ArrayList<String>();
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

        //Thread runGame = new Thread(gameStart);
        //runGame.start();

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


    }


    public static void main(String[] args) {
        launch(args);
    }
}
