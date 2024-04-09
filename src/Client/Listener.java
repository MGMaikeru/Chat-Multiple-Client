package Client;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Listener implements Runnable{
    byte[] bytes;
    InputStream in;
    AudioRecorderPlayer audioRecorderPlayer;
    public Listener(InputStream in, AudioRecorderPlayer audioRecorderPlayer){
        this.in=in;
        this.audioRecorderPlayer = audioRecorderPlayer;
    }

    @Override
    public void run() {
    }

    public void reproduce(String bytes) {
        //leer la linea que envia el servidor e imprimir en pantalla
        try {

            while ((bytes = Arrays.toString(in.readAllBytes())) != null) {
                audioRecorderPlayer.reproduceAudio(in.readAllBytes());
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

