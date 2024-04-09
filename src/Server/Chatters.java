package Server;

import Client.Client;
import Client.Lector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class Chatters {
    private final Set<Person> clientes = new HashSet<>();
    private Map<String, Set<Person>> groups = new HashMap<>();
    private StringBuilder allHistory = new StringBuilder();

    public Chatters() {
    }

    public boolean existsUser(String name) {
        for (Person p : clientes) {
            if (name.equals(p.getName())) {
                return true;
            }
        }
        return false;
    }

    public Person getUser(String name) {
        for (Person p : clientes) {
            if (name.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    public boolean existsGroup(String groupName) {
        return groups.containsKey(groupName);
    }

    public void addUser(String name, PrintWriter out, OutputStream soundOut) {
        if (!name.isBlank() && !existsUser(name)) {
            Person p = new Person(name, out, soundOut);
            clientes.add(p);
        }else{
            getUser(name);
        }
    }

    public void removeUser(String name) {
        Iterator<Person> iterator = clientes.iterator();
        while (iterator.hasNext()) {
            Person p = iterator.next();
            if (name.equals(p.getName())) {
                iterator.remove();
                break;
            }
        }
    }

    public void createGroup(String groupName) {
        if (!groupName.isBlank() && !existsGroup(groupName)) {
            groups.put(groupName, new HashSet<>());
        }
    }

    public void addUserToGroup(String groupName, String personname) {
        if (existsGroup(groupName)) {
            for (Person p : clientes) {
                if (personname.equals(p.getName())) {
                    Set<Person> groupMembers=groups.get(groupName);
                    groupMembers.add(p);
                    groups.replace(groupName,groupMembers);
                }
            }
        }
    }

    public void removeUserFromGroup(String groupName, String  personame) {

        if (existsGroup(groupName)) {
            Person personToRemove = null;
            Set<Person>groupMembers= groups.get(groupName);
            for (Person person : groupMembers) {
                if (person.getName().equals(personame)) {
                    personToRemove = person;
                }
            }
            groupMembers.remove(personToRemove);
        }
    }

    public void sendMessageToGroup(String groupName, String senderName, String message) {
        if (existsGroup(groupName)) {
            Set<Person> groupMembers = groups.get(groupName);
            for (Person p : groupMembers) {
                p.getOut().println("[Group: " + groupName + ", Sender: " + senderName + "]: " + message);
            }

            String historyMessage = "[Group: " + groupName + ", Sender: " + senderName + "]: " + message + "\n";
            allHistory.append(historyMessage);
            try {
                saveHistory(new StringBuilder(historyMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPrivateMessage(String senderName, String recipientName, String message) {
        for (Person p : clientes) {
            if (recipientName.equals(p.getName())) {
                p.getOut().println("[Private from " + senderName + "]: " + message);
                break;
            }
        }
        String historyMessage = "[Private from " + senderName + " to " + recipientName + "]: " + message + "\n";
        allHistory.append(historyMessage);
        try {
            saveHistory(new StringBuilder(historyMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message){
        for (Person p: clientes) {
            p.getOut().println(message);
        }
        String historyMessage = message + "\n";
        allHistory.append(historyMessage);
        try {
            saveHistory(new StringBuilder(historyMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendVoiceMessageToGroup(String standar, String groupName, String senderName, String bytes) throws IOException{

        if (existsGroup(groupName)) {
            Set<Person> groupMembers = groups.get(groupName);
            for (Person p : groupMembers) {
                    p.getOut().println("[audio from " + senderName + "] ");
                    //enviar
                    String novo= standar+","+bytes;
                    p.getOut().println(novo);
                    p.getOut().flush();
            }
            String historyMessage = "[Group: " + groupName + ", Sender: " + senderName + "] Audio" + "\n";
            allHistory.append(historyMessage);
            try {
                saveHistory(new StringBuilder(historyMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendPrivateVoiceMessage(String standar,String senderName, String recipientName, String bytes) throws IOException{
        for (Person p : clientes) {
            if (recipientName.equals(p.getName())) {
                p.getOut().println("[Private audio from " + senderName + "] ");
                //enviar
                String novo= standar+","+bytes;
                p.getOut().println(novo);
                p.getOut().flush();
            }
        }

        String historyMessage = "[Private from " + senderName + "]: Audio " + "\n";
        allHistory.append(historyMessage);
        try {
            saveHistory(new StringBuilder(historyMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String folder = "history";
    static String path = "history/allHistory.txt";

    public static void saveHistory(StringBuilder allHistory) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            File f = new File(folder);
            if (!f.exists()) {
                f.mkdirs();
            }
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file, true);
        writer.write(allHistory.toString());
        writer.flush();
        writer.close();
    }

    public StringBuilder getAllHistory() {
        return allHistory;
    }


}
