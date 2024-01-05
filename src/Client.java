import java.io.*;
import java.net.Socket;

public class Client {

    private static final String SERVER1_HOST = "localhost";
    private static final int SERVER1_PORT = 5001;
    private static final String SERVER2_HOST = "localhost";
    private static final int SERVER2_PORT = 5002;
    private static final String SERVER3_HOST = "localhost";
    private static final int SERVER3_PORT = 5003;
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static void main(String[] args) throws IOException {
        String command;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (!(command = in.readLine()).equals("q")){
            if (command.equals("")){

            }else{
                sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, command);
                sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, command);
            }
        }
        /*
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL");
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONIPTAL");
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL");
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONIPTAL");
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL");
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONIPTAL");
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL");
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONIPTAL");
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS");
         */
    }

    private static void sendAndReceiveMessage(String host, int port, String message) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send a message to the server
            out.println(message);

            // Receive the response from the server
            String response = in.readLine();
            System.out.println("Response from server on port " + ANSI_BLACK_BACKGROUND + ANSI_GREEN + " " + port + ": " + response + " " + ANSI_RESET );
        } catch (IOException e) {
            System.out.println("Error connecting to server on port " + port + ": " + e.getMessage());
        }
    }
}
