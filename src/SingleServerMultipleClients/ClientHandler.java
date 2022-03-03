package SingleServerMultipleClients;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler>clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName=bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("Server : "+clientName+" has joined the conversation ! ");

        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    @Override
    public void run() {
            String clientMessage;
            while(socket.isConnected()){
                try{
                    clientMessage= bufferedReader.readLine();
                    broadcastMessage(clientMessage);
                }catch(IOException e){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
    }
    public void broadcastMessage(String msg){
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.clientName.equals(clientName)){
                    clientHandler.bufferedWriter.write(msg);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }

    }
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("Server : "+clientName+" has left the conversation !");

    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();

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
}
