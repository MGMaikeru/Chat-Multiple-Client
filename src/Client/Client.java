package Client;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 6789;
    private PrintWriter out;
    private static OutputStream soundOut;
    private static InputStream soundIn;
    private static AudioRecorderPlayer  audioRecorder= new AudioRecorderPlayer();
    private static String finalname;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            System.out.println("Conectado al servidor.");

            String message;
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream soundOut = socket.getOutputStream();
            InputStream soundIn = socket.getInputStream();

            while ((message = in.readLine()) != null) {
                if (message.startsWith("SUBMITNAME")) {
                    System.out.print("Ingrese nombre de usuario: ");
                    String name = userInput.readLine();
                    finalname=name;
                    out.println(name);
                }
                else if (message.startsWith("NAMEACCEPTED")) {
                    System.out.println("Nombre aceptado!!");
                    break;
                }
            }

            Lector lector = new Lector(in,soundIn, audioRecorder, soundOut);
            new Thread(lector).start();

            System.out.println("Para unirse a un grupo , use el formato 'JOINGROUP,<nombre_del_grupo>'");
            System.out.println("Para crear un grupo, use el formato 'CREATEGROUP,<nombre_del_grupo>'");
            System.out.println("Para salir de un grupo, use el formato 'LEAVEGROUP,<nombre_del_grupo>'");
            System.out.println("Para enviar un mensaje a un grupo, use el formato 'SENDTOGROUP,<nombre_del_grupo>,<mensaje>'");
            System.out.println("Para enviar un audio a un grupo, use el formato 'SENDAUDIOTOGROUP,<nombre_del_grupo>'");
            System.out.println("Para enviar un audio privado, use el formato 'SENDPRIVATEAUDIO,<nombre_del_usuario>'");
            System.out.println("Para enviar un mensaje privado, use el formato 'SENDPRIVATETEXT,<nombre_del_usuario>,<mensaje>'");
            System.out.println("Para ver el historial, use el formato 'HISTORY' ");
            while (true) {
                String input = userInput.readLine();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                if (input.startsWith("SENDPRIVATEAUDIO")||input.startsWith("SENDAUDIOTOGROUP")) {
                    ByteArrayOutputStream bytes= audioRecorder.recordAudio();
                    // Codificación de los bytes a BASE64
                    String encodedBytes = Base64.getEncoder().encodeToString(bytes.toByteArray());
                    // Concatenación de 'input' y los bytes codificados en BASE64
                    String mSend = input + ","+encodedBytes+","+finalname;
                    out.println(mSend);
                }else{
                    out.println(input+","+finalname);
                }
            }

            // Cuando el usuario escribe "exit", cerramos los flujos y el socket
            out.close();
            in.close();
            socket.close();
            soundOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

