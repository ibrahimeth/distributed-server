import java.io.*;
import java.net.*;
import java.util.List;

public class Server2 {
    private static final int PORT = 5002; // Server2's port
    private static boolean Mode = false;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server2 is running on port " + PORT);

        // Ping other servers
        //new PingThread("localhost", 5001).start(); // Ping Server1
        //new PingThread("localhost", 5003).start(); // Ping Server3

        // Listen for client connections
        try {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
                System.out.println("YENİ HANDLER");
            }
        } finally {
            serverSocket.close();
        }
    }

    // Thread to handle client requests
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            // Implement client handling logic here
            BufferedReader in = null;
            String message = null;
            Aboneler objectCast = null;
            PrintWriter out = null;
            ObjectInputStream inObject = null;
            System.out.println("Yeniden başladık " + String.valueOf(Mode) );
            if (Mode){
                try {
                    System.out.print("Aboneler Listesi => ");
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    inObject = new ObjectInputStream(clientSocket.getInputStream());
                    objectCast = (Aboneler) inObject.readObject();
                    List<Boolean> mylist = objectCast.getAboneler();
                    System.out.println(mylist);
                    Mode = false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    message = in.readLine();
                    if (message.equals("xxx")){
                        Mode = true ;
                        System.out.println("mode değişti " + String.valueOf(Mode));
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

