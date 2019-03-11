import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class WrapperCode2 {

    private static File[] imageFile = {new File("/Users/ishan/webserver/public/knoparking/image1.png"),
            new File("/Users/ishan/webserver/public/knoparking/image2.png"),
            new File("/Users/ishan/webserver/public/knoparking/image3.png")};
    private static File[] statusFile = {new File("/Users/ishan/webserver/public/knoparking/status1.txt"),
            new File("/Users/ishan/webserver/public/knoparking/status2.txt"),
            new File("/Users/ishan/webserver/public/knoparking/status3.txt")};
    private static File[] licenseFile = {new File("/Users/ishan/webserver/public/knoparking/license1.txt"),
            new File("/Users/ishan/webserver/public/knoparking/license2.txt"),
            new File("/Users/ishan/webserver/public/knoparking/license3.txt")};

    public static void main(String[] args) {

        DataHandler2 dataHandler = new DataHandler2("172.20.10.6", 55555);

        while(true) {
            int id = dataHandler.getInt();
            dataHandler.receiveBufferedImage(imageFile[id - 1]);
            dataHandler.receiveString(licenseFile[id - 1]);
            dataHandler.receiveInt(statusFile[id - 1]);
        }
    }
}

class DataHandler2 {
    Socket webSocket;
    DataInputStream dis;

    DataHandler2(String ServerAddr, int ServerPort) {
        try {
            webSocket = new Socket(ServerAddr, ServerPort);
            dis = new DataInputStream(webSocket.getInputStream());
            System.out.println("Got connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveInt(File file) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int returnInt = 0;
        try {
            returnInt = dis.readInt();
            System.out.println("Received : " + returnInt);
        } catch (IOException e) {
            System.out.println("Could not Receive");
            e.printStackTrace();
        }
        pw.println(returnInt);
        System.out.println("Wrote int to file");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pw.close();
    }
    public int getInt() {
        int returnInt = 0;
        try {
            returnInt = dis.readInt();
            System.out.println("Received : " + returnInt);
        } catch (IOException e) {
            System.out.println("Could not Receive");
            e.printStackTrace();
        }
        return returnInt;
    }


    public void receiveString(File file) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String returnString = null;
        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(webSocket.getInputStream()));
            returnString = dis.readUTF();
            System.out.println("Received : " + returnString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.println(returnString);
        pw.close();

    }

    public void receiveBufferedImage(File file) {
        BufferedImage img;
        try {
            img = ImageIO.read(webSocket.getInputStream());
            ImageIO.read(webSocket.getInputStream());
            ImageIO.read(webSocket.getInputStream());
            if (img == null) {
                System.out.println("the image is null");
            } else {
                System.out.println("The image is nonnull");
            }
            ImageIO.write(img, "png", file);
        } catch (IOException ioe) {
            System.out.println("Error receiving BufferedImage");
        }
        System.out.println("Received image and wrote to file");
    }


}