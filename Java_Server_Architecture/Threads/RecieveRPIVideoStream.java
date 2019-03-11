import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class RecieveRPIVideoStream extends Thread {

    private ServerSocket RPI_VideoStream_ServerSocket;
    private ServerSocket webServer_ServerSocket;
    private Socket webServer_Socket;
    private Socket recieveVideoRPI_Stream;
    private String RPI_server_Addr;
    private int RPI_server_Port;
    private String webServer_Addr;
    private int webServer_port;

    public RecieveRPIVideoStream (int VideoStreamRPI_serverSocketPort, int webServerSocketPort, String RPI_server_Addr, int RPI_server_Port, String webServer_Addr, int webServer_port) {
        this.RPI_server_Addr = RPI_server_Addr;
        this.RPI_server_Port = RPI_server_Port;
        this.webServer_Addr = webServer_Addr;
        this.webServer_port = webServer_port;
        try {
            RPI_VideoStream_ServerSocket = new ServerSocket(VideoStreamRPI_serverSocketPort);
        } catch (IOException ioe) {
            System.err.println("Error Creating Input/Ouput Streams for RPI server socket.");
            ioe.printStackTrace();
        }
        try {
            webServer_ServerSocket = new ServerSocket(webServerSocketPort);
        } catch (IOException ioe) {
            System.err.println("Error Creating Input/Ouput Streams for webServer server socket.");
            ioe.printStackTrace();
        }
    }


    @Override
    public void run() {

    }

    public void bindToRPI_Socket_VideoStream() {
        try {
            recieveVideoRPI_Stream = new Socket(RPI_server_Addr, RPI_server_Port);
            recieveVideoRPI_Stream = RPI_VideoStream_ServerSocket.accept();
        } catch (UnknownHostException UHE) {
            System.err.println("Unknown Host, Check MacBookPro IP, May have changed.");
            UHE.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("IO Exception");
            ioe.printStackTrace();
        }
    }

    public void bindToWebserver_Socket_VideoStream() {
        try {
            webServer_Socket = new Socket(webServer_Addr, webServer_port);
            webServer_Socket = webServer_ServerSocket.accept();
        } catch (UnknownHostException UHE) {
            System.err.println("Host not known");
            UHE.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O Error");
            e.printStackTrace();
        }
    }


}
