package sample;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by lex on 16/03/17.
 */
public class roomClient implements  Runnable{
    int port;
    String IP,userID;
    public roomClient(int port, String IP, String username){
        this.port=port;
        this.IP=IP;
        this.userID=username;

        GridPane gridPane=new GridPane();

        Scene scene=new Scene(gridPane,100,100);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                        System.out.println("left");
                        break;
                    case RIGHT:
                        System.out.println("right");
                        break;
                    case UP:
                        System.out.println("up");
                        break;
                    case DOWN:
                        System.out.println("down");
                        break;
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case SHIFT:
                        break;
                }
            }
        });

        Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void run() {
        System.out.println("fuk");
    }
}
