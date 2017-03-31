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

public class Main extends Application {
    String name = "Saya";
    String answered = "no";
    String realAnaswer = "";
    String hostName = "";

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //primaryStage.setTitle("Hello World");
        //primaryStage.setScene(new Scene(root, 300, 275));
        //primaryStage.show();
        /*
        Runnable gameStart = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        ServerSocket runn = new ServerSocket(8888);
                        while(true){
                            Socket yas = runn.accept();

                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        };
        */
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
