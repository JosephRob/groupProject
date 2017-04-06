package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    boolean gameStarted = false;
    boolean check = false;
    boolean check2 = false;
    boolean answered = false;
    boolean playagain = false;
    String historyOfDraw = "";
    String realAnswer = "";
    String historyOfText = "";
    String winner;
    Thread updtChat;
    Thread recDraw;
    Thread snDraw;
    int answerToStart = 1000000000;
    int numOfPlayer = 0;
    int numbOfStart = 0;
    int run = 0;
    ServerSocket inputAnswerSocket;
    ServerSocket sendTheDraw;
    ServerSocket inputDraw;

    @Override
    public void start(Stage primaryStage) throws Exception{
        ServerSocket serverSocket = new ServerSocket(1997); //Joining Socket
        ServerSocket chatSocket = new ServerSocket(9053);  //Chat Socket
        inputAnswerSocket = new ServerSocket(2026);//Input Answer and Receive Draw
        sendTheDraw = new ServerSocket(1904); //Socket to Send Draw To Client
        inputDraw = new ServerSocket(2027);
        Runnable takingPeople = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Socket client = serverSocket.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String sent = br.readLine();
                        historyOfText += sent + "\n";
                        client.close();
                        numOfPlayer++;
                        if (numOfPlayer == 2){
                            startGame();
                            run++;
                        }
                    } catch (IOException ex){

                    }
                }
            }
        };

        Thread ppl = new Thread(takingPeople);
        ppl.start();

        Runnable receiveDraw = new Runnable() {
            @Override
            public void run() {
                try {
                    while(answered == false){
                        Socket clientDr = inputDraw.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(clientDr.getInputStream()));
                        String pos = br.readLine();
                        historyOfDraw += pos + "%";
                    }
                    answered = false;
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        };

        recDraw = new Thread(receiveDraw);

        Runnable sendDraw = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        while (true){
                            Socket sck = sendTheDraw.accept();
                            PrintWriter out = new PrintWriter(sck.getOutputStream());
                            out.println(historyOfDraw);
                            out.flush();
                            sck.close();
                            Thread.sleep(100);
                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        snDraw = new Thread(sendDraw);

        Runnable chat = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        int x = 0;
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
                                    historyOfText += "\n\t\t\t  " + username + "'s About To Draw!\n";
                                    out.println(historyOfText);
                                    out.println("yesYouDraw");
                                    check = true;
                                } else if (line.equals(realAnswer)){
                                    answered = true;
                                    historyOfText += "\n" + username + ":\t" + line + "\n";
                                    historyOfText += "\n\t\t\t" + username + " HAS WON THE GAME!" + "\n";
                                    gameStarted = false;
                                    playagain = true;
                                    winner = username;
                                    historyOfText += "ENTER 'START' TO PLAY AGAIN (MIN. 2 PLAYERS TO START)\n";
                                } else if (line.equals("start")){
                                    if (playagain == true){
                                        numbOfStart++;
                                        historyOfText += "\n" + username + ":\t" + line + "\n";
                                        if(numbOfStart == 2){
                                            historyOfDraw = "";
                                            playagain = false;
                                            numbOfStart = 0;
                                            check2 = true;
                                        }
                                    }
                                } else {
                                    historyOfText += "\n" + username + ":\t" + line;
                                    out.println(historyOfText);
                                    out.println("noYouReceive");
                                }
                            } else {
                                out.println(historyOfText);
                                if (gameStarted == true){
                                    out.println("gameIsStarted");
                                } else {
                                    out.println("gameIsNotStarted");
                                }
                            }

                            if (answered == true){
                                out.println("gameIsAnswered");
                                out.println("winneris," + winner);
                                if (x > numOfPlayer){
                                    answered = false;
                                    x = 0;
                                }
                                x++;
                            } else {
                                out.println("gameStillNotAnswered");
                                out.println("stillNoWinner");
                            }
                            out.flush();
                            socket.close();
                            if (check == true){
                                answerInput();
                                if (run == 1){
                                    recDraw.start();
                                    snDraw.start();
                                }
                                check = false;
                            }
                            if (check2 == true){
                                answered = false;
                                historyOfText = "";
                                startGame();
                                check2 = false;
                                run++;
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

    public void startGame(){
        long mTime = System.currentTimeMillis();
        long end = mTime + 1500;
        while(mTime < end){
            mTime = System.currentTimeMillis();
        }
        historyOfText += "GAME STARTS IN ->  3\t";
        mTime = System.currentTimeMillis();
        end = mTime + 1000;
        while(mTime < end){
            mTime = System.currentTimeMillis();
        }
        historyOfText += "\t->\t2\t";
        mTime = System.currentTimeMillis();
        end = mTime + 1000;
        while(mTime < end){
            mTime = System.currentTimeMillis();
        }
        historyOfText += "\t->\t1";
        mTime = System.currentTimeMillis();
        end = mTime + 1000;
        while(mTime < end){
            mTime = System.currentTimeMillis();
        }
        historyOfText += "\n\t\t\t\tENJOY!!\n";
        createNumb();
        gameStarted = true;
    }

    public void createNumb(){
        Random rand = new Random();
        int numb1 = rand.nextInt(100)+1;
        int numb2 = rand.nextInt(100)+1;
        int operator = rand.nextInt(3)+1;
        if (operator == 1){
            answerToStart = numb1 + numb2;
            String question = numb1 + " + " + numb2;
            historyOfText += "\n\t\t   ANSWER QUESTION BELOW\n\t\t\t\t  " + question + "\n TYPE JOIN BEFORE ANSWERING!";
        } else if (operator == 2){
            answerToStart = numb1 - numb2;
            String question = numb1 + " - " + numb2;
            historyOfText += "\n\t\t   ANSWER QUESTION BELOW\n\t\t\t\t  " + question + "\n TYPE JOIN BEFORE ANSWERING!";
        } else {
            answerToStart = numb1 * numb2;
            String question = numb1 + " x " + numb2;
            historyOfText += "\n\t\t   ANSWER QUESTION BELOW\n\t\t\t\t  " + question + "\n TYPE JOIN BEFORE ANSWERING!";
        }
    }

    public void answerInput(){
        try{
            Socket openForAnswer = inputAnswerSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(openForAnswer.getInputStream()));
            realAnswer = br.readLine();
            char answerInChar[] = realAnswer.toCharArray();
            historyOfText += "\n" + answerInChar.length + " characters, begins with " +
                    Character.toUpperCase(answerInChar[0]) + " ends with " +
                    Character.toUpperCase(answerInChar[answerInChar.length-1]) + "\n";
            openForAnswer.close();
        } catch (IOException ex){

        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}