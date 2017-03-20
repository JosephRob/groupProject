package sample;

import javafx.scene.control.*;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    Label result;
    Button[][] tiles;

    public ticTacToeClient(final int  port, String IPA, String username){
        MenuBar top=new MenuBar();
        Menu file=new Menu("file");
        MenuItem restart=new MenuItem("restart");
        restart.setAccelerator(KeyCombination.keyCombination("Ctrl+r"));
        MenuItem quit=new MenuItem("quit");
        quit.setAccelerator(KeyCombination.keyCombination("Ctrl+q"));
        file.getItems().addAll(restart,quit);
        top.getMenus().add(file);

        terminate=false;
        toX=-1;
        toY=-1;
        this.port=port;
        this.IP=IPA;
        this.username=username;
        GridPane base=new GridPane();
        tiles=new Button[3][3];
        for (int x=0;x<3;x++){
            for(int y=0;y<3;y++){
                tiles[x][y]=new Button("\n \n\n");

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
        result=new Label();
        root.setTop(top);
        root.setBottom(result);
        final Scene scene=new Scene(root,400,400);

        final Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();

        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.hide();
                stage.close();
                terminate=true;
            }
        });
        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Socket socket = new Socket(IP, port);

                    PrintWriter out=new PrintWriter(socket.getOutputStream());

                    out.println(-1);
                    out.flush();

                    socket.close();
                }catch (java.io.IOException e){System.out.println(e);}
                System.out.println("restart");
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
                Thread.sleep(50);
                Socket socket=new Socket(IP,port);
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out=new PrintWriter(socket.getOutputStream());

                out.println(1);
                out.println(username);
                out.flush();

                if (Integer.parseInt(br.readLine())==1) {

                    if (over()){
                        toX=-1;
                        toY=-1;
                    }
                    out.println(toX);
                    out.println(toY);
                    /*if(toX!=-1) {
                        System.out.print(toX+"\t");
                        System.out.println(toY);
                    }*/
                    out.flush();


                    toX = -1;
                    toY = -1;
                    final char[][] tempP = new char[3][3];

                    String a=br.readLine();
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            final char icon = br.readLine().charAt(0);
                            if (icon != 'e') {
                                tiles[x][y].setDisable(true);
                                tempP[x][y] = icon;
                            } else {
                                tempP[x][y] = ' ';
                            }
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for (int x = 0; x < 3; x++)
                                for (int y = 0; y < 3; y++) {
                                    tiles[x][y].setText("\n" + (tempP[x][y]) + "\n");
                                }
                        }
                    });
                }

                socket.close();
            }catch (java.io.IOException e){System.out.println(e);}
            catch (java.lang.InterruptedException e){System.out.println(e);}
        }
    }
    public boolean over(){
        char[][]board=new char[3][3];
        for (int x=0;x<3;x++)
            for (int y=0;y<3;y++) {
                board[x][y] = tiles[x][y].getText().charAt(1);
                //System.out.println(x+":"+y+":"+board[x][y]);
            }
        if (board[0][0]==board[1][1] && board[1][1]==board[2][2]){
            if (board[0][0]!=' ') {
                final char winner=board[0][0];
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        result.setText("a:"+winner + " wins");
                    }
                });
                return true;
            }
        }
        else if(board[2][0]==board[1][1] && board[1][1]==board[0][2]){
            if (board[2][0]!=' ') {
                final char winner=board[2][0];
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(winner + " wins");
                    }
                });
                return true;
            }
        }
        else {for (int x=0;x<3;x++){
            if (board[x][0]==board[x][1] && board[x][1]==board[x][2]){
                if (board[x][0]!=' ') {
                    final char winner=board[x][0];
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            result.setText(winner + " wins");
                        }
                    });
                    return true;
                }
            }
            if (board[0][x]==board[1][x] && board[1][x]==board[2][x]){
                if (board[0][x]!=' ') {
                    final char winner=board[0][x];
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            result.setText(winner + " wins");
                        }
                    });
                    return true;
                }
            }
        }}
        if (board[0][0]==board[0][1]&&board[0][0]==board[0][2]&&board[0][0]==board[1][0]&&board[0][0]==board[1][1]&&board[0][0]==board[1][2]&&board[0][0]==board[2][0]&&board[0][0]==board[2][1]&&board[0][0]==board[2][2]){
            if (board[0][0]!=' ') {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        result.setText("tie");
                    }
                });
                return true;
            }
        }

        return false;
    }
}
