package sample;

import com.sun.corba.se.spi.activation.Server;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Application {
    private Canvas canvas;
    String answer = "";
    String toDraw = "draw";
    String hostName = "";
    String notAnswered = "yes";
    String itsAnswered = "no";
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Group root = new Group();
        Scene scene = new Scene(root, 800,500, Color.WHITE);

        canvas = new Canvas();
        canvas.setHeight(500);
        canvas.setWidth(800);

        root.getChildren().add(canvas);
        primaryStage.setTitle("Drawing");
        primaryStage.setScene(scene);
        primaryStage.show();

        Runnable go = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        ServerSocket serverSocket = new ServerSocket(8888);
                        while(true){
                            Socket gotTurn = serverSocket.accept();
                            BufferedReader br = new BufferedReader(new InputStreamReader(gotTurn.getInputStream()));
                            String toDo = br.readLine();
                            if (toDraw == toDo){
                                youDraw();
                            } else {
                                receiveDraw();
                            }
                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        };

        Thread goRun = new Thread(go);
        goRun.start();
    }

    public void receiveDraw() throws IOException{
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        Runnable receiveDraw = new Runnable() {
            @Override
            public void run() {
                try{
                    ServerSocket serverSc = new ServerSocket(4567);
                    while(notAnswered != itsAnswered){
                        Socket us = serverSc.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(us.getInputStream()));
                        double xLoc = Double.parseDouble(br.readLine());
                        double yLoc = Double.parseDouble(br.readLine());
                        gc.fillOval(xLoc, yLoc, 5, 5);
                    }
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        };
        Thread goReceiveDraw = new Thread(receiveDraw);
        goReceiveDraw.start();
    }

    public void youDraw() throws IOException{
        Stage stage2 = new Stage();
        Label inpAns = new Label();
        inpAns.setText("Input Answer Below");

        TextField ans = new TextField();
        ans.setPromptText("Input Answer Here");

        Button send = new Button();
        send.setText("Send");
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                answer = ans.getText();
                ans.setText("");
                stage2.close();

                try {
                    Socket socket = new Socket(hostName, 1234);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println(answer);
                } catch (IOException ex){
                    ex.printStackTrace();
                }
                System.out.println(answer);
            }
        });

        VBox vb = new VBox();
        vb.setPadding(new Insets(10,10,10,10));
        vb.setSpacing(10);

        GridPane all = new GridPane();
        all.add(inpAns,0,0);
        all.add(ans,0,1);
        all.add(send,0,2);

        vb.getChildren().add(all);

        Scene popUp = new Scene(vb, 300,200);
        stage2.setScene(popUp);
        stage2.show();

        String winner = "";
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        try{
            ServerSocket answeredOrNot = new ServerSocket(2345);
            while(notAnswered != itsAnswered){
                Socket itAnswered = answeredOrNot.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(itAnswered.getInputStream()));
                itsAnswered = br.readLine();
                canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try{
                            Socket socket = new Socket(hostName, 3456);
                            PrintWriter out = new PrintWriter(socket.getOutputStream());
                            gc.fillOval(event.getX()-2,event.getY()-2,5,5);
                            out.println(event.getX()-2);
                            out.println(event.getY()-2);
                            out.flush();
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                });
                itAnswered.close();
                br.close();
            }
            Socket winn = answeredOrNot.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(winn.getInputStream()));
            winner = br.readLine();
            winn.close();
            br.close();
            answeredOrNot.close();
        } catch(IOException ex){
          ex.printStackTrace();
        }

        Stage stageWin = new Stage();
        Label win = new Label();
        win.setText(winner + " GOT IT RIGHT!");
        win.setFont(new Font(20));

        VBox vb2 = new VBox();
        vb2.setPadding(new Insets(10,10,10,10));
        vb2.setSpacing(10);

        vb2.getChildren().add(win);
        Scene popUp2 = new Scene(vb2, 300,200);

        stageWin.setScene(popUp2);

        long mTime = System.currentTimeMillis();
        long end = mTime + 5000;
        while(mTime < end){
            mTime = System.currentTimeMillis();
        }
        gc.clearRect(0,0,800,500);
        notAnswered = "yes";
        itsAnswered = "no";
        stageWin.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
