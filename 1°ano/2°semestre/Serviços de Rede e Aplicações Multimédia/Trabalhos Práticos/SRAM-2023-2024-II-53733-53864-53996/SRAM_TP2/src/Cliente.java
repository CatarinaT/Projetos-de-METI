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
import java.util.ArrayList;
import java.util.Arrays;

public class Cliente extends JFrame {
    private BufferedImage img;
    private static JLabel Hour1Label = new JLabel();
    private static JLabel Hour2Label = new JLabel();
    private static JLabel Minute1Label = new JLabel();
    private static JLabel Minute2Label = new JLabel();
    private static JLabel Second1Label = new JLabel();
    private static JLabel Second2Label = new JLabel();
    private static JLabel DeciLabel = new JLabel();
    private static JLabel CentiLabel = new JLabel();
    private static JLabel MilliLabel = new JLabel();

    private static JLabel HourMinuteSeparatorLabel = new JLabel();
    private static JLabel MinuteSecondSeparatorLabel = new JLabel();
    private static JLabel SecondDeciSeparatorLabel = new JLabel();


    private static ArrayList<JLabel> labels = new ArrayList<>();
    private BufferedImage lastimg = null;

    public Cliente() throws IOException, ClassNotFoundException, InterruptedException, InvocationTargetException {
        setTitle("CLOCK");
        setLayout(new FlowLayout());
        getContentPane().setBackground(Color.BLACK);

        // Add the JLabel to the frame
        add(Hour1Label);
        add(Hour2Label);
        add(HourMinuteSeparatorLabel);
        add(Minute1Label);
        add(Minute2Label);
        add(MinuteSecondSeparatorLabel);
        add(Second1Label);
        add(Second2Label);
        add(SecondDeciSeparatorLabel);
        add(DeciLabel);
        add(CentiLabel);
        add(MilliLabel);

        labels.add(Hour1Label);
        labels.add(Hour2Label);
        labels.add(HourMinuteSeparatorLabel);
        labels.add(Minute1Label);
        labels.add(Minute2Label);
        labels.add(MinuteSecondSeparatorLabel);
        labels.add(Second1Label);
        labels.add(Second2Label);
        labels.add(SecondDeciSeparatorLabel);
        labels.add(DeciLabel);
        labels.add(CentiLabel);
        labels.add(MilliLabel);


        setSize(600, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        receiveData();
    }


    /**
     Receive data from server/forwarder
     */
    private void receiveData() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String hostname = "127.0.0.1";
                    int port = 54321; //forwarder port

                    InetAddress address = InetAddress.getByName(hostname);
                    DatagramSocket socket = new DatagramSocket();

                    byte[] buffer = new byte[2];

                    DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
                    socket.send(request);
                    //int count = 0;
                    while(true){
                        byte[] bufferResponse = new byte[2048 * 15];
                        DatagramPacket response = new DatagramPacket(bufferResponse, bufferResponse.length);
                        socket.receive(response);
                        System.out.println("Resposta recevida");


                        ByteArrayInputStream bais = new ByteArrayInputStream(response.getData());
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        PDU deserializedObject = (PDU) ois.readObject();

                        bais.reset();
                        ois.close();

                        if(deserializedObject.getA() >= ((System.currentTimeMillis() - deserializedObject.getTimeSentMillis())/1000) % 60) {

                            System.out.println(deserializedObject.getString());

                            for (int i = 0; i < deserializedObject.getData().size(); i++) {
                                img = ImageIO.read(new ByteArrayInputStream(deserializedObject.getData().get(i)));
                                final ImageIcon nextIcon = new ImageIcon(img);
                                img.flush();

                                int finalI = i;
                                java.awt.EventQueue.invokeLater(() -> {
                                    labels.get(finalI).setIcon(nextIcon);
                                    labels.get(finalI).repaint();
                                });
                            }
                        }
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
