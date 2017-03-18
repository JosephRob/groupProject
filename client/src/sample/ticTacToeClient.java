package sample;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by lex on 18/03/17.
 */
public class ticTacToeClient implements Runnable {
    int port;
    boolean terminate;
    String IP,username;
    int toX,toY;
    Button[][] tiles;
    char me;

    public ticTacToeClient(final int  port, String IP, String username){
        terminate=false;
        me='A';
        toX=-1;
        toY=-1;
        this.port=port;
        this.IP=IP;
        this.username=username;
        GridPane base=new GridPane();
        tiles=new Button[3][3];
        for (int x=0;x<3;x++){
            for(int y=0;y<3;y++){
                tiles[x][y]=new Button("\n\n\n");

                tiles[x][y].setMinWidth(60);
                final int tempx=x,tempy=y;
                tiles[x][y].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //System.out.println(tempx+"\t"+tempy);
                        toX=tempx;
                        toY=tempy;
                    }
                });
                base.add(tiles[x][y],x,y);
            }
        }
        BorderPane root=new BorderPane();
        root.setCenter(base);
        Button exit=new Button("exit");
        root.setTop(exit);
        final Scene scene=new Scene(root,400,400);

        final Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.hide();
                stage.close();
                terminate=true;
                Thread.currentThread().interrupt();
            }
        });

    }
    @Override
    public void run() {
        while (true){
            if (terminate){
                Thread.currentThread().interrupt();
                return;
            }
            try{
                Socket socket=new Socket(IP,port);
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out=new PrintWriter(socket.getOutputStream());

                out.println(username);
                out.flush();

                out.println(toX);
                out.println(toY);
                out.println(me);
                out.flush();
                //System.out.println("fasdfads");

                if (toX!=-1 && toY!=-1)
                    tiles[toX][toY].setDisable(true);

                toX=-1;
                toY=-1;

                for (int x=0;x<3;x++)
                    for (int y=0;y<3;y++) {
                        final char icon=br.readLine().charAt(0);
                        final int tempx=x;
                        final int tempy=y;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                tiles[tempx][tempy].setText("\n\n");
                                if (!(icon!='e'))
                                    tiles[tempx][tempy].setText("\n" + icon + "\n");
                            }
                        });
                    }



                socket.close();
            }catch (java.io.IOException e){System.out.println(e);}
        }
    }
}
