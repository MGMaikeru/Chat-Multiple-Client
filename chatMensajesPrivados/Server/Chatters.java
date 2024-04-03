import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class Chatters {
    private final Set<Person> clientes = new HashSet<>();
    private Map<String, Set<Person>> groups = new HashMap<>();

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

    public boolean existsGroup(String groupName) {
        return groups.containsKey(groupName);
    }

    public void addUser(String name, PrintWriter out) {
        if (!name.isBlank() && !existsUser(name)) {
            Person p = new Person(name, out);
            clientes.add(p);
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

    public void addUserToGroup(String groupName, Person person) {
        if (existsGroup(groupName)) {
            Set<Person> groupMembers = groups.get(groupName);
            groupMembers.add(person);
            System.out.println(groupMembers.size());
        }
    }

    public void removeUserFromGroup(String groupName, Person person) {
        if (existsGroup(groupName)) {
            Set<Person> groupMembers = groups.get(groupName);
            groupMembers.remove(person);
        }
    }

    public void sendMessageToGroup(String groupName, String senderName, String message) {
        if (existsGroup(groupName)) {
            Set<Person> groupMembers = groups.get(groupName);
            for (Person p : groupMembers) {
                p.getOut().println("[Group: " + groupName + ", Sender: " + senderName + "]: " + message);
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
    }

    public void broadcastMessage(String message){

        for (Person p: clientes) {
            p.getOut().println(message);
        }
   }

    public void sendVoiceMessageToGroup(String groupName, String senderName){
        ByteArrayOutputStream byteArrayOutputStream = null;
        for (Person p: clientes) {
            if (senderName == p.getName()){
                p.getOut().println("Grabando...");
                byteArrayOutputStream = p.getAudioRecorder().recordAudio();
                p.getOut().println("Grabacion terminada");
            }
        }

        if (existsGroup(groupName)) {
            Set<Person> groupMembers = groups.get(groupName);
            for (Person p : groupMembers) {
                if (!p.getName().equals(senderName)){
                    p.getOut().println("[Group: " + groupName + ", Sender: " + senderName + "] Audio:");
                    p.getOut().println("Reproduciendo");
                    p.getAudioRecorder().reproduceAudio(byteArrayOutputStream);
                }
            }
        }

    }

    public void reproduceVoiceMessage(){

    }


}