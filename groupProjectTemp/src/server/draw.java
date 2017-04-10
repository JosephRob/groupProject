//Joseph Robertson
//Justin Duong
//Clyve Widjaya
/*
Clyve Widjaya
This File is for the server side of Draw game!
*/
package server;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/*
This is the main class for draw side of Draw game!
Thread of this class will be created from the main server file
and the runnable thread will be the chat part of the game
*/
public class draw implements Runnable{
    boolean gameStarted;
    boolean check;
    boolean check2;
    boolean answered;
    boolean playagain;
    String historyOfDraw;
    String realAnswer;
    String historyOfText;
    String winner;
    Thread updtChat;
    Thread recDraw;
    Thread snDraw;
    int answerToStart;
    int numOfPlayer;
    int numbOfStart;
    int run;
    ServerSocket inputAnswerSocket;
    ServerSocket sendTheDraw;
    ServerSocket inputDraw;
    ServerSocket chatSocket;

    /*
    This is the constructor class of the draw class
    It will open serversocket for multiple methods such as chat, receive draw,
    send draw, input draw. Once this function called, it will run a runnable which will
    repeatedly accept client connection using the port given from main server. This function
    also have 2 more runnable thread, receiveDraw, and sendDraw. ReceiveDraw will receive
    coordinates from drawer UI, and save it. sendDraw will send coordinates to clients that 
    are set to receive draw
    @Param int port
    @return - 
    */
    public draw (int port){
        gameStarted = false;
        check = false;
        check2 = false;
        answered = false;
        playagain = false;
        historyOfDraw = "";
        historyOfText = "";
        realAnswer = "";
        winner = "";
        answerToStart = 1000000000;
        numbOfStart = 0;
        numOfPlayer = 0;
        run = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(port); //Joining Socket
            chatSocket = new ServerSocket(9053);  //Chat Socket
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
			    if (sent.contains(" has left the game :(")){
				numOfPlayer--;
			    } else if (sent.contains(" has joined the game!")){
				numOfPlayer++;
			    } 
                            if (numOfPlayer == 2){
                                startGame();
                                run++;
                            }
			    if(numOfPlayer == 0){
				historyOfText = "";
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
        } catch (IOException ex){
                ex.printStackTrace();
        }
    }

    /*
    This is the runnable chat function, this will mostly control the game. This will receive and 
    send new inputs from client chat and send it to all client. The function will also be the way 
    of communicating between server and each client.
    @Param -
    @return - 
    */
    public void run(){
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
                            historyOfText += "\n\t\t\t\t" + username + "'s About To Draw!\n";
                            out.println(historyOfText);
                            out.println("yesYouDraw");
                            check = true;
                        } else if (line.equals(realAnswer)){
                            answered = true;
                            historyOfText += "\n" + username + ":\t" + line + "\n";
                            historyOfText += "\n\t\t\t   " + username.toUpperCase() + " HAS WON THE GAME!" + "\n";
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
			if (numOfPlayer >= 2){
			    startGame();
			    check2 = false;
                            run++;
			}
                    }
                }
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }

    /*
    This function will be called if there are 2 or more people joining the draw game.
    Once called, it will call createNumb function to create the math question and 
    set the gameStarted to be true
    @Param -
    @return - 
    */
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
        historyOfText += "\n\t\t\t\t           ENJOY!!\n";
        createNumb();
        gameStarted = true;
    }

    /*
    This function will be called to create a question for users to answer 
    in order to be able to draw. It will also create the answer, obviously. 
    @Param -
    @return - 
    */
    public void createNumb(){
        Random rand = new Random();
        int numb1 = rand.nextInt(100)+1;
        int numb2 = rand.nextInt(100)+1;
        int operator = rand.nextInt(3)+1;
        if (operator == 1){
            answerToStart = numb1 + numb2;
            String question = numb1 + " + " + numb2; 
            historyOfText += "\n\t\t\t    ANSWER QUESTION BELOW\n\t\t\t\t\t    " + question + "\n\t\t\t TYPE JOIN BEFORE ANSWERING!\n";
        } else if (operator == 2){
            answerToStart = numb1 - numb2;
            String question = numb1 + " - " + numb2;
            historyOfText += "\n\t\t\t    ANSWER QUESTION BELOW\n\t\t\t\t\t    " + question + "\n\t\t\t TYPE JOIN BEFORE ANSWERING!\n";
        } else {
            answerToStart = numb1 * numb2;
            String question = numb1 + " x " + numb2;
            historyOfText += "\n\t\t\t    ANSWER QUESTION BELOW\n\t\t\t\t\t    " + question + "\n\t\t\t TYPE JOIN BEFORE ANSWERING!\n";
        }
    }

    /*
    This function will be called if a client answered the math question. They got the chance to draw,
    but before drawing, client will have to input the answer of their drawing to the server. This function
    will open a server and receive the answer and saved it.
    @Param -
    @return - 
    */
    public void answerInput(){
        try{
            Socket openForAnswer = inputAnswerSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(openForAnswer.getInputStream()));
            realAnswer = br.readLine();
            char answerInChar[] = realAnswer.toCharArray();
            historyOfText += "\n\t\t\t" + answerInChar.length + " characters, begins with " +
                    Character.toUpperCase(answerInChar[0]) + " ends with " +
                    Character.toUpperCase(answerInChar[answerInChar.length-1]) + "\n";
            openForAnswer.close();
        } catch (IOException ex){

        }
    }
}
