/*
private static final int PORT = 5001; // Server1's port
public static final String ANSI_RESET = "\u001B[0m";
public static final String ANSI_BLACK = "\u001B[30m";
public static final String ANSI_RED = "\u001B[31m";
public static final String ANSI_GREEN = "\u001B[32m";
public static final String ANSI_YELLOW = "\u001B[33m";
public static final String ANSI_BLUE = "\u001B[34m";
public static final String ANSI_PURPLE = "\u001B[35m";
public static final String ANSI_CYAN = "\u001B[36m";
public static final String ANSI_WHITE = "\u001B[37m";
public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
*/
import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
public class Server1 {
    private static final int PORT = 5001; // Server3's port
    private static boolean Mode = false;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Aboneler serverAboneler = new Aboneler();
        System.out.println(ANSI_BLACK_BACKGROUND + ANSI_GREEN + "Server1 is running on port " + PORT + ANSI_RESET);
        // Listen for client connections
        try {
            while (true) {
                // server a erişen bir clienti burada lock yapabiliriz ???
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

        public ClientHandler(Socket socket, Aboneler serverAboneler) {
            this.clientSocket = socket;
            this.serverAboneler = serverAboneler;
        }

        public void run() {
            // Implement client handling logic here
            BufferedReader in = null;
            String message = null;
            PrintWriter out = null;
            //ReentrantLock lock = new ReentrantLock();
            //lock.lock();
            if (Mode) {
                    Mode = false;
                try {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    ObjectInputStream inObject  = new ObjectInputStream(clientSocket.getInputStream());
                    Aboneler newObject = (Aboneler) inObject.readObject();
                    // ifi kontrol ett
                    if (serverAboneler.getEpochMiliSeconds() <= newObject.getEpochMiliSeconds()) { //GÜNCELLE
                        //serverAboneler.setEpochMiliSeconds(newObject.getEpochMiliSeconds());
                        serverAboneler.setEpochMiliSeconds(newObject.getEpochMiliSeconds());
                        serverAboneler.setAboneler(newObject.getAboneler());
                        serverAboneler.setGirisYapanlarListesi(newObject.getGirisYapanlarListesi());
                        //serverAboneler = newObject;
                        System.out.print(clientSocket.getPort());
                        out.println("55 TAMM");
                    } else {
                        System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                        out.println("50 HATA - MESAJ ESKİ TARİHLİ");
                        //BU ARKADAŞ DİGERLERİNE GONDERMELI
                    }
                    System.out.println(" " + ANSI_GREEN_BACKGROUND + " " + ANSI_RESET + " ServerAboneler was updated");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    out.println("99 HATA - WRONG OBJECT");
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    message = in.readLine();
                    if (message.equals("xxx")) {
                        Mode = true;
                        //System.out.println("mode değişti " + String.valueOf(Mode));
                    } else {
                        if (message != null) {
                            String[] commandList = message.split(" ");
                            if (commandList.length != 2) {
                                System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                                out.println("50 HATA - You should send it as 'command {userId}'");
                                return;
                            }
                            String command = commandList[0];
                            int userId = Integer.parseInt(commandList[1]);
                            if (command.equals("ABONOL")) {
                                userCreateSub(out, userId); // kullanıcı abone değilse kaydını yapar.
                            } else if (command.equals("ABONIPTAL")) {
                                userDeleteSubscriber(out, userId);
                            } else if (command.equals("GIRIS")) {
                                logginUser(userId, out);
                            } else if (command.equals("CIKIS")) {
                                logoutUser(userId, out);
                            } else {
                                System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                                out.println("50 HATA - UNKNOWN COMMAND SENT");
                                return;
                            }
                        }
                    }
                    // Send a response back to the client
                    out.println("55 TAMM");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (message != null && !message.equals("xxx")) {
                System.out.println("Received message on Server1(" + currentThread().getId() + ") from client: " + ANSI_YELLOW_BACKGROUND + ANSI_BLACK + " " + message + " " + ANSI_RESET);
            }
            //lock.unlock();
        }

        private void userDeleteSubscriber(PrintWriter out, int userId) {
            ArrayList<Boolean> abonelerListesi;
            abonelerListesi = (ArrayList<Boolean>) serverAboneler.getAboneler();
            try {
                if (abonelerListesi.get(userId - 1)) {
                    abonelerListesi.set(userId - 1, false);
                    //sunucuları haberdar et.
                } else {
                    out.println("50 HATA - NO SUBSCRIPTION ALREADY");
                    System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);

                    return;
                }
            } catch (Exception b) {
                //zaten kullanıcı abonelıgı yok hata yonetimi yapılamsı gerkiyor.
                out.println("50 HATA - NO SUBSCRIPTION ALREADY");
                System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                return;
            }
            System.out.print("ABONELER LİSTESİ=> ");
            System.out.println(abonelerListesi);
            serverAboneler.setAboneler(abonelerListesi);
            setEpochMiliSeconds();
            Thread a = new ServersUpdate("localhost", 5002, serverAboneler); // Ping Server2
            Thread b = new ServersUpdate("localhost", 5003, serverAboneler); // Ping Server2
            b.start();
            a.start();
            try {
                b.join();
                a.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void userCreateSub(PrintWriter out, int userId) {
            ArrayList<Boolean> abonelerListesi;
            abonelerListesi = (ArrayList<Boolean>) serverAboneler.getAboneler();
            try {
                if (abonelerListesi.get(userId - 1)) {
                    out.println("50 HATA - SUBSCRIPTION ALREADY EXISTS");
                    System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                    return;
                } else {
                    abonelerListesi.set(userId - 1, true);
                }
            } catch (Exception b) {
                for (int i = 0; i < userId - 1; i++) {
                    try {
                        abonelerListesi.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        abonelerListesi.add(false);
                    }
                }
                abonelerListesi.add(true);
            }
            System.out.print("ABONELER LİSTESİ => ");
            System.out.println(abonelerListesi);
            serverAboneler.setAboneler(abonelerListesi);
            setEpochMiliSeconds();
            Thread a = new ServersUpdate("localhost", 5002, serverAboneler); // Ping Server2
            Thread b = new ServersUpdate("localhost", 5003, serverAboneler); // Ping Server2
            b.start();
            a.start();
            try {
                b.join();
                a.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void logginUser(int userId, PrintWriter out) {
            List<Boolean> girisYapanlarListesi = serverAboneler.getGirisYapanlarListesi();
            List<Boolean> aboneOlanlarListesi = serverAboneler.getAboneler();
            if (aboneOlanlarListesi.size() >= userId && aboneOlanlarListesi.get(userId - 1)) {
                if (girisYapanlarListesi.size() < userId) {
                    for (int i = 0; i < userId - 1; i++) {
                        try {
                            girisYapanlarListesi.get(i);
                        } catch (Exception e) {
                            girisYapanlarListesi.add(false);
                        }
                    }
                    girisYapanlarListesi.add(true);
                } else { //Guzel dizi boyutu normal
                    if (girisYapanlarListesi.get(userId - 1).equals(true)) {
                        System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                        out.println("50 HATA - USER ALREADY LOGGED");
                        return;
                    } else {
                        girisYapanlarListesi.set(userId - 1, true);
                    }
                }
                System.out.print("GİRİŞ YAPANLAR LİSTESİ => ");
                System.out.println(girisYapanlarListesi);
                serverAboneler.setGirisYapanlarListesi(girisYapanlarListesi);
                setEpochMiliSeconds();
                Thread a = new ServersUpdate("localhost", 5002, serverAboneler); // Ping Server2
                Thread b = new ServersUpdate("localhost", 5003, serverAboneler); // Ping Server2
                b.start();
                a.start();
                try {
                    b.join();
                    a.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                out.println("50 HATA - USER CANNOT LOGIN WITHOUT SUBSCRIBE");
            }
        }

        private void setEpochMiliSeconds() {
            long a = Instant.now().getEpochSecond();
            serverAboneler.setEpochMiliSeconds(a);
        }

        private void logoutUser(int userID, PrintWriter out) {
            List<Boolean> girisYapanlarListesi = serverAboneler.getGirisYapanlarListesi();
            try {
                if (girisYapanlarListesi.get(userID - 1)) {
                    girisYapanlarListesi.set(userID - 1, false);
                } else {
                    System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                    out.println("50 HATA - USER NOT LOGGED IN");
                    return;
                }
            } catch (Exception e) {
                System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + " INVALID REQUEST " + ANSI_RESET);
                out.println("50 HATA - USER NOT LOGGED IN");
                return;
            }
            System.out.print("GİRİŞ YAPANLAR LİSTESİ => ");
            System.out.println(girisYapanlarListesi);
            serverAboneler.setGirisYapanlarListesi(girisYapanlarListesi);
            setEpochMiliSeconds();
            Thread a = new ServersUpdate("localhost", 5002, serverAboneler); // Ping Server2
            Thread b = new ServersUpdate("localhost", 5003, serverAboneler); // Ping Server2
            b.start();
            a.start();
            //serverların gönderdiği cevaba bakabilirsin
            try {
                b.join();
                a.join();
                try {
                    a.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

        private static class ServersUpdate extends Thread {
            private String host;
            private int port;
            private Aboneler aboneler;

            public ServersUpdate(String host, int port, Aboneler aboneler) {
                this.host = host;
                this.port = port;
                this.aboneler = aboneler;
            }

            public void run() {
                while (true) {
                    try (Socket socketToServer = new Socket(host, port)) {
                        PrintWriter outString = new PrintWriter(socketToServer.getOutputStream(), true);
                        outString.println("xxx");
                        socketToServer.close();
                        sleep(1); //azaltılabilir.
                        sendObjectToServer();

                        break;
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            private synchronized void sendObjectToServer() throws IOException {
                Socket socketToServer2 = new Socket(host, port);
                ObjectOutputStream out = new ObjectOutputStream(socketToServer2.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socketToServer2.getInputStream()));
                out.writeObject(aboneler);
                String message = in.readLine();
                if (message.equals("55 TAMM")){
                    System.out.println("Server to Server " + ANSI_BLACK_BACKGROUND + ANSI_GREEN + " " + port + ": " + message + " " + ANSI_RESET );
                }else{
                    System.out.println("güncel olmayan Obje gönderimi");
                }

            }
        }
    }
}