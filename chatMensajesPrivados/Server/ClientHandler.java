// ClientHandler.java

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private static PrintWriter out;
    private String clientName;
    private Chatters clients;
    private AudioRecorderPlayer audioPlayer;

    public ClientHandler(Socket socket, Chatters clients) {
        this.clients = clients;
        this.clientSocket = socket;
        this.audioPlayer = new AudioRecorderPlayer();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                out.println("SUBMITNAME");
                clientName = in.readLine();
                if (clientName == null) {
                    return;
                }
                synchronized (clientName) {
                    if (!clientName.isBlank()) {
                        clients.broadcastMessage(clientName + " se ha unido al chat.");
                        out.println("NAMEACCEPTED " + clientName);
                        clients.addUser(clientName, out);
                        break;
                    }
                }
            }

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("CREATEGROUP")) {
                    String[] parts = message.split(",", 2); // Obtener el nombre del grupo
                    clients.createGroup(parts[1]);
                    clients.addUserToGroup(parts[1], new Person(clientName, out));
                    out.println("¡Has creado y te has unido al grupo '" + parts[1] + "'!");
                } else if (message.startsWith("JOINGROUP")) {
                    String[] parts = message.split(",", 2); // Obtener el nombre del grupo
                    clients.addUserToGroup(parts[1], new Person(clientName, out));
                    out.println("¡Te has unido al grupo '" + parts[1] + "'!");
                } else if (message.startsWith("LEAVEGROUP")) {
                    String[] parts = message.split(",", 2); // Obtener el nombre del grupo
                    clients.removeUserFromGroup(parts[1], new Person(clientName, out));
                    out.println("Has abandonado el grupo '" + parts[1] + "'.");
                } else if (message.startsWith("SENDTOGROUP")) {
                    String[] parts = message.split(",", 3);
                    String groupName = parts[1];
                    String content = parts[2];
                    clients.sendMessageToGroup(groupName, clientName, content);
                } else if (message.startsWith("SENDAUDIOTOGROUP")){
                    String[] parts = message.split(",", 3);
                    String groupName = parts[1];
                    clients.sendVoiceMessageToGroup(groupName, clientName);
                }else if (message.startsWith("SENDPRIVATEAUDIO")) {
                    String[] parts = message.split(",", 3);
                    String receiverUser = parts[1];
                    clients.sendPrivateVoiceMessage(clientName, receiverUser);
                }else if(message.startsWith("SENDPRIVATETEXT")){
                    String[] parts = message.split(",", 3);
                    String receiverUser = parts[1];
                    String content = parts[2];
                    clients.sendPrivateMessage(clientName, receiverUser, content);
                }else if(message.startsWith("HISTORY")){
                    out.println("---------");
                    load(clientSocket, clients);
                } else {
                    clients.broadcastMessage(clientName + ": " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println(clientName + " ha abandonado el chat.");
                clients.broadcastMessage(clientName + " ha abandonado el chat.");
                clients.removeUser(clientName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void load(Socket socket, Chatters clients) throws IOException{
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println(clients.getAllHistory().toString());
    
    }
}


