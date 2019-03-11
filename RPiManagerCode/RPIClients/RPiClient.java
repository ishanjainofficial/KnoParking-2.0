import com.hopding.jrpicam.*;
import com.pi4j.io.gpio.*;

import javax.imageio.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;

import static java.lang.Math.*;

class HCSR04 extends Thread {
    private GpioController gpioController;
    private GpioPinDigitalOutput trig;
    private GpioPinDigitalInput echo;

    private long limit;
    private long accuracy;
    private long cmp;
    private int lastLong;
    private int count;
    private boolean isFound;
    boolean close;

    HCSR04(Pin trig, Pin echo, long limit, long accuracy) {
        gpioController = GpioFactory.getInstance();
        this.trig = gpioController.provisionDigitalOutputPin(trig);
        this.echo = gpioController.provisionDigitalInputPin(echo);
        this.limit = limit;
        this.accuracy = accuracy;
        this.cmp = -1;
        this.lastLong = 0;
        this.count = 0;
        isFound = false;
        close = false;
        start();
    }

    private void gpioClose() {
        trig.low();
        gpioController.shutdown();
    }

    private boolean isCmpReset() {
        return cmp == -1;
    }

    private void resetCmp() {
        cmp = -1;
        count = 0;
    }

    private long getDistanceProcess() throws InterruptedException {
        trig.high();
        Thread.sleep((long) 0.01);
        trig.low();
        long TOstart = System.currentTimeMillis();
        while (echo.isLow() && ((System.currentTimeMillis() - TOstart) < 1500)) {
        }
        long start = System.nanoTime();
        while (echo.isHigh()) {
        }
        long end = System.nanoTime();
        return  (end - start) / 10000;
    }

    private void logDistance(long distance) {
        System.out.println("Distance:" + distance + " cmp:" + cmp + " count:" + count + " lastLong:" + lastLong);
        if (!isFound) {
            if (distance > limit) {
                lastLong++;
                if (3 <= lastLong) {
                    resetCmp();
                }
            } else {
                if (isCmpReset()) {
                    cmp = distance;
                } else if (abs(cmp - distance) >= accuracy) {
                        resetCmp();
                } else {
                    lastLong = 0;
                    if (count == 10) {
                        isFound = true;
                        RPiClient.imageMode = 2;
                        resetCmp();
                    }
                    count++;
                }
            }
        } else {
            if (distance > limit) {
                if (3 <= lastLong) {
                    isFound = false;
                    RPiClient.imageMode = 1;
                    resetCmp();
                }
                lastLong++;
            } else {
                lastLong = 0;
            }
        }
    }

    @Override
    public void run() {
        try {
            trig.low();
            Thread.sleep(1000);
            while (!close) {
                Thread.sleep(100);
                logDistance(getDistanceProcess());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            gpioClose();
        }
    }
}

class Connection extends Thread {
    ServerSocket listener;
    Socket connection;
    DataOutputStream dos;
    boolean closed;

    Connection() throws Exception {
        listener = new ServerSocket(13085);
        connection = null;
        dos = null;
        closed = false;
        start();
    }

    @Override
    public void run() {
        while (!closed) {
            if (connection == null) {
                try {
                    connection = listener.accept();
                    dos = new DataOutputStream(connection.getOutputStream());
                } catch (IOException e) {
                    connection = null;
                }
            }
        }
    }

    void send(BufferedImage image, int i) {
        if (connection != null && dos != null) {
            try {
                dos.writeInt(i);
                ImageIO.write(image, "png", connection.getOutputStream());
            } catch (java.net.SocketException e) {
                connection = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }
}


public class RPiClient {
    static int imageMode;

    public static void main(String[] args) throws Exception {
        Connection connection = new Connection();
        HCSR04 hcsr04 = new HCSR04(RaspiPin.GPIO_09, RaspiPin.GPIO_07, 140, 10);
        RPiCamera rPiCamera = new RPiCamera();
        while (true) {
            if (imageMode != 0) {
                int c = imageMode;
                imageMode = 0;
                connection.send(rPiCamera.takeBufferedStill(), c -  1);
            }
        }
    }
}
