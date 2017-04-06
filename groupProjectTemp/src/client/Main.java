package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Main extends Application {
    static String IP="";
    static int userCode=-1;
    static TableView<Location> list;
    static String username;
    @Override
    public void start(final Stage primaryStage) throws Exception{
        primaryStage.setTitle("login");

        GridPane login=new GridPane();
        Label namePrompt=new Label("Username");
        Label passwordPrompt=new Label("Password");
        Label IPPrompt=new Label("IP adress");
        final Label answer=new Label();
        final TextField nameBox=new TextField();
        final TextField IPBox=new TextField("localHost");//temporary staring value to make testing easier change per server
        nameBox.setPromptText("username");
        IPBox.setPromptText("IP adress");
        final PasswordField passwordBox=new PasswordField();
        passwordBox.setPromptText("password");

        Button loginButton=new Button("Login");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    IP=IPBox.getText();
                    String name = nameBox.getText();
                    String password = passwordBox.getText();
                    Socket socket = new Socket(IP, 1111);
                    PrintWriter out=new PrintWriter(socket.getOutputStream());
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.println(0);
                    out.flush();
                    if (Integer.parseInt(br.readLine())==1){
                        out.println(name);
                        username=name;
                        out.println(password);
                        out.flush();
                        String line=br.readLine();

                        if(Integer.parseInt(line)==1){
                            userCode=Integer.parseInt(br.readLine());
                            //System.out.println(userCode);
                            answer.setText("");
                            bringList();
                            primaryStage.hide();
                        }
                        else answer.setText("invalid password\n or username");
                    }
                    socket.close();
                }
                catch (java.io.IOException e){
                    System.err.println(e);
                }
            }
        });
        Button registerButton=new Button("Register");
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    IP=IPBox.getText();
                    String name = nameBox.getText();
                    username=name;
                    String password = passwordBox.getText();
                    Socket socket = new Socket(IP, 1111);
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out=new PrintWriter(socket.getOutputStream());
                    out.println(1);
                    out.flush();
                    if (Integer.parseInt(br.readLine())==1) {
                        out.println(name);
                        out.println(password);
                        out.flush();
                        if (Integer.parseInt(br.readLine()) == 1) {
                            userCode = Integer.parseInt(br.readLine());
                            answer.setText("");
                            bringList();
                            primaryStage.hide();
                        }
                        else answer.setText("user already exists");
                    }
                    else answer.setText("invalid");
                    socket.close();
                }
                catch (java.io.IOException e){
                    System.err.println(e);
                }

            }
        });

        login.add(namePrompt,0,0);
        login.add(passwordPrompt,0,1);
        login.add(IPPrompt,0,2);
        login.add(nameBox,1,0);
        login.add(passwordBox,1,1);
        login.add(IPBox,1,2);
        login.add(loginButton,0,4);
        login.add(registerButton,1,4);
        login.add(answer,3,0);

        Scene base=new Scene(login,400,100);


        primaryStage.setScene(base);

        primaryStage.show();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                //System.out.println("logout");
                try{
                    if (userCode!=-1) {
                        Socket socket = new Socket(IP, 9999);
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        out.println(userCode);
                        out.flush();
                        //System.out.println(userCode);
                        socket.close();
                    }
                }catch (java.io.IOException e){}
                System.out.println("exit");
            }
        }, "Shutdown-thread"));
    }
    private void bringList(){
        final ObservableList<Location> available= FXCollections.observableArrayList();
        Runnable update=new Runnable() {
            @Override
            public void run() {
                while(true)try {
                    ObservableList<Location> temp= FXCollections.observableArrayList();
                    Thread.sleep(200);

                    Socket socket = new Socket(IP, 1112);
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out=new PrintWriter(socket.getOutputStream());

                    out.println(userCode);
                    out.flush();

                    if(Integer.parseInt(br.readLine())==1) {

                        String port;
                        while ((port = br.readLine()) != null) {
                            String name = br.readLine();
                            //System.out.println(port+"\t"+name);

                            temp.add(new Location(Integer.parseInt(port), name));
                        }
                    }
                    socket.close();
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            available.clear();

                    available.addAll(temp);
                        }});
                }
                catch (Exception e){}
            }
        };

        Thread updatelist=new Thread(update);
        updatelist.start();

        list=new TableView<>();
        TableColumn<Location,String> locationNameTableColumn=new TableColumn<>("Name");
        locationNameTableColumn.setCellValueFactory(new PropertyValueFactory<Location, String>("name"));

        TableColumn<Location,Integer> locationPortTableColumn=new TableColumn<>("Port");
        locationPortTableColumn.setCellValueFactory(new PropertyValueFactory<Location, Integer>("port"));

        list.setItems(available);
        list.getColumns().addAll(locationNameTableColumn,locationPortTableColumn);

        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                //Check whether item is selected and set value of selected item to Label
                try {
                    if (list.getSelectionModel().getSelectedItem() != null) {
                        TableView.TableViewSelectionModel selectionModel = list.getSelectionModel();
                        Location selectedCells = (Location) selectionModel.getSelectedItem();

                        String name = selectedCells.getName();
                        name = name.replaceAll("\\d", "");
                        //System.out.println(name);
                        switch (name) {
                            case "chat":
                                //System.out.println("open chat");
                                Thread chat = new Thread(new chatClient(selectedCells.getPort(), IP, username));
                                chat.start();
                                break;
                            case "room":
                                Thread room=new Thread(new roomClient(selectedCells.getPort(), IP, username));
                                room.start();
                                break;
                            case "tictactoe":
                                Thread tictactoe=new Thread(new ticTacToeClient(selectedCells.getPort(), IP, username));
                                tictactoe.start();
                                break;
                            case "agar":
                                Thread agar=new Thread(new agarClient(selectedCells.getPort(), IP, username));
                                agar.start();
                                break;
                            case "draw":
                                Thread draw = new Thread(new drawClient(selectedCells.getPort(), IP, username));
                                draw.start();
                                break;
                            default:
                                break;
                        }
                    }
                }
                catch (Exception e){System.out.println(e);}
            }
        });

        Button exit=new Button("exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        BorderPane base=new BorderPane();
        base.setBottom(exit);
        base.setCenter(list);

        Stage stage=new Stage();
        stage.setScene(new Scene(base));
        stage.setTitle(username);

        stage.show();
    }



    public static void main(String[] args) {
        if (args.length>0){
            IP=args[0];
        }
        launch(args);
    }
}
