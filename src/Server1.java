import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server1 {
    private static final int PORT = 5001; // Server1's port

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server1 is running on port " + PORT);

        Aboneler serverAboneler = new Aboneler();

        //new PingThread("localhost", 5002).start(); // Ping Server2
        //new PingThread("localhost", 5003).start(); // Ping Server3


        // Listen for client connections
        try {
            while (true) {
                // Ping other servers

                new ClientHandler(serverSocket.accept(), serverAboneler).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    // Thread to handle client requests
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private Aboneler serverAboneler;

        public ClientHandler(Socket socket,Aboneler serverAboneler) {
            this.clientSocket = socket;
            this.serverAboneler = serverAboneler;
        }

        public void run() {
            // Implement client handling logic here
            BufferedReader in = null;
            String message = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                message = in.readLine();
                if (message != null) {
                    String[] commandList = message.split(" ");
                    String command = commandList[0];
                    String userId = commandList[1];
                    if (command.equals("ABONOL")){
                        //Aboneler classında userId li kullanıcı hesabı var mı?
                        controlIsUserCorrect(out, userId); // kullanıcı abone değilse kaydını yapar.
                        Thread a = new ServerUpdate("localhost", 5002, serverAboneler); // Ping Server2
                        a.start();
                        a.join();
                        //new ServerUpdate("localhost", 5003, serverAboneler).start(); // Ping Server3
                    }else if (command.equals("ABONPTAL")){
                        controlUserSubscriber(out, userId);
                    }
                }
                // burayı yönetmemiz gerekiyor.
                // Send a response back to the client
                out.println("55 TAMM");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Received message on Server1("+currentThread().getId() +") from client: " + message);
        }
        private void controlUserSubscriber(PrintWriter out, String userId){
            ArrayList<Boolean> abonelerListesi;
            abonelerListesi = (ArrayList<Boolean>) serverAboneler.getAboneler();
            try {
                if(abonelerListesi.get(Integer.parseInt(userId) - 1)){
                    abonelerListesi.set(Integer.parseInt(userId) - 1, false);
                    System.out.println("KULLANICI ABONELIGI IPTAL EDILDI");
                    //sunucuları haberdar et.
                }else {
                    System.out.println("KULLANICI ZATEN YOK");
                    //çevrimiçi yap
                    //abonelerListesi.set(Integer.parseInt(userId) - 1, true);
                }
            }catch (Exception b){
                //zaten kullanıcı abonelıgı yok hata yonetimi yapılamsı gerkiyor.
                out.println("Already there is no subscriber yet");
            }
            System.out.print("Aboneler Listesi => ");
            System.out.println(abonelerListesi);
            serverAboneler.setAboneler(abonelerListesi);
        }
        private void controlIsUserCorrect(PrintWriter out,String userId){
            ArrayList<Boolean> abonelerListesi;
            abonelerListesi = (ArrayList<Boolean>) serverAboneler.getAboneler();
            try {
                if(abonelerListesi.get(Integer.parseInt(userId) - 1)){
                    System.out.println("KULLANICI ZATEN VAR");
                    //true döenebilirsin
                }else {
                    System.out.println("KULLANICI KAYDI YAPILDI");
                    //çevrimiçi yap
                    abonelerListesi.set(Integer.parseInt(userId) - 1, true);
                }
            }catch (Exception b){
                for (int i = 0 ; i < Integer.parseInt(userId) - 1 ; i++){
                    try {
                        if(abonelerListesi.get(i) == null){
                            System.out.println("evet null");
                        }
                    }catch (IndexOutOfBoundsException e){
                        abonelerListesi.add(false);
                    }
                }
                abonelerListesi.add(true);
            }
            System.out.print("Aboneler Listesi => ");
            System.out.println(abonelerListesi);
            serverAboneler.setAboneler(abonelerListesi);
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

    private static class ServerUpdate extends Thread {
        private String host;
        private int port;
        private Aboneler aboneler;
        public ServerUpdate(String host, int port, Aboneler aboneler) {
            this.host = host;
            this.port = port;
            this.aboneler = aboneler;
        }
        public void run(){
            while (true){
                try (Socket socketToServer = new Socket(host, port)){
                    PrintWriter outString = new PrintWriter(socketToServer.getOutputStream(),true);
                    outString.println("xxx");
                    socketToServer.close();
                    sleep(50);
                    Socket socketToServer2 = new Socket(host, port);
                    ObjectOutputStream out = new ObjectOutputStream(socketToServer2.getOutputStream());
                    out.writeObject(aboneler);
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

