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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Application {
    private Canvas canvas;
    private TextArea chatArea;
    private TextField ansArea;
    String answer = "";
    String toDraw = "draw";
    String notAnswered = "yes";
    String itsAnswered = "no";
    String hostName = "127.0.0.1";
    String playerName;
    Thread runChat;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //runChat = new Thread(chat);
        Stage inputName = new Stage();
        Label lab1 = new Label();
        lab1.setText("Input Your Name");

        Button but1 = new Button();
        but1.setText("OK");

        TextField tf1 = new TextField();

        but1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playerName = tf1.getText();
                //System.out.println(playerName);
                inputName.close();
                primaryStage.show();
                try {
                    startPlay();
                    runChat.start();
                } catch (IOException ex){}
            }
        });

        GridPane panee = new GridPane();
        panee.add(lab1, 0,0);
        panee.add(tf1,0,1);
        panee.add(but1,0,2);

        Group allInput = new Group();
        allInput.getChildren().add(panee);

        inputName.setScene(new Scene(allInput, 200,150, Color.WHITE));
        inputName.show();


        Group root = new Group();

        GridPane chatSys = new GridPane();

        chatArea = new TextArea();
        chatArea.setMaxWidth(300);
        chatArea.setMinHeight(470);
        chatArea.setEditable(false);

        ansArea = new TextField();
        ansArea.setMinHeight(30);
        ansArea.setMinWidth(250);

        Button send = new Button();
        send.setText("Send");
        send.setMaxHeight(30);
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String text = tf1.getText();
                tf1.setText("");

                try{
                    Socket socket = new Socket(hostName, 9053);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println(text);
                    out.flush();
                    socket.close();
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        GridPane sendText = new GridPane();
        sendText.add(ansArea,0,0);
        sendText.add(send,1,0);

        chatSys.add(chatArea,0,0);
        chatSys.add(sendText,0,1);

        BorderPane all = new BorderPane();

        canvas = new Canvas();
        canvas.setHeight(500);
        canvas.setWidth(800);

        all.setLeft(canvas);
        all.setRight(chatSys);

        Scene scene = new Scene(root, 1100,500, Color.WHITE);

        root.getChildren().add(all);
        primaryStage.setTitle("Drawing");
        primaryStage.setScene(scene);

        //startPlay();
        Runnable chat = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Socket sckt = new Socket(hostName, 9053);
                        BufferedReader br = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
                        //PrintWriter out = new PrintWriter(sckt.getOutputStream());
                        String text = br.readLine();
                        chatArea.setText(text);
                        br.close();
                        sckt.close();
                        runChat.sleep(100);
                    } catch (IOException ex){

                    } catch (InterruptedException e){

                    }
                }
            }
        };
        runChat = new Thread(chat);


/*
        Runnable go = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        //ServerSocket serverSocket = new ServerSocket(8888);
                        while(true){
                            Socket gotTurn = new Socket(hostName,2819);
                            PrintWriter out = new PrintWriter(gotTurn.getOutputStream());
                            out.println("1");
                            out.close();

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
*/
    }

    public void startPlay() throws IOException{
        Socket joinServer = new Socket(hostName,1997);
        PrintWriter out = new PrintWriter(joinServer.getOutputStream());
        out.println(playerName);
        out.println(playerName + " has joined the game!");
        out.close();
    }
/*
    public void chatRun() throws IOException{
        Socket joinAgain = new Socket(hostName,9053);
        BufferedReader br = new BufferedReader(new InputStreamReader(joinAgain.getInputStream()));

    }
*/
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
