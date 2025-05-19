/**
 * SERVIÇOS DE REDE E APLICAÇÕES MULTIMÉDIA 2024
 *
 * @name Protocolo Transporte Multimédia
 *
 * @SDK Oracle OpenJDK version 22
 *
 * @date 18/06/24
 *
 * @authors Catarina Pereira  <pg53733@alunos.uminho.pt>
 *          Inês Neves <pg53864@alunos.uminho.pt>
 *          Leonardo Martins <pg53996@alunos.uminho.pt>
 *
 * @details Este programa tem como objetivo testar um protocolo de transporte
 *          Multimédia para enviar dados de um servidor para um cliente através de
 *          um reencaminhador que irá introduzir um atraso e perdas de pacotes na
 *          comunicação
 */

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Servidor {
    private static final int PORT = 12345;
    private DatagramSocket serverSocket;
    private static final int F = 0;
    private static final int A = 2;

    private ArrayList<byte[]> digits = new ArrayList<>();
    private String[] imagePaths = new String[] {"zero.png", "um.png", "dois.png", "tres.png", "quatro.png", "cinco.png",
            "seis.png", "sete.png", "oito.png", "nove.png", "separador.png"};

    public Servidor() {
        try {
            //InetSocketAddress addr = new InetSocketAddress(12);
            serverSocket = new DatagramSocket(PORT);
            System.out.println("Server started on port: " + PORT);
            System.out.println("Reading digit images to array list");
            readDigitsToArray();
            System.out.println("Waiting for connections");
            // Always keep trying for new client connections
            while (true) {
                try {
                    byte[] buffer = new byte[256];

                    //Wait until a client requests to "subscribe" the server
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(request);

                    //Get client "socket" informations
                    InetAddress clientAddress = request.getAddress();
                    int clientPort = request.getPort();

                    System.out.println("Client Connected from: " + clientAddress + ":" + clientPort);

                    // Handle the incoming clients on a new thread
                    ServerHandler HandlerThread = new ServerHandler(serverSocket, clientAddress, clientPort, getF(), getA(), getDigits());
                    new Thread(HandlerThread).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            serverSocket.close();
        }

    }

    public static int getA() {
        return A;
    }

    public static int getF() {
        return F;
    }

    public ArrayList<byte[]> getDigits() {
        return digits;
    }

    public void readDigitsToArray() throws IOException {
        for (int i = 0; i < imagePaths.length; i++){
            File fi = new File(imagePaths[i]);
            byte[] fileContent = Files.readAllBytes(fi.toPath());
            digits.add(fileContent);
        }
    }

    public static void main(String[] args) {
        new Servidor();
    }


    /**
    Client Handler for the server side
    */
    private class ServerHandler implements Runnable {
        private InetAddress clientAddress;
        private int clientPort;
        private int F;
        private int A;
        private final ArrayList<byte[]> digits;
        private DateTimeFormatter formatter;
        private DatagramSocket serverSocket;

        public ServerHandler(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort, int F, int A, ArrayList<byte[]> digits) {
            this.serverSocket = serverSocket;
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
            this.F = F;
            this.A = A;
            this.digits = digits;
            this.formatter = FormatDecission();
        }

        @Override
        public void run() {
            double[] sleepIntervalls = {
                    1000,          // 0: Seconds
                    100,        // 1: Deciseconds
                    10,         // 2: Centiseconds
                    1,          // 3: Milliseconds
            };
            try {
                long start = System.currentTimeMillis();
                while (true) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    long end = System.currentTimeMillis();
                    if (end - start >= sleepIntervalls[this.F]) {
                        LocalTime now = LocalTime.now();
                        PDU toSend = new PDU(this.F, this.A);
                        String formattedTime = this.formatter.format(now);
                        char[] charArray = formattedTime.toCharArray();
                        toSend.addString(formattedTime);
                        System.out.println(toSend.getString());
                        for (char c : charArray) {
                            // Character is a digit '0' to '9' or ':' (which is next to '9' on ascii table)
                            toSend.addDigit(digits.get(c - '0')); // Convert char to integer index
                        }
                        toSend.setTimeSent();


                        oos.writeObject(toSend);
                        oos.flush();

                        byte[] serializedPDU = baos.toByteArray();
                        DatagramPacket packet = new DatagramPacket(serializedPDU, serializedPDU.length, clientAddress, clientPort);

                        serverSocket.send(packet);
                        baos.close();
                        oos.close();
                        start = end;
                    }
                }
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * @return the clock time format
         * @params the precision of the clock, decided by the server (in this case F)
         */
        private DateTimeFormatter FormatDecission() {
            String[] patterns = {        // Time Format depending on Precision (F)
                    "HH:mm:ss",          // 0: Seconds
                    "HH:mm:ss:S",        // 1: Deciseconds
                    "HH:mm:ss:SS",       // 2: Centiseconds
                    "HH:mm:ss:SSS",      // 3: Milliseconds   -> with LocalTime library we can go up to the
                                                              // nanoseconds, but it wouldn't be precise because
                                                              // of the available computacional power, so we
                                                              // decided to stop here (since here it's already
                                                              // not 100% precise)
            };

            // Ensure precision (F) is within bounds of LocalTime clock limits
            if (this.F < 0){
                this.F = 0;
            } else if (this.F >= patterns.length) {
                this.F = patterns.length - 1;
            }

            String pattern = patterns[this.F];

            return DateTimeFormatter.ofPattern(pattern);
        }
    }
}