package sample;

import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.io.*;

public class Main {
    private static HashMap<String,String> Users=new HashMap<>();
    private static HashMap<Integer,String> UserIDs=new HashMap<>();
    private static HashMap<Integer,String> serverMap=new HashMap<>();
    private static int passwordKey=12345;
    static int joinPort=1111,
            listPort=1112,
            serverstart=2000;
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

        Thread defaultChat=new Thread(new chat(serverstart));
        defaultChat.start();
        serverMap.put(serverstart,"chat1");
        serverstart++;

        Thread defaultChat2=new Thread(new chat(serverstart));
        defaultChat2.start();
        serverMap.put(serverstart,"chat2");
        serverstart++;

        Thread defaultRoom1=new Thread(new room(serverstart));
        defaultRoom1.start();
        serverMap.put(serverstart,"room1");
        serverstart++;
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
        catch (java.io.FileNotFoundException f){
            try {
                Users.put("admin","password");
                new Main().printInCode();
            }
            catch (Exception e){}
        }
        catch (java.io.IOException e){}
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
