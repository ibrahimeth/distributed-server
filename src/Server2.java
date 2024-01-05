import java.io.*;
import java.net.*;
import java.util.List;

public class Server2 {
    private static final int PORT = 5002; // Server2's port
    private static boolean Mode = false;
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println(ANSI_BLACK_BACKGROUND + ANSI_GREEN +"Server2 is running on port " + PORT + ANSI_RESET);

        Aboneler serverAboneler = new Aboneler();

        // Ping other servers
        //new PingThread("localhost", 5001).start(); // Ping Server1
        //new PingThread("localhost", 5003).start(); // Ping Server3

        // Listen for client connections
        try {
            while (true) {
                new ClientHandler(serverSocket.accept(), serverAboneler).start();
                //System.out.println("YENİ HANDLER");
            }
        } finally {
            serverSocket.close();
        }
    }

    // Thread to handle client requests
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private Aboneler serverAboneler;

        public ClientHandler(Socket socket, Aboneler serverAboneler) {
            this.clientSocket = socket;
            this.serverAboneler = serverAboneler;
        }

        public void run() {
            // Implement client handling logic here
            BufferedReader in = null;
            String message = null;
            PrintWriter out = null;
            ObjectInputStream inObject = null;
            if (Mode){
                try {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    inObject = new ObjectInputStream(clientSocket.getInputStream());
                    Aboneler newObject = (Aboneler) inObject.readObject();
                    if (serverAboneler.getEpochMiliSeconds() < newObject.getEpochMiliSeconds()){ //GÜNCELLE
                        //serverAboneler.setEpochMiliSeconds(newObject.getEpochMiliSeconds());
                        serverAboneler = newObject ;
                        System.out.print(serverAboneler.getEpochMiliSeconds());
                    }else{
                        System.out.println("daha eski");
                        //BU ARKADAŞ DİGERLERİNE GONDERMELI
                    }
                    System.out.println(" " +ANSI_GREEN_BACKGROUND + " " + ANSI_RESET + " ServerAboneler güncellendi.");

                    Mode = false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    out.println("99 HATA - WRONG OBJECT");
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    message = in.readLine();
                    if (message.equals("xxx")){
                        Mode = true ;
                        //System.out.println("mode değişti " + String.valueOf(Mode));
                    }else {
                        System.out.println("string komut geldi abi yönet.");
                    }
                    // Send a response back to the client
                    out.println("55 TAMM");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //System.out.println("Received message on Server2("+currentThread().getId() +") from client: " + message);

        }
    }

    // Thread to ping other servers
    private static class PingThread extends Thread {
        private String host;
        private int port;

        public PingThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void run() {

            try {
                while (true) {
                    try (Socket socket = new Socket(host, port)) {
                        System.out.println("Pinged " + host + " on port " + port);
                    } catch (IOException e) {
                        System.out.println("Ping to " + host + " on port " + port + " failed, retrying...");
                    }

                    try {
                        Thread.sleep(10000); // Wait for 10 seconds before retrying
                    } catch (InterruptedException ie) {
                        System.out.println("Ping thread interrupted: " + ie.getMessage());
                        break; // Optional: exit the loop if the thread is interrupted
                    }
                }
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }

        }
    }
}

