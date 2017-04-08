package client;

import javafx.scene.canvas.GraphicsContext;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.canvas.Canvas;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.Random;

/**
 * @author Joseph
 * @date 17/4/8
 */
public class roomClient implements  Runnable{
    int port;
    String IP,userID;
    boolean left,right,up,down;
    int x,y,v,size;
    double[] color;
    boolean terminate;

    @FXML
    private Canvas base;

    /**
     * Sets up input and graphics.
     *
     * @param port
     * @param IP
     * @param username
     */
    public roomClient(final int port, String IP, final String username){
        terminate=false;
        this.port=port;
        this.IP=IP;
        final String tempIP=IP;

        this.userID=username;
        size=50;

        color=new double[3];
        Random random=new Random(System.currentTimeMillis());
        for (int x=0;x<3;x++) {
            color[x] = random.nextDouble();
            //System.out.println(color[x]);
        }
        base=new Canvas();
        base.setWidth(400);
        base.setHeight(base.getWidth());

        x=(int)base.getWidth()/10;
        y=(int)(3*base.getHeight()/4);
        v=0;


        Group root=new Group();
        root.getChildren().add(base);

        final Scene scene=new Scene(root,400,400);

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                        //System.out.println("left");
                        left=false;
                        break;
                    case RIGHT:
                        //System.out.println("right");
                        right=false;
                        break;
                    case UP:
                        //System.out.println("up");
                        up=false;
                        break;
                    case DOWN:
                        //System.out.println("down");
                        down=false;
                        break;
                }
            }
        });

        final Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                        //System.out.println("left");
                        left=true;
                        break;
                    case RIGHT:
                        //System.out.println("right");
                        right=true;
                        break;
                    case UP:
                        //System.out.println("up");
                        up=true;
                        break;
                    case DOWN:
                        //System.out.println("down");
                        down=true;
                        break;
                    case Q:
                        stage.hide();
                        stage.close();
                        break;
                }
            }
        });

        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    Socket socket = new Socket(tempIP, port);
                    PrintWriter out=new PrintWriter(socket.getOutputStream());
                    out.println("0\n"+username);
                    out.flush();
                    socket.close();
                    System.out.println("exit complete");
                }
                catch (java.io.IOException e){System.out.println("a"+e);}
                terminate=true;
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                //System.out.println("logout");
                terminate=true;
            }
        }, "Shutdown-thread"));
    }

    /**
     * Sends other player info to server, get other players from server, and displays players and their names.
     */
    @Override
    public void run() {
        while (true) {try {
            while(true) {
                Thread.sleep(20);

                if (terminate){
                    Thread.currentThread().interrupt();
                    return;
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (! terminate) {
                            String otherUser;
                            final GraphicsContext gc = base.getGraphicsContext2D();
                            try {
                                final Socket socket = new Socket(IP, port);
                                PrintWriter out = new PrintWriter(socket.getOutputStream());
                                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                out.println(1);
                                out.println(userID);
                                out.println(x);
                                out.println(y);
                                for (int x = 0; x < 3; x++)
                                    out.println(color[x]);

                                out.flush();

                                final Integer[] otherPlace = new Integer[2];
                                final Double[] otherColor = new Double[3];

                                drawBack(gc);
                                while ((otherUser = br.readLine()) != null) {
                                    for (int x = 0; x < 2; x++)
                                        otherPlace[x] = Integer.parseInt(br.readLine());
                                    for (int x = 0; x < 3; x++)
                                        otherColor[x] = Double.parseDouble(br.readLine());
                                    gc.setFill(new Color(otherColor[0], otherColor[1], otherColor[2], 1));
                                    gc.fillRect(otherPlace[0] - size, otherPlace[1] - size, size, size);
                                    gc.strokeText(otherUser, otherPlace[0] - size, otherPlace[1] + 10);
                                }
                                socket.close();
                            } catch (java.io.IOException e) {
                                System.out.println("b"+e);
                            }
                        }
                    }
                });


                if (up) {
                    if (y==(int)(3*base.getHeight()/4))
                        v=-100;
                }
                v=v+4;
                y = y + (int)(((double)v)/10);
                if (down) {
                    Random random=new Random(System.currentTimeMillis());
                    for (int x=0;x<3;x++) {
                        color[x] = random.nextDouble();
                        //System.out.println(color[x]);
                    }
                }
                if (left) {
                    x = x - 5;
                }
                if (right) {
                    x = x + 5;
                }
                if (x<size)
                    x=size;
                else if (x>base.getWidth())
                    x=(int)base.getWidth();
                if (y<size)
                    y=size;
                else if(y>(3*base.getHeight()/4))
                    y=(int)(3*base.getHeight()/4);

            }
        }
        catch (Exception e){System.err.println("a"+e);}}
    }

    /**
     * Draws the background of the room.
     *
     * @param gc
     */
    private void drawBack(GraphicsContext gc){
        gc.setFill(Color.DEEPSKYBLUE);
        gc.fillRect(0,0,base.getWidth(),base.getHeight());
        gc.setFill(Color.LAWNGREEN);
        gc.fillRect(0,3*base.getHeight()/4,base.getWidth(),base.getHeight());
        gc.setFill(Color.YELLOW);
        gc.fillOval(base.getWidth()*3/4,base.getHeight()/4,size,size);
    }
}
