//Joseph Robertson
//Justin Duong
//Clyve Widjaya
/*
Clyve Widjaya
This File is for the client side of Draw game!
*/
package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.stage.WindowEvent;

/*
This is the main class for client side of Draw game!
Thread of this class will be created from the main client file
and the runnable thread will be the chat part of the game
*/
public class drawClient implements Runnable{
    private Canvas canvas;
    private TextArea chatArea;
    private TextField ansArea;
    public String answer;
    public String winner;
    public String hostName;
    public String playerName;
    public Thread runChat;
    public String line;
    public Boolean answered;
    public Boolean youDraww;
    public Boolean receiveIsRunning;
    public Boolean gameStarted;
    public Boolean alreadyInputAnswer;
    public Thread goReceiveDraw;
    public BorderPane display;
    public Stage primaryStage;
	
    /*
    This is the constructor class of the drawClient class
    It will open the UI which has canvas to draw and receive the drawing, and the chat box.
    This function has the shutdown hook thread, so if a user is quitting the game, system will
    tell people that he/she left and decrease the number of current player
    @Param int port, String ipAddress, String username
    @return - 
    */
    public drawClient(final int  port, String IPA, String username){
	this.playerName = username;
        this.hostName = IPA;
        answered = false;
        youDraww = false;
        receiveIsRunning = false;
        gameStarted = false;
        alreadyInputAnswer = false;
        answer = "";
        winner = "";
	primaryStage = new Stage();
        try {
            startPlay(port, IPA, username);
        } catch (IOException ex){
            ex.printStackTrace();
        }

        Group root = new Group();

        GridPane chatSys = new GridPane();

        chatArea = new TextArea();
        chatArea.setMaxWidth(425);
        chatArea.setMinHeight(470);
        chatArea.setEditable(false);

        ansArea = new TextField();
        ansArea.setMinHeight(30);
        ansArea.setMinWidth(375);

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

        display = new BorderPane();

        canvas = new Canvas();
        canvas.setHeight(500);
        canvas.setWidth(800);

        display.setLeft(canvas);
        display.setRight(chatSys);

        Scene scene = new Scene(root, 1225,500, Color.WHITE);

        root.getChildren().add(display);
        primaryStage.setTitle("Drawing " + (port-1999));
        primaryStage.setScene(scene);
        primaryStage.show();
	
	primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    Socket socket = new Socket(hostName, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println(username + " has left the game :(");
                    out.close();
                    socket.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    Socket socket = new Socket(hostName, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println(username + " has left the game :(");
                    out.close();
                    socket.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }, "Shutdown-thread"));	
    }

    /*
    This function will open a socket with the port given from the main UI. 
    Socket will connect to the serversocket, and tell the server that you are joining 
    and be in the game.
    @Param int port, String ipAddress, String username
    @return - 
    */
    public void startPlay(final int port, String IPA, String username) throws IOException{
        try {
            Socket joinServer = new Socket(IPA, port);
            PrintWriter out = new PrintWriter(joinServer.getOutputStream());
            out.println(username + " has joined the game!");
            out.close();
            joinServer.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /*
    This function is where the runnable chat run. It will constantly connect to the server
    with different port, and this function will update most of the booleans, like gameStarted,
    receiveIsRunning and other. When you are the one drawing and someone answered it, then the
    canvas will be renewed.
    @Param -
    @return - 
    */
    public void run(){
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
                        receiveIsRunning = true;
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
                    } else if (line.equals("gameIsStarted")){
                        gameStarted = true;
                    } else if (line.equals("gameIsNotStarted")){
                        gameStarted = false;
                    } else if (line.equals("noYouReceive")){
                        if (gameStarted == true){
                            if (receiveIsRunning == false){
                                receiveDraw();
                                receiveIsRunning = true;
                            }
                        }
                    } else if (line.equals("gameIsAnswered")){
                        answered = true;
                        if(youDraww == true){
                            gameStarted = false;
                            receiveIsRunning = false;

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    canvas = new Canvas();
                                    canvas.setHeight(500);
                                    canvas.setWidth(800);
                                    display.setLeft(canvas);
                                }
                            });
                            youDraww = false;
                            answered = false;
                        }
                    } else if (line.equals("gameStillNotAnswered")){
                        answered = false;
                    } else if (line.contains("winneris")){
                        String[] theLine = line.split(",");
                        winner = theLine[theLine.length-1];
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

    /*
    This function will run if you are set to receive the drawing. It has a delay of 100 miliseconds.
    This function has a runnable thread, while game is not answered, it will repeatedly connect
    to a socket and receive coordinates. If the game is answered, canvas will be set as a new
    canvas again
    @Param -
    @return - 
    */
    public void receiveDraw() throws IOException{
        receiveIsRunning = true;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);

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
                    gc.clearRect(0,0,800,500);
                    gameStarted = false;
                    receiveIsRunning = false;
                    answered = false;

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            canvas = new Canvas();
                            canvas.setHeight(500);
                            canvas.setWidth(800);
                            display.setLeft(canvas);
                        }
                    });
                }
            }
        };

        goReceiveDraw = new Thread(receiveDraw);
        goReceiveDraw.start();
    }

    /*
    This function will run if you answered the math question first. It will set your canvas to be
    able for you to draw on it. First you have to input the answer of your drawing to the UI popped up
    When there is a mouse dragged event, then it will connect to the server, and send coordinates. 
    @Param -
    @return - 
    */
    public void youDraw() throws IOException{
        receiveIsRunning = true;
        youDraww = true;
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

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    Socket socketDraw = new Socket(hostName, 2027);
                    PrintWriter out = new PrintWriter(socketDraw.getOutputStream());
                    gc.fillOval(event.getX()-2,event.getY()-2,5,5);
                    double x = event.getX()-2;
                    double y = event.getY()-2;
                    String coordinate = (x + "," + y);
                    out.println(coordinate);
                    out.flush();
                    socketDraw.close();
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
    }
}
