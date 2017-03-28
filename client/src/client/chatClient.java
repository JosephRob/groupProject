package client;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by lex on 16/03/17.
 */
public class chatClient implements Runnable {
    int port;
    String IP,line,userID;
    Stage stage;
    TextArea textArea;
    TextField textField;
    Button button,exit;
    boolean terminate;

    public chatClient(final int port, String IP, final String username){
        terminate=false;
        this.userID=username;
        this.port=port;
        this.IP=IP;

        stage=new Stage();
        stage.setTitle("chat "+(port-1999));
        BorderPane borderPane=new BorderPane();
        textArea=new TextArea();
        textArea.wrapTextProperty().setValue(true);
        //textArea.setEditable(false);
        textField=new TextField();
        textField.setMinWidth(300);

        textField.alignmentProperty().setValue(Pos.BASELINE_CENTER);
        final String tempIP=IP;

        try {
            Socket socket = new Socket(tempIP, port);
            PrintWriter out=new PrintWriter(socket.getOutputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("SYSYTEM\n"+username+" has joined");
            out.flush();
            br.readLine();
            socket.close();
        }
        catch (java.io.IOException e){
            System.out.println(e);
        }

        exit=new Button("exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.hide();
                stage.close();
            }
        });

        button=new Button("send");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                line=textField.getText();
                textField.setText("");
            }
        });
        GridPane gridPane=new GridPane();
        gridPane.add(textField,0,0);
        gridPane.add(button,1,0);
        gridPane.add(exit,2,0);
        borderPane.setCenter(textArea);
        borderPane.setBottom(gridPane);

        stage.setScene(new Scene(borderPane,500,700));
        stage.show();

        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    Socket socket = new Socket(tempIP, port);
                    PrintWriter out=new PrintWriter(socket.getOutputStream());
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.println("SYSYTEM\n"+username+" has left");
                    out.flush();
                    br.readLine();
                    socket.close();
                    terminate=true;
                }
                catch (java.io.IOException e){
                    System.out.println(e);
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    Socket socket = new Socket(tempIP, port);
                    PrintWriter out=new PrintWriter(socket.getOutputStream());
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.println("SYSYTEM\n"+username+" has left");
                    out.flush();
                    br.readLine();
                    socket.close();
                }
                catch (java.io.IOException e){
                    System.out.println(e);
                }
                terminate=true;
            }
        }, "Shutdown-thread"));
    }
    @Override
    public void run() {
        while(true) {
            try {
                if (terminate) {
                    Thread.currentThread().interrupt();
                    break;
                }


                Socket socket = new Socket(IP, port);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                out.println(userID);
                if (line != null && !(line.equals(""))) {
                    out.println(line);
                } else {
                    out.println(" ");
                }
                line = "";
                out.flush();

                String line, lines = "";
                //System.out.println("1");

                while ((line = br.readLine()) != null) {
                    lines = lines + ("\n" + line);
                }
                textArea.setText(lines);

                //System.out.println("2");
                socket.close();
                Thread.sleep(100);
            } catch (java.io.IOException e) {
                System.out.println(e + "\t" + userID + "\t\t" + line);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
