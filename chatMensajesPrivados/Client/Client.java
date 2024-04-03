// Client.java

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 6789;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            System.out.println("Conectado al servidor.");

            String message;
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while ((message = in.readLine()) != null) {
                if (message.startsWith("SUBMITNAME")) {
                    System.out.print("Ingrese nombre de usuario: ");
                    String name = userInput.readLine();
                    out.println(name);
                }
                else if (message.startsWith("NAMEACCEPTED")) {
                    System.out.println("Nombre aceptado!!");
                    break;
                }
            }

            Lector lector = new Lector(in);
            new Thread(lector).start();

            // Manejo de grupos
            System.out.println("Para unirse a un grupo , use el formato 'JOINGROUP,<nombre_del_grupo>'");
            System.out.println("Para crear un grupo, use el formato 'CREATEGROUP,<nombre_del_grupo>'");
            System.out.println("Para salir de un grupo, use el formato 'LEAVEGROUP,<nombre_del_grupo>'");
            System.out.println("Para enviar un mensaje a un grupo, use el formato 'SENDTOGROUP,<nombre_del_grupo>,<mensaje>'");
            System.out.println("Para enviar un audio a un grupo, use el formato 'SENDAUDIOTOGROUP,<nombre_del_grupo>'");
            System.out.println("Para enviar un audio privado, use el formato 'SENDPRIVATEAUDIO,<nombre_del_usuario>'");
            while (true) {
                String input = userInput.readLine();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println(input);
            }

            // Cuando el usuario escribe "exit", cerramos los flujos y el socket
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
