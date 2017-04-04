package sample;

import com.sun.corba.se.spi.activation.Server;
import javafx.application.Application;
import javafx.application.Platform;
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
    String line;
    Boolean receiveIsRunning = false;
    Boolean gameStarted = false;
    @Override
    public void start(Stage primaryStage) throws Exception{
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
                line = ansArea.getText();
                ansArea.setText("");
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
                        Socket socket = new Socket(hostName, 9053);
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());

                        out.println(playerName);
                        if (line != null && !line.equals("")) {
                            out.println(line + "\n");
                        } else {
                            out.println(" ");
                        }
                        line = "";
                        out.flush();

                        String line, lines = "";
                        //System.out.println("1");

                        while ((line = br.readLine()) != null) {
                            if (line.equals("yesYouDraw")){
                                System.out.println("cp1");
                                receiveIsRunning = true;
                                System.out.println("cp2");
                                //System.out.println("DRAW RUNNING");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            youDraw();
                                        } catch (IOException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                                //System.out.println("yes draw");
                                //lines = lines + ("\n" + line);
                            } else if (line.equals("noYouReceive")){
                                System.out.println("cp3");
                                if (gameStarted == true){
                                    if (receiveIsRunning == false){
                                        System.out.println("Start Receive");
                                        receiveIsRunning = true;
                                    }
                                }
                                System.out.println("cp4");
                                //lines = lines + ("\n" + line);
                            } else if (line.equals("gameIsStarted")){
                                gameStarted = true;
                            } else if (line.equals("gameIsNotStarted")){
                                gameStarted = false;
                            } else {
                                lines = lines + ("\n" + line);
                            }
                        }
                        chatArea.setText(lines);

                        socket.close();
                        Thread.sleep(100);
                    } catch (java.io.IOException e) {
                        System.out.println(e + "\t" + playerName + "\t\t" + line);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            }
        };
        runChat = new Thread(chat);
    }

    public void startPlay() throws IOException{
        Socket joinServer = new Socket(hostName,1997);
        PrintWriter out = new PrintWriter(joinServer.getOutputStream());
        out.println(playerName);
        out.println(playerName + " has joined the game!");
        out.close();
        joinServer.close();
    }

    public void receiveDraw() throws IOException{
        receiveIsRunning = true;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        System.out.println("RECEIVE RUNNING");
        /*
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
        */
    }

    public void youDraw() throws IOException{
        System.out.println("DRAW RUNNING");
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
                    Socket socket = new Socket(hostName, 2026);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println(answer);
                    out.flush();
                    socket.close();
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
        /*
        try{
            ServerSocket answeredOrNot = new ServerSocket(2345);
            while(notAnswered != itsAnswered){
                Socket itAnswered = answeredOrNot.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(itAnswered.getInputStream()));
                itsAnswered = br.readLine();
                */
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //try{
                //    Socket socket = new Socket(hostName, 3456);
                //    PrintWriter out = new PrintWriter(socket.getOutputStream());
                gc.fillOval(event.getX()-2,event.getY()-2,5,5);
                //     out.println(event.getX()-2);
                //     out.println(event.getY()-2);
                //    out.flush();
                //} catch (IOException ex){
                //   ex.printStackTrace();
                // }
            }
        });
                /*
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
        */
    }

    public static void main(String[] args) {
        launch(args);
    }
}