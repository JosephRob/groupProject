package sample;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
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

    public chatClient(int port, String IP, String username){
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
        exit=new Button("exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
    }
    @Override
    public void run() {
        while(true){try {

            Socket socket=new Socket(IP,port);
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream());

            out.println(userID);
            if(line!=null && !(line.equals(""))) {
                out.println(line);
            }
            else {
                out.println(" ");
            }
            line="";
            out.flush();

            String line,lines="";
            //System.out.println("1");

            while ((line=br.readLine())!=null){
                lines=lines+("\n"+line);
            }
            textArea.setText(lines);

            //System.out.println("2");
            socket.close();
            Thread.sleep(100);
        }catch(Exception e){System.out.println(e+"\t"+userID+"\t\t"+line);while(true){}}}
    }
}
