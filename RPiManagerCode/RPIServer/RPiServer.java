// ADITYA: GO TO LINE 86

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

class ImageData {
    BufferedImage image;
    int imageMode;
    int RPiID;

    ImageData(BufferedImage image, int mode, int RPiID) {
        this.image = image;
        this.imageMode = mode;
        this.RPiID = RPiID;
    }
}


class RPiHandler extends Thread {
    Socket socket;
    InputStream inputStream;
    DataInputStream dataInputStream;
    int RPiID;

    boolean closed;

    RPiHandler(String host, int port, int RPiID) {
        try {
            socket = new Socket(host, port);
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            this.RPiID = RPiID;
            closed = false;
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!closed) {
            try {
                int imageMode = dataInputStream.readInt();
                BufferedImage image = ImageIO.read(inputStream);
                ImageIO.read(inputStream);
                ImageIO.read(inputStream);
                System.out.println("Getting data of RPi " + RPiID + ": image of size " + image.getWidth() + "x" + image.getHeight() + " and mode " + imageMode);
                Forwarder.toBeSent.add(new ImageData(image, imageMode, RPiID));
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
    }
}

class Forwarder extends Thread {
    static Queue<ImageData> toBeSent;

    static Socket socket;
    static DataOutputStream dos;

    boolean closed;

    Forwarder() throws Exception {
        toBeSent = new ConcurrentLinkedQueue<>();
        socket = new Socket("192.168.22.103", 50000);
        dos = new DataOutputStream(socket.getOutputStream());
        closed = false;
        start();
    }

    @Override
    public void run() {
        while (!closed) {
            if (!toBeSent.isEmpty()) {
                System.out.println("hey");
                ImageData extraction = toBeSent.remove();
                try {
                    // This is where my program sends information to your program.
                    // All the data I need to send is in the 'extraction' variable.
                    // You simply have to rewrite the code below to suite your socket needs.
                    // To run the RPi program, go to the three terminals on the finder window,
                    // and run 'java -cp :lib.jar: RPiClient'. That should be the last command
                    // run, so you can press up and enter. Then you run this server using IntelliJ. To demo it,
                    // put cars in front of the sensors. If anything here doesn't work, WAKE ME UP!!!
                    // DO NOT ATTEMPT TO FIX MY CODE YOURSELF!
                    // WAKE ME UP BEFORE CHANGING ANY LINES ~~~EXCEPT THIS TRY STATEMENT~~~!!!!
                    // DO NOT CHANGE THE RPi CODE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    // Also, if you're doing anything except adding the code to send info to your server, wake me up first.
                    // I rly don't want to sleep anyways. Also, I will always know my code better than you.
                    // This is all you have to do.
                    dos.writeInt(extraction.RPiID);
                    dos.writeInt(extraction.imageMode);
                    ImageIO.write(extraction.image, "png", socket.getOutputStream());
                    System.out.println("Sending data of RPi " + extraction.RPiID + ": image of size " + extraction.image.getWidth() + "x" + extraction.image.getHeight() + " and mode " + extraction.imageMode);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}

public class RPiServer {
    public static void main(String[] args) throws Exception {
        Forwarder forwarder = new Forwarder();
        RPiHandler rPiHandler1 = new RPiHandler("192.168.22.100", 13085, 1);
        RPiHandler rPiHandler2 = new RPiHandler("192.168.22.101", 13085, 2);
        RPiHandler rPiHandler3 = new RPiHandler("192.168.22.102", 13085, 3);

        while (true) {
        }
    }
}
