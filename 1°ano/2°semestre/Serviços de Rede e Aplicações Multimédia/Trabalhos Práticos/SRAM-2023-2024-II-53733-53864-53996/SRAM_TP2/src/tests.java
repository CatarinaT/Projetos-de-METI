import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class tests extends JFrame {
    private BufferedImage img;
    private static JLabel picLabel;
    private BufferedImage lastimg = null;

    public tests() throws IOException, ClassNotFoundException, InterruptedException, InvocationTargetException {
        setTitle("Server");
        setLayout(new BorderLayout());

        picLabel = new JLabel();

        // Add the JLabel to the frame
        add(picLabel, BorderLayout.CENTER);

        setSize(400, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        receiveData();
    }

    private void receiveData() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String hostname = "127.0.0.1";
                    int port = 12345;

                    InetAddress address = InetAddress.getByName(hostname);
                    DatagramSocket socket = new DatagramSocket();

                    byte[] buffer = new byte[2]; //temporary size for now since i know its enough for the images

                    DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
                    socket.send(request);
                    //int count = 0;
                    for(int i = 0; i < 5; i++){
                        byte[] bufferResponse = new byte[2048 * 15];
                        DatagramPacket response = new DatagramPacket(bufferResponse, bufferResponse.length);
                        socket.receive(response);
                        System.out.println("Resposta recevida");
                        ByteArrayInputStream bais = new ByteArrayInputStream(response.getData());
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        PDU deserializedObject = (PDU) ois.readObject();

                        bais.reset();
                        ois.close();

                        System.out.println(deserializedObject.getString());
                    }
                } catch (Exception ex) {
                }

            }
        };
        //You can use a ThreadPool too.
        new Thread(runnable).start();
    }

    public static void main(String[] args) {
        try {
            new Cliente();
        } catch (IOException | ClassNotFoundException | InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
