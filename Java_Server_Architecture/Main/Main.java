public class Main {

    private static int webServerPort = 55555;
    private static int RPI_PictureStream_ServerSocket = 50000;
    public static String webServerAddr = "172.20.10.2";

    public static void main(String[] args) {
        Server testServer = new Server(RPI_PictureStream_ServerSocket, webServerAddr, webServerPort);
        testServer.runMain();
    }
}
