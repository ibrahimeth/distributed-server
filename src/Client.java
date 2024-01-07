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
        int serverMode;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        /*
        while(true){
            serverMode = Integer.parseInt(in.readLine());
            command = in.readLine();
            if (command.equals("")){

            } else if (command.equals("q")) {
                break;
            } else{
                switch (serverMode){
                    case 1:
                        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, command);
                        break;
                    case 2:
                        sendAndReceiveMessage(SERVER3_HOST, SERVER2_PORT, command);
                        break;
                    default:
                        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, command);
                        break;
                }
            }
        }

         */
        long startTime = System.currentTimeMillis();
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL 2");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONIPTAL 3");
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS 2");
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "CIKIS 2");
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "ABONOL 4");
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONIPTAL 2");
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS 4");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS 3");
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "GIRIS 1");
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL 3");
        long estimatedTime = System.currentTimeMillis() - startTime;
        double seconds = (double)estimatedTime/1000;
        System.out.println("10 istek süresi =>" + seconds);
        double time = givePerSecondRequest(seconds) ;
        System.out.println("1 saniyede " + String.valueOf(time) );

    }
    public static int givePerSecondRequest(double second){
        double a = 600 / second;
        return (int) a ;
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
