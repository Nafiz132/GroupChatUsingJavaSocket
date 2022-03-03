package SingleServerMultipleClients;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    public Client(Socket socket, String userName) {
        try{
            this.socket=socket;
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName=userName;
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }

    }
    public void sendMessage(){
        try{
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner= new Scanner(System.in);

            while(socket.isConnected()){
                String msg= scanner.nextLine();
                bufferedWriter.write(userName+" : "+ msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;
                while(socket.isConnected()){
                   try{
                       msgFromChat=bufferedReader.readLine();
                       System.out.println(msgFromChat);
                   }catch(IOException e){
                       closeEverything(socket,bufferedReader,bufferedWriter);
                   }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        //removeClientHandler();

        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!= null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Your name for the conversation : ");
        String userName = scanner.nextLine();
        Socket socket= new Socket("localhost",1234);
        Client client = new Client(socket,userName);
        client.listenForMessage();
        client.sendMessage();
    }
}
