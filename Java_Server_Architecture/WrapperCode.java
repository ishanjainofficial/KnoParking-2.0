import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class WrapperCode {

    private static File imageFile = new File("");
    private static File idFile = new File("");
    private static File statusFile = new File("");
    private static File licenseFile = new File("");

    public static void main(String[] args) {
        DataHandler dataHandler = new DataHandler("172.16.3.20", 55555);
        while(true) {
            dataHandler.recieveBufferedImage(imageFile);
            dataHandler.recieveInt(idFile);
            dataHandler.recieveInt(statusFile);
            dataHandler.recieveString(licenseFile);
        }

    }
}

class DataHandler {

    Socket webSocket;

    DataHandler(String ServerAddr, int ServerPort) {
        try {
            webSocket = new Socket(ServerAddr, ServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recieveInt(File file) {
        delete(file.toString(), 1, 1);
        Writer wr = null;
        try {
            wr = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int returnInt = 0;
        try {
            InputStream is = webSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            returnInt = br.read();
        } catch (IOException e) {
            System.out.println("fuck you");
            e.printStackTrace();
        }
        try {
            wr.write(String.valueOf(returnInt));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Wrote int to file");
    }

    public void recieveString(File file) {
        delete(file.toString(), 1, 1);
        Writer writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            System.out.println("Crap");
            e.printStackTrace();
        }
        String returnString = null;
        try {
            InputStream is = webSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            returnString = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writer.write(returnString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recieveBufferedImage(File file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(ImageIO.createImageInputStream(webSocket.getInputStream()));
            ImageIO.write(img, "jpg", file);
        } catch (IOException ioe) {
            System.out.println("Error recieving BufferedImage");
        }
    }

    public void delete(String filename, int startline, int numlines)
    {
        try
        {
            BufferedReader br=new BufferedReader(new FileReader(filename));

            //String buffer to store contents of the file
            StringBuffer sb=new StringBuffer("");

            //Keep track of the line number
            int linenumber=1;
            String line;

            while((line=br.readLine())!=null)
            {
                //Store each valid line in the string buffer
                if(linenumber<startline||linenumber>=startline+numlines)
                    sb.append(line+"\n");
                linenumber++;
            }
            if(startline+numlines>linenumber)
                System.out.println("End of file reached.");
            br.close();

            FileWriter fw=new FileWriter(new File(filename));
            //Write entire string buffer into the file
            fw.write(sb.toString());
            fw.close();
        }
        catch (Exception e)
        {
            System.out.println("Something went horribly wrong: "+e.getMessage());
        }
    }

}
