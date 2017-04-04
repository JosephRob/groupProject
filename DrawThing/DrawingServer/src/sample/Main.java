package sample;

import com.sun.corba.se.spi.activation.Server;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    boolean gameStarted = false;
    boolean check = false;
    boolean answered = false;
    int answerToStart = 1000000000;
    String realAnswer = "";
    ArrayList<String> players;
    String historyOfText = "";
    Thread updtChat;
    int numOfPlayer = 1;
    String historyOfDraw = "";
    ServerSocket inputAnswerSocket;

    @Override
    public void start(Stage primaryStage) throws Exception{
        ServerSocket serverSocket = new ServerSocket(1997);
        ServerSocket chatSocket = new ServerSocket(9053);
        inputAnswerSocket = new ServerSocket(2026);
        Runnable takingPeople = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Socket client = serverSocket.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String name = br.readLine();
                        String sent = br.readLine();
                        historyOfText += sent + "\n";
                        client.close();
                        //Thread.sleep(1000);
                        if (numOfPlayer == 2){
                            Thread.sleep(1500);
                            historyOfText += "GAME STARTS IN ->  3\t";
                            updtChat.sleep(1000);
                            historyOfText += "\t->  2\t";
                            updtChat.sleep(1000);
                            historyOfText += "\t ->  1";
                            updtChat.sleep(1000);
                            historyOfText += "\n\t\t\t\t  ENJOY!!\n";
                            createNumb();
                            gameStarted = true;
                        }
                        System.out.println(numOfPlayer);
                        numOfPlayer++;
                    } catch (IOException ex){

                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            }
        };

        Thread ppl = new Thread(takingPeople);
        ppl.start();

        Runnable chat = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        while (true){
                            Socket socket = chatSocket.accept();
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintWriter out = new PrintWriter(socket.getOutputStream());

                            String username = br.readLine();
                            String line = br.readLine();

                            if (!(line.equals(" "))) {
                                if (line.equals(Integer.toString(answerToStart))){
                                    answerToStart = 1000000000;
                                    historyOfText += "\n" + username + ":\t" + line;
                                    historyOfText += "\n\t\t\t\t" + username + "'s About To Draw!\n";
                                    //System.out.println(line);
                                    out.println(historyOfText);
                                    out.println("yesYouDraw");
                                    System.out.println("cp1");
                                    check = true;
                                    System.out.println("cp2");
                                } else {
                                    historyOfText += "\n" + username + ":\t" + line;
                                    //System.out.println(historyOfText + " 1");
                                    out.println(historyOfText);
                                    //System.out.println(historyOfText);
                                    out.println("noYouReceive");
                                }
                            } else {
                                out.println(historyOfText);
                            }

                            if (gameStarted == true){
                                out.println("gameIsStarted");
                            } else {
                                out.println("gameIsNotStarted");
                            }

                            out.flush();
                            socket.close();
                            if (check == true){
                                answerInput();
                                check = false;
                            }
                        }
                    } catch (Exception e){
                        System.out.println(e);
                    }
                }
            }
        };

        updtChat = new Thread(chat);
        updtChat.start();
    }

    public void createNumb(){
        Random rand = new Random();
        int numb1 = rand.nextInt(100)+1;
        int numb2 = rand.nextInt(100)+1;
        int operator = rand.nextInt(3)+1;
        if (operator == 1){
            answerToStart = numb1 + numb2;
            String question = numb1 + " + " + numb2;
            historyOfText += "\n\t\t  ANSWER QUESTION BELOW\n\t\t\t\t  " + question;
        } else if (operator == 2){
            answerToStart = numb1 - numb2;
            String question = numb1 + " - " + numb2;
            historyOfText += "\n\t\t  ANSWER QUESTION BELOW\n\t\t\t\t  " + question;
        } else {
            answerToStart = numb1 * numb2;
            String question = numb1 + " x " + numb2;
            historyOfText += "\n\t\t  ANSWER QUESTION BELOW\n\t\t\t\t  " + question;
        }
    }

    public void answerInput(){
        try{
            Socket openForAnswer = inputAnswerSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(openForAnswer.getInputStream()));
            realAnswer = br.readLine();
            char answerInChar[] = realAnswer.toCharArray();
            historyOfText += answerInChar.length + " characters, begins with " +
                    Character.toUpperCase(answerInChar[0]) + " ends with " +
                    Character.toUpperCase(answerInChar[answerInChar.length-1]);
            openForAnswer.close();
            System.out.println(realAnswer);
        } catch (IOException ex){

        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}