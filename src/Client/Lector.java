package Client;

import java.io.*;
import java.net.*;
import java.util.Base64;

public class Lector implements Runnable {
    String message;
    BufferedReader in;
    byte[] bytes;
    InputStream soundIn;
    OutputStream soundOut;
    AudioRecorderPlayer audioRecorderPlayer;

    public Lector(BufferedReader in, InputStream soundIn, AudioRecorderPlayer audioRecorderPlayer, OutputStream soundOut) {
        this.in = in;
        this.soundIn = soundIn;
        this.soundOut = soundOut;
        this.audioRecorderPlayer = audioRecorderPlayer;
    }

    @Override
    public void run() {
        //leer la linea que envia el servidor e imprimir en pantalla
        try {
            Thread.sleep(4000);
            while ((message = in.readLine()) != null) {
                if (message.startsWith("SENDPRIVATEAUDIO") || message.startsWith("SENDAUDIOTOGROUP")) {
                    String[] parts = message.split(",", 2);
                    String audiostring = parts[1];
                    byte[] decodedBytes = Base64.getDecoder().decode(audiostring);
                    audioRecorderPlayer.reproduceAudio(decodedBytes);
                } else {
                        System.out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
