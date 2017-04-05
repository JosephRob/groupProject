package sample;

import com.sun.corba.se.spi.activation.Server;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
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
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Application {
    private Canvas canvas;
    private TextArea chatArea;
    private TextField ansArea;
    String answer = "";
    String winner = "";
    String toDraw = "draw";
    String hostName = "192.168.0.24";
    String playerName;
    Thread runChat;
    String line;
    Boolean answered = false;
    Boolean receiveIsRunning = false;
    Boolean gameStarted = false;
    Boolean alreadyInputAnswer = false;
    Thread goReceiveDraw;
    Thread runShowWin;
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
                            } else if (line.equals("gameIsStarted")){
                                gameStarted = true;
                                //receiveIsRunning = true;
                                //receiveDraw();
                                //System.out.println("gameisstarted");
                            } else if (line.equals("gameIsNotStarted")){
                                gameStarted = false;
                                //System.out.println("gameisnotstarted");
                            } else if (line.equals("noYouReceive")){
                                System.out.println("cp3");
                                if (gameStarted == true){
                                    if (receiveIsRunning == false){
                                        System.out.println("Start Receive");
                                        receiveDraw();
                                        receiveIsRunning = true;
                                    }
                                }
                                System.out.println("cp4");
                                //lines = lines + ("\n" + line);
                            } else if (line.equals("gameIsAnswered")){
                                answered = true;
                                System.out.print("Game Won By ");
                            } else if (line.equals("gameStillNotAnswered")){
                                answered = false;
                            } else if (line.contains("winneris")){
                                String[] theLine = line.split(",");
                                winner = theLine[theLine.length-1];
                                System.out.println(winner);
                            } else if (line.equals("stillNoWinner")){
                                winner = "";
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
        //out.println(playerName);
        out.println(playerName + " has joined the game!");
        out.close();
        joinServer.close();
    }

    public void receiveDraw() throws IOException{
        receiveIsRunning = true;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        //System.out.println("RECEIVE RUNNING");

        Runnable receiveDraw = new Runnable() {
            @Override
            public void run() {
                while(answered == false){
                    try {
                        Socket us = new Socket(hostName,1904);
                        BufferedReader br = new BufferedReader(new InputStreamReader(us.getInputStream()));
                        String inside = br.readLine();
                        if (!inside.equals("")){
                            String[] coordinates = inside.split("%");
                            for (int i = 0; i < coordinates.length; i++){
                                String[] coor = coordinates[i].split(",");
                                double xLoc = Double.parseDouble(coor[0]);
                                double yLoc = Double.parseDouble(coor[1]);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        gc.fillOval(xLoc, yLoc, 5, 5);
                                    }
                                });
                            }
                        }
                        Thread.sleep(100);
                    } catch (IOException ex){
                        ex.printStackTrace();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                if (answered == true){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Stage stageWin = new Stage();
                            Label win = new Label();
                            win.setText(" GOT IT RIGHT!");
                            //win.setFont(new Font(20));

                            //VBox vb2 = new VBox();
                            //vb2.setPadding(new Insets(10,10,10,10));
                            //vb2.setSpacing(10);

                            GridPane all = new GridPane();
                            all.add(win,0,0);
                            Group allInputt = new Group();
                            allInputt.getChildren().add(all);
                            //vb2.getChildren().add(all);

                            stageWin.setScene(new Scene(allInputt, 500,200,Color.WHITE));
                            stageWin.show();

                            long mTime = System.currentTimeMillis();
                            long end = mTime + 5000;
                            while(mTime < end){
                                mTime = System.currentTimeMillis();
                            }
                            gc.clearRect(0,0,800,500);
                            stageWin.close();
                            receiveIsRunning = false;
                            System.out.println("sampe sini");
                            //goReceiveDraw.stop();
                            //break;
                        }
                    });
                    //goReceiveDraw.stop();
                }
            }
        };

        goReceiveDraw = new Thread(receiveDraw);
        goReceiveDraw.start();
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
                    alreadyInputAnswer = true;
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

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);

        try {
            Socket socketDraw = new Socket(hostName, 2027);
            PrintWriter out = new PrintWriter(socketDraw.getOutputStream());
            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    //Socket socket = new Socket(hostName, 2026);
                    gc.fillOval(event.getX()-2,event.getY()-2,5,5);
                    double x = event.getX()-2;
                    double y = event.getY()-2;
                    String coordinate = (x + "," + y);
                    out.println(coordinate);
                    out.flush();
                }
            });
        } catch (IOException ex){
            ex.printStackTrace();
        }

        Runnable showWinner = new Runnable() {
            @Override
            public void run() {
                if (answered == true){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Stage stageWin = new Stage();
                            Label win = new Label();
                            win.setText(" GOT IT RIGHT!");
                            //win.setFont(new Font(20));

                            VBox vb2 = new VBox();
                            vb2.setPadding(new Insets(10,10,10,10));
                            vb2.setSpacing(10);

                            GridPane all = new GridPane();
                            all.add(win,0,0);

                            vb2.getChildren().add(all);
                            Scene popUp2 = new Scene(vb2, 500,200);

                            stageWin.setScene(popUp2);
                            stageWin.show();

                            long mTime = System.currentTimeMillis();
                            long end = mTime + 5000;
                            while(mTime < end){
                                mTime = System.currentTimeMillis();
                            }
                            gc.clearRect(0,0,800,500);
                            stageWin.close();
                            receiveIsRunning = false;
                            System.out.println("sampe sini");
                            //goReceiveDraw.stop();
                            //break;
                        }
                    });
                    //goReceiveDraw.stop();
                }
            }
        };
        runShowWin = new Thread(showWinner);
        runShowWin.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}