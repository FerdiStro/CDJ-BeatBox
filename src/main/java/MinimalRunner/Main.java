package MinimalRunner;

import org.deepsymmetry.beatlink.DeviceFinder;
import org.main.util.Logger;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws SocketException {


        /**
         * Network part for Linux
         */
        System.setProperty("java.net.preferIPv4Stack", "true");

        System.out.println("--- Interface Diagnose ---");
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.isUp() && !ni.isLoopback()) {
                System.out.println("Interface: " + ni.getName());
                System.out.println("  - Supports Multicast: " + ni.supportsMulticast());
                System.out.println("  - Is PointToPoint: " + ni.isPointToPoint());

            }
        }
        System.out.println("--------------------------");



        int unattempted = 0;
        boolean deviceFound = false;


        while (unattempted < 10 || deviceFound) {
            Logger.debug("Attempt to find Device. Attemp: " + unattempted);
            try {
                DeviceFinder deviceFinder = DeviceFinder.getInstance();
                deviceFinder.start();

                //wait for finding
                sleep(5000);

                if (!deviceFinder.getCurrentDevices().isEmpty()) {
                    deviceFound = true;
                    Logger.info("Devices found");
                    return;
                }
                //Nothing found
                unattempted++;

            } catch (SocketException e) {
                Logger.error(e.toString());
            } catch (InterruptedException e) {
                Logger.error(e.toString());
            }
        }
    }
}
