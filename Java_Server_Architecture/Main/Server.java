/**
 * (C) Copyright Aditya Prerepa
 * Passer class for our main class.
 * Recieve Video Stream and picture stream
 * separately with different threads.
 */
public class Server {

    /**
     * All variables needed for I/O.
     */
    private int PictureStreamRPI_serverSocketPort;
    private int webServerSocketPort;
    private int VideoStreamRPI_serverSocketPort;
    private String webServerAddr;

    //TODO : Separate streaming ports

    /**
     * Left blank on purpose.
     */
    public Server() {

    }

    /**
     * Production constructor. Set all constants, pass to main,
     * set locally.
     * @param PictureStreamRPI_serverSocketPort
     * @param VideoStreamRPI_serverSocketPort
     * @param webServerSocketPort
     * @param RPI_server_Addr
     * @param RPI_server_Port_VideoStream
     * @param RPI_server_Port_PictureStream
     * @param webServer_Addr
     * @param webServer_port
     */
    public Server(int PictureStreamRPI_serverSocketPort, int VideoStreamRPI_serverSocketPort, int webServerSocketPort, String RPI_server_Addr, int RPI_server_Port_VideoStream, int RPI_server_Port_PictureStream, String webServer_Addr, int webServer_port) {
        this.PictureStreamRPI_serverSocketPort = PictureStreamRPI_serverSocketPort;
        this.webServerSocketPort = webServerSocketPort;
        this.VideoStreamRPI_serverSocketPort = VideoStreamRPI_serverSocketPort;
    }

    /**
     * Prod constructor.
     * @param PictureStreamRPI_serverSocketPort
     * @param webServerSocketPort
     */
    public Server( int PictureStreamRPI_serverSocketPort, String webServerAddr, int webServerSocketPort) {
        this.PictureStreamRPI_serverSocketPort = PictureStreamRPI_serverSocketPort;
        this.webServerSocketPort = webServerSocketPort;
        this.webServerAddr = webServerAddr;
    }

    RecieveRPIPictureStream rpiPictureStream = new RecieveRPIPictureStream(PictureStreamRPI_serverSocketPort, webServerAddr, webServerSocketPort);

    public void runMain() {

        runThreads();

    }
    RecieveRPIPictureStream rpiPictureStreamTest = new RecieveRPIPictureStream();

    private void runThreads() {
        rpiPictureStream.bindToRPIServer_Socket();
        rpiPictureStream.bindTowebServer_Socket();
        rpiPictureStream.run();
    }

    /**
     * Run individual streaming test.
     */
    private void test() {
        rpiPictureStreamTest.run();
    }


    public void runTest() {
        test();
    }
}
