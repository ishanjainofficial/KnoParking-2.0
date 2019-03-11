/**
 * @author Aditya Prerepa
 * Copyright (C) Aditya Prerepa
 * MenloHacks project Class.
 */


import io.socket.IOAcknowledge;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import javax.imageio.ImageIO;
import io.socket.IOCallback;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;

/**
 * This class receives the objects from our server
 * mediator for the Raspberry Pis and sends them to the website in a
 * Thread. A thread is required for constant receiving and sending
 * of pictures, there cannot be another deadlocking function in
 * front of when this is called. This is because we do not know
 * when we will get sent data, in this case, Image streams.
 */
public class RecieveRPIPictureStream extends Thread implements IOCallback {

    /**
     * Necessary constants; Sockets and ServerSockets
     * established here.
     */
    private ServerSocket RPI_PictureStream_ServerSocket;
    private Socket webServer_Socket;
    private Socket recievePicturesRPISocket;
    private String webServerAddr;
    private ServerSocket webServer_ServerSocket;
    private File saveTo;
    private DetectText dt;
    private int webServerSocketPort;
    private SocketIO socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    public RecieveRPIPictureStream() {
        // TODO : Car confimation - from akshay take pic only then
        saveTo = new File("/home/aditya/tests/pictureTests/test18.png");
        dt = new DetectText(saveTo);
    }

    /**
     * Main constructor. The local variables are established here,
     * and the RPI_PictureStream_ServerSocket serverSocket is established
     * here, just once. Do not be afraid to use multiple serverSockets.
     * @param PictureStreamRPI_serverSocketPort
     * @param webServerSocketPort
     */
    public RecieveRPIPictureStream(int PictureStreamRPI_serverSocketPort, String webServerAddr, int webServerSocketPort) {
        this.webServerSocketPort = webServerSocketPort;
        this.webServerAddr = webServerAddr;
        saveTo = new File("/home/aditya/tests/pictureTests/test6.png");
        try {
            RPI_PictureStream_ServerSocket = new ServerSocket(50000);
            System.out.println("Created server for RPI socket");
        } catch (IOException ioe) {
            System.err.println("Error Creating Input/Ouput Streams for RPI server socket.");
            ioe.printStackTrace();
        }

        dt = new DetectText(saveTo);


        try {
            webServer_ServerSocket = new ServerSocket(55555);
            System.out.println("Created web server socket");
        } catch (IOException ioe) {
            System.err.println("Error Creating Input/Ouput Streams for webServer server socket.");
            ioe.printStackTrace();
        }
    }

    /**
     * Bind the RPI socket to the server socket, in reality,
     * the information being sent is being regulated by a mediator.
     */
    public void bindToRPIServer_Socket() {
        try {
            System.out.println("Waiting for RPI connection...");
            recievePicturesRPISocket = RPI_PictureStream_ServerSocket.accept();
//            recievePicturesRPISocket = new Socket(RPI_server_Addr, RPI_server_Port);
            System.out.println("Bound to RPI");
        } catch (UnknownHostException UHE) {
            System.err.println("Unknown Host, Check MacBookPro IP, May have changed.");
            UHE.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("IO Exception");
            ioe.printStackTrace();
        }

        try {
            dis = new DataInputStream(recievePicturesRPISocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Bind to the Web Server's socket. This will
     * establish the connections, so streams can happen.
     */
    public void bindTowebServer_Socket() {
        System.out.println("Waiting for wbserver connection...");
        try {
            webServer_Socket = webServer_ServerSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dos = new DataOutputStream(webServer_Socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.printf("Got connection from %s:%d\n", webServer_Socket.getInetAddress(), webServer_Socket.getPort());
//        socket = new SocketIO();
//        String url = String.format("http://172.20.10.2:55555/");
//        try {
//            socket.connect(url, this);
//            System.out.println("Created web server socket");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * Thread's Main method. This run concurrent to our Application's
     * main thread. In a nutshell, this thread receives the image and ID
     * of the RPI, and sends the image and ID as well as the ML License to
     * the web server.
     *
     *
     * Control Flow :
     * ( 1 ) Recieve image
     * ( 2 ) Recieve Car ID
     * ( 3 ) Recieve Car Status {@link RecieveRPIVideoStream}
     * ( 4 ) Use google vision {@link DetectText}, return string of plate
     * ( 5 ) Parse XML, send all data to server.
     */
    @Override
    public void run() {
        while (true) {
            int id ;
            int status;
            String plateRecog = "No Car";
            id = recieveInt();
            System.out.println("Id : " + id);
            status = recieveInt();
            System.out.println("Status : " + status);
            recieveImage(saveTo);
            System.out.println("Recieved Image.");

            if (status == 1) {
                System.out.println("Contacting Google Vision");
                plateRecog = dt.mainMethod();;
                System.out.println(plateRecog);
            }


            sendIDToWebServer(id);
            sendImageToWebServer(saveTo);
            sendLicenseToWebServer(plateRecog);
            sendStatusToWebServer(status);
        }

    }

    /**
     * Receive image from server connected to RPI, save to
     * file.
     * @param saveImageTo
     */
    private void recieveImage(File saveImageTo) {
        BufferedImage recievedImage = null;
        try {
            recievedImage = ImageIO.read(recievePicturesRPISocket.getInputStream());
            ImageIO.read(recievePicturesRPISocket.getInputStream());
            ImageIO.read(recievePicturesRPISocket.getInputStream());
        } catch (IOException ioe) {
            System.out.println("Error recieving BufferedImage");
        }
        try {
            ImageIO.write(recievedImage, "png", saveImageTo);
        } catch (IOException ioe) {
            System.err.println("Error writing to file");
        }
    }

    private int recieveInt() {
        int status = 0;

        try {
            status = dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }



    /**
     *
     * @param file
     */
    private void sendImageToWebServer(File file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(img, "png", webServer_Socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String encodedImage = imgToBase64String(img, "jpg");
//        System.out.printf("Image trying to send to %s : %d", webServer_Socket.getInetAddress(), webServer_Socket.getPort());
//        socket.send(encodedImage);
//        System.out.println(encodedImage);
//        System.out.println("Sent Car image to website");

    }

    private void sendIDToWebServer(int id) {

        try {
            dos.writeInt(id);
        } catch (Exception e ) {
            e.printStackTrace();
            System.out.println("f");
        }
//        String sendString = id.toString();
//        System.out.printf("ID trying to send to %s : %d", webServer_Socket.getInetAddress(), webServer_Socket.getPort());
//        socket.send(sendString);
//        System.out.println("Sent ID to website.");
    }



    private void webServerTest() {
        SocketIO socketIO;

        socketIO = new SocketIO();

    }


    private void sendLicenseToWebServer(String plate) {
        System.out.printf("Plate trying to send to %s : %d\n", webServer_Socket.getInetAddress(), webServer_Socket.getPort());
        try {
            OutputStream os = webServer_Socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            dos.writeUTF(plate);
            // pw.print(plate + "\n\r");
            //pw.println(plate);
            //dos.write(plate.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendStatusToWebServer(int status) {
        System.out.printf("Status trying to send to %s : %d\n", webServer_Socket.getInetAddress(), webServer_Socket.getPort());
        try {
            dos.writeInt(status);
        } catch (Exception e) {
            System.out.println("Crap");
            e.printStackTrace();
        }
        System.out.println("Send Car status to Web server");
    }


    /**
     * For Akshay's client application:
     * Sends data in XML.
     *
     * Recieves Data..
     * then it can save it to a file. All given
     * in header.
     * @param xmlPath
     * @param carID
     * @param carImage
     * @param carStatus
     */
    private void recieveDataXML(File xmlPath, BufferedImage carImage, Integer carID, Integer carStatus, String license) {

        DocumentBuilder documentBuilder = null;
        String encodedImage = imgToBase64String(carImage, "jpg");
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("data");
        document.appendChild(root);

        Element elements = document.createElement("elements");
        root.appendChild(elements);

        Element image = document.createElement("ImageInBase64");
        image.appendChild(document.createTextNode(encodedImage));
        elements.appendChild(image);

        Element carId = document.createElement("ID");
        carId.appendChild(document.createTextNode(carID.toString()));
        elements.appendChild(carId);

        Element carStat = document.createElement("carStatus");
        carStat.appendChild(document.createTextNode(carStatus.toString()));
        elements.appendChild(carStat);

        Element carLisence = document.createElement("license");
        carLisence.appendChild(document.createTextNode(license));
        elements.appendChild(carLisence);


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlPath.toString()));

            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Wrote to XML");

        sendXMLFile(xmlPath);
    }

    private void sendXMLFile(File xmlFile) {

        byte[] bytes = new byte[4096];
        OutputStream os = null;
        try {
            os = recievePicturesRPISocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = null;
        try {
            is = new FileInputStream(xmlFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int count = 0;
        while (true) {
            try {
                if (!((count = is.read(bytes)) > 0)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.write(bytes, 0, count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        System.out.println("Sent file!");
        /**
         * -- Do your socket stuff here
         */
    }


    /**
     * Encode given image to string
     * @param img
     * @param formatName
     * @return
     */
    public static String imgToBase64String(BufferedImage img, String formatName)
    {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try
        {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        }
        catch (final IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }

    @Override
    public void onMessage(JSONObject json, IOAcknowledge ack) {
        try {
            System.out.println("Server said:" + json.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String data, IOAcknowledge ack) {
        System.out.println("Server said: " + data);
    }

    @Override
    public void onError(SocketIOException socketIOException) {

    }

    @Override
    public void onDisconnect() {
        System.out.println("Connection terminated.");
    }

    @Override
    public void onConnect() {
        System.out.println("Connection established");
    }

    @Override
    public void on(String event, IOAcknowledge ack, Object... args) {
        System.out.println("Server triggered event '" + event + "'");
    }


}
