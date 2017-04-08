package server;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    private static HashMap<String,String> Users=new HashMap<>();
    private static HashMap<Integer,String> UserIDs=new HashMap<>();
    private static HashMap<Integer,String> serverMap=new HashMap<>();
    private static HashMap<String, Thread> threads=new HashMap<>();
    private static int passwordKey=12345;
    static int joinPort=1111,
            listPort=1112;
    static int keyCode=0;
    public static void main(String[] args) {
        try {
            String adressName = "";
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    //System.out.println(i.getHostAddress());
                    if ((i.getHostAddress() + "").charAt(0) != '0') {
                        adressName = ((InetAddress) ee.nextElement()).getHostAddress() + "";
                        break;
                    }
                }
                if (adressName != "") break;
            }
            System.out.println(adressName);
        }
        catch (Exception e){System.out.println("localHost");}



        keyCode = new Random().nextInt(10);
        Users=new Main().setUsers();

        Runnable logout=new Runnable() {
            @Override
            public void run() {
                while (true){try {
                    Thread.sleep(100);
                    ServerSocket serverSocket=new ServerSocket(9999);
                    while (true){
                        Socket socket=serverSocket.accept();
                        BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        int id=Integer.parseInt(br.readLine());
                        if (UserIDs.containsKey(id)) {

                            UserIDs.remove(id);

                        }
                        socket.close();
                    }
                }catch (Exception e){
                    System.err.println(e);
                }}
            }
        };
        Runnable login=new Runnable() {
            @Override
            public void run() {
                while (true)try {
                    Thread.sleep(10);
                    ServerSocket serverSocket = new ServerSocket(joinPort);
                    while (true) {
                        Socket socket=serverSocket.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out=new PrintWriter(socket.getOutputStream());
                        int type=2;
                        try{
                            type=Integer.parseInt(br.readLine());
                        }catch (Exception e){out.println(0);}

                        if (type==0){out.println(1);
                            out.flush();
                            String name=br.readLine(),
                                    password=br.readLine();
                            //System.out.println(name);
                            boolean good=true;
                            for (Map.Entry item:UserIDs.entrySet()){
                                //System.out.print(item.getValue()+" ");
                                if (item.getValue().equals(name)) {
                                    good=false;
                                }
                            }
                            if (good && Users.containsKey(name) && password.equals(Users.get(name))){
                                out.println(1);
                                out.flush();
                                keyCode+= new Random().nextInt(10)+1;
                                out.println(keyCode);
                                out.flush();
                                UserIDs.put(keyCode,name);
                            }
                            else out.println(0);
                            out.flush();
                        }
                        else if (type==1){out.println(1);
                            out.flush();
                            String name=br.readLine(),
                                    password=br.readLine();
                            //System.out.println(name);
                            if(Users.containsKey(name)){
                                out.println(0);//name already exists
                            }
                            else {
                                out.println(1);
                                Users.put(name, password);
                                keyCode += new Random().nextInt(10)+1;
                                UserIDs.put(keyCode, name);
                                new Main().printInCode();
                                out.println(keyCode);
                            }
                            out.flush();
                        }
                        else {out.println(0);
                            continue;
                        }

                        socket.close();
                    }
                }
                catch (Exception e){
                    System.err.println(e);
                }
            }
        };
        Runnable listings = new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        ServerSocket serverSocket=new ServerSocket(listPort);
                        while (true){
                            Thread.sleep(100);
                            Socket socket=serverSocket.accept();
                            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintWriter out=new PrintWriter(socket.getOutputStream());

                            if(UserIDs.containsKey(Integer.parseInt(br.readLine()))){
                                out.println(1);
                            }
                            else {
                                out.println(0);
                            }
                            out.flush();

                            Iterator it=serverMap.entrySet().iterator();
                            while (it.hasNext()){
                                Map.Entry item=(Map.Entry)it.next();
                                out.println(item.getKey()+"\n"+item.getValue());
                                out.flush();
                            }

                            socket.close();
                        }
                    }
                    catch (Exception e){}
                }
            }
        };

        Thread loginThread=new Thread(login);
        loginThread.start();
        Thread logoutThread=new Thread(logout);
        logoutThread.start();
        Thread list=new Thread(listings);
        list.start();
        Thread maintain=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
                        String input = br.readLine();
                        String command=input.split(" ")[0];
                        String selection;
                        switch (command){
                            case "add":
                                selection=input.split(" ")[1];
                                if (selection.equals("room")){
                                    //add room
                                    ServerSocket a=new ServerSocket(0);
                                    int serverstart=a.getLocalPort();
                                    a.close();
                                    Thread defaultRoom=new Thread(new room(serverstart));
                                    defaultRoom.start();
                                    serverMap.put(serverstart,"room"+serverstart);
                                    threads.put("room"+serverstart,defaultRoom);
                                    System.out.println("\tadded room"+serverstart);
                                }
                                else if (selection.equals("chat")){
                                    //add chat
                                    ServerSocket a=new ServerSocket(0);
                                    int serverstart=a.getLocalPort();
                                    a.close();
                                    Thread defaultChat=new Thread(new chat(serverstart));
                                    defaultChat.start();
                                    serverMap.put(serverstart,"chat"+serverstart);
                                    threads.put("chat"+serverstart,defaultChat);
                                    System.out.println("\tadded chat"+serverstart);
                                }
                                else if (selection.equals("ticTacToe")){
                                    //add ticTacToe
                                    ServerSocket a=new ServerSocket(0);
                                    int serverstart=a.getLocalPort();
                                    a.close();
                                    Thread defaultTictactoe=new Thread(new ticTacToe(serverstart));
                                    defaultTictactoe.start();
                                    serverMap.put(serverstart,"ticTacToe"+serverstart);
                                    threads.put("ticTacToe"+serverstart,defaultTictactoe);
                                    System.out.println("\tadded ticTacToe"+serverstart);
                                }
                                else if (selection.equals("agar")){
                                    //add agar
                                    ServerSocket a=new ServerSocket(0);
                                    int serverstart=a.getLocalPort();
                                    a.close();
                                    Thread defaultAgar=new Thread(new agar(serverstart));
                                    defaultAgar.start();
                                    serverMap.put(serverstart,"agar"+serverstart);
                                    threads.put("agar"+serverstart,defaultAgar);
                                    System.out.println("\tadded agar"+serverstart);
                                } else if (selection.equals("draw")){
                                    ServerSocket a = new ServerSocket(0);
                                    int serverstart = a.getLocalPort();
                                    a.close();
                                    Thread defaultDraw = new Thread(new draw(serverstart));
                                    defaultDraw.start();
                                    serverMap.put(serverstart, "draw" + serverstart);
                                    threads.put("draw" + serverstart, defaultDraw);
                                    System.out.println("\tadded draw" + serverstart);
                                } else if (selection.equals("shootyGame")){
                                    ServerSocket a = new ServerSocket(0);
                                    int serverstart = a.getLocalPort();
                                    a.close();
                                    Thread shootyGame = new Thread(new ShootyGameServer(serverstart));
                                    shootyGame.start();
                                    serverMap.put(serverstart, "shootyGame" + serverstart);
                                    threads.put("shootyGame" + serverstart, shootyGame);
                                    System.out.println("\tadded shootyGame" + serverstart);
                                }
                                else{System.out.println("Invalid selection");}
                                break;
                            case "remove":
                                selection=input.split(" ")[1]+input.split(" ")[2];
                                int number=Integer.parseInt(input.split(" ")[2]);
                                if (threads.containsKey(selection)){
                                    System.out.println("Warning: shutting down a server while a user is connected will not disconnect the user, but will prevent new users from joining");
                                    System.out.println("\tremoving "+selection);
                                    threads.remove(selection);
                                    serverMap.remove(number);
                                }
                                else {System.out.println("Invalid selection: server does not exist");}
                                break;
                            case "display":
                                for (int part:serverMap.keySet()){
                                    System.out.println("\t"+serverMap.get(part));
                                }
                                break;
                            case "help":
                                System.out.println("To add server: add <serverType>");
                                System.out.println("\texample for abcServer:");
                                System.out.println("\tadd abcServer");
                                System.out.println("To remove server: remove <serverType> <serverNumber>");
                                System.out.println("\texample for abcServer1234:");
                                System.out.println("\tremove abcServer 1234");
                                System.out.println("To display all servers: display");
                                System.out.println("To create demo set of servers: demo");
                                System.out.println("To exit: terminate");
                                break;
                            case "demo":
                                //add one of each
                                //add room
                                ServerSocket a=new ServerSocket(0);
                                int serverstart=a.getLocalPort();
                                a.close();
                                Thread defaultRoom=new Thread(new room(serverstart));
                                defaultRoom.start();
                                serverMap.put(serverstart,"room"+serverstart);
                                threads.put("room"+serverstart,defaultRoom);
                                System.out.println("\tadded room"+serverstart);
                                //add chat
                                a=new ServerSocket(0);
                                serverstart=a.getLocalPort();
                                a.close();
                                Thread defaultChat=new Thread(new chat(serverstart));
                                defaultChat.start();
                                serverMap.put(serverstart,"chat"+serverstart);
                                threads.put("chat"+serverstart,defaultChat);
                                System.out.println("\tadded chat"+serverstart);
                                //add ticTacToe
                                a=new ServerSocket(0);
                                serverstart=a.getLocalPort();
                                a.close();
                                Thread defaultTictactoe=new Thread(new ticTacToe(serverstart));
                                defaultTictactoe.start();
                                serverMap.put(serverstart,"tictactoe"+serverstart);
                                threads.put("tictactoe"+serverstart,defaultTictactoe);
                                System.out.println("\tadded tictactoe"+serverstart);
                                //add agar
                                a=new ServerSocket(0);
                                serverstart=a.getLocalPort();
                                a.close();
                                Thread defaultAgar=new Thread(new agar(serverstart));
                                defaultAgar.start();
                                serverMap.put(serverstart,"agar"+serverstart);
                                threads.put("agar"+serverstart,defaultAgar);
                                System.out.println("\tadded agar"+serverstart);
                                //add draw
                                a = new ServerSocket(0);
                                serverstart = a.getLocalPort();
                                a.close();
                                Thread defaultDraw = new Thread(new draw(serverstart));
                                defaultDraw.start();
                                serverMap.put(serverstart, "draw" + serverstart);
                                threads.put("draw" + serverstart, defaultDraw);
                                System.out.println("\tadded draw" + serverstart);
                                //add shootyGame
                                a = new ServerSocket(0);
                                serverstart = a.getLocalPort();
                                a.close();
                                Thread shootyGameThread = new Thread(new ShootyGameServer(serverstart));
                                shootyGameThread.start();
                                serverMap.put(serverstart, "shootyGame" + serverstart);
                                threads.put("shootyGame" + serverstart, shootyGameThread);
                                System.out.println("\tadded shootyGame" + serverstart);
                                break;
                            case "terminate":
                                System.out.println("exit (Y/N)");
                                if (br.readLine().equalsIgnoreCase("y"))
                                    System.exit(0);
                                else System.out.println("exit aborted");

                                break;
                            default:
                                System.out.println("unrecognised command\n\ttry command: help");
                                break;
                        }
                    }
                    catch (IOException e){
                        System.err.println(e);
                    }
                    catch (Exception e){
                        System.err.println("Invalid command\n\t"+e);
                    }
                }
            }
        });
        maintain.start();
    }
    private HashMap<String,String> setUsers(){
        HashMap<String,String> temp=new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("people.txt"));
            String line;
            while ((line=br.readLine())!=null) {
                char[] name = decode(line);
                char[] password = decode(br.readLine());
                //System.out.println(new String(name)+ "\t" + new String(password));//uncomment to view usernames and passwords in run

                temp.put(new String(name), new String(password));
            }
        }
        catch (FileNotFoundException f){
            try {
                Users.put("admin","password");
                new Main().printInCode();
            }
            catch (Exception e){}
        }
        catch (IOException e){}
        return temp;
    }
    private int[] code(String input){
        int[] output=new int[input.length()];
        Random encode=new Random(passwordKey);
        for (int x=0;x<input.length();x++){
            output[x]=input.getBytes()[x]+encode.nextInt();
        }
        return output;
    }
    private char[] decode(String input){
        if (input.length()==0)return null;
        char[] output=new char[input.split(",").length];
        Random encode=new Random(passwordKey);
        for (int x=0;x<input.split(",").length;x++){
            output[x]=(char)(Integer.parseInt(input.split(",")[x])-encode.nextInt());
        }
        return output;
    }
    private void printInCode()throws  IOException{
        PrintWriter out=new PrintWriter(new FileWriter("people.txt"));
        int[] line;
        String key;
        Iterator it=Users.entrySet().iterator();
        while (it.hasNext()) {
            key= ((Map.Entry)it.next()).getKey()+"";
            line = code(key);
            out.print(line[0]);
            for (int x = 1; x < line.length; x++) {
                out.print("," + line[x]);
            }
            out.print("\n");

            line = code(Users.get(key));
            out.print(line[0]);
            for (int x = 1; x < line.length; x++) {
                out.print("," + line[x]);
            }
            out.print("\n");
            out.flush();
        }
        out.close();
    }
}
