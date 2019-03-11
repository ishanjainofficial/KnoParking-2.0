import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class LocalPictureTest {


    public static void main(String[] args) {
        File file = new File("/home/aditya/tests/pictureTests/test18.jpg");
        int status = 1;
        int port = 33333;
        int id = 2;
        interactWithLocalhost(file, status, id, port);
    }

    public static void interactWithLocalhost(File file, int status, int id, int port) {
        Socket localSocket = null;
        BufferedImage img = null;
        try {
            localSocket = new Socket("localhost", port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            img = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Image trying to send to localhost:33333");

        try {
            ImageIO.write(img, "jpg", localSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            OutputStream os = localSocket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.print(id);
            pw.print(status);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Image sent");



    }

}


