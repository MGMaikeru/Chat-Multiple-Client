package Server;

import Client.Listener;

import java.util.Set;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

//
public class Person {
    private String name;
    PrintWriter out;
    private OutputStream soundout;

    private Listener listener;

    public Person(String name, PrintWriter out, OutputStream soundOut){
        this.name = name;
        this.out  = out;
        this.soundout = soundOut;
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    public PrintWriter getOut() {
        return out;
    }

    public OutputStream getOutputStream(){
        return soundout;
    }

    public Listener getListener() {
        return listener;
    }
}
