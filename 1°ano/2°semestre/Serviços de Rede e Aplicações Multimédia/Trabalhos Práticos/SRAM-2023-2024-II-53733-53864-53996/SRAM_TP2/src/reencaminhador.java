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
import java.util.LinkedList;
import java.util.Queue;

public class reencaminhador {
    private static final int SERVER_PORT = 12345;
    private static final int FORWARDER_PORT = 54321;
    private static final int N = 10; // Number of frames before pausing
    private static final int P = 2;  // Pause duration in seconds
    private static final int M = 30; // Interval in seconds to drop a frame

    private DatagramSocket forwarderSocket;
    private DatagramSocket serverSubscriptionSocket;
    private InetAddress serverAddress;
    private InetAddress clientAddress;
    private Queue<byte[]> buffer = new LinkedList<>();
    private long lastIgnoreTime = 0;

    public reencaminhador() {
        try {
            forwarderSocket = new DatagramSocket(FORWARDER_PORT);
            serverAddress = InetAddress.getByName("127.0.0.1");
            System.out.println("Forwarder started on port: " + FORWARDER_PORT);

            // Wait for client subscription
            byte[] subscriptionBuffer = new byte[256];
            DatagramPacket subscriptionPacket = new DatagramPacket(subscriptionBuffer, subscriptionBuffer.length);
            forwarderSocket.receive(subscriptionPacket);

            clientAddress = subscriptionPacket.getAddress();
            int clientPort = subscriptionPacket.getPort();
            System.out.println("Client subscribed from: " + clientAddress + ":" + clientPort);

            // Subscribe to the server
            subscribeToServer();

            // Start receiving frames from the server and forwarding to the client
            while (true) {
                byte[] buffer = new byte[2048 * 15];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSubscriptionSocket.receive(packet);

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastIgnoreTime >= M * 1000) {
                    lastIgnoreTime = currentTime;
                    System.out.println("Ignoring PDU");
                    continue;
                }

                this.buffer.add(packet.getData());

                if (this.buffer.size() >= N) {
                    System.out.println("Pausing for " + P + " seconds");
                    Thread.sleep(P * 1000);
                    while (!this.buffer.isEmpty()) {
                        byte[] data = this.buffer.poll();
                        DatagramPacket forwardPacket = new DatagramPacket(data, data.length, clientAddress, clientPort);
                        forwarderSocket.send(forwardPacket);
                    }
                } else {
                    DatagramPacket forwardPacket = new DatagramPacket(packet.getData(), packet.getLength(), clientAddress, clientPort);
                    forwarderSocket.send(forwardPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (forwarderSocket != null && !forwarderSocket.isClosed()) {
                forwarderSocket.close();
            }
            if (serverSubscriptionSocket != null && !serverSubscriptionSocket.isClosed()) {
                serverSubscriptionSocket.close();
            }
        }
    }

    private void subscribeToServer() throws IOException {
        serverSubscriptionSocket = new DatagramSocket();
        byte[] buffer = new byte[256];
        DatagramPacket subscriptionPacket = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
        serverSubscriptionSocket.send(subscriptionPacket);
        System.out.println("Subscribed to server at " + serverAddress + ":" + SERVER_PORT);
    }

    public static void main(String[] args) {
        new reencaminhador();
    }
}



