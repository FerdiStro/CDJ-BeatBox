package MinimalRunner;

import org.deepsymmetry.beatlink.*;
import org.main.settings.Settings;
import org.main.util.Logger;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws Exception {


        /*
          Network part for Linux
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

        DeviceFinder deviceFinder = DeviceFinder.getInstance();

        while (unattempted < 10 || !deviceFound) {
            Logger.debug("Attempt to find Device. Attemp: " + unattempted);
            try {

                deviceFinder.start();

                //wait for finding
                sleep(5000);

                if (!deviceFinder.getCurrentDevices().isEmpty()) {
                    deviceFound = true;
                    Logger.info("Devices found");
                }
                //Nothing found
                unattempted++;

            } catch (SocketException e) {
                Logger.error(e.toString());
            } catch (InterruptedException e) {
                Logger.error(e.toString());
            }
        }

        if(!deviceFound){
            Logger.error("No Device Found");
            return;
        }


            System.out.println("--- Devices: ---");
            for (DeviceAnnouncement currentDevice : deviceFinder.getCurrentDevices()) {
                System.out.println("Device: " + currentDevice.getName());
                System.out.println("  - InetAddress: " + currentDevice.getAddress());
                System.out.println("  - Number: " + currentDevice.getDeviceNumber());
            }
            System.out.println("--------------------------");


            Logger.info("Start Virtual Device");
            VirtualCdj cdj = VirtualCdj.getInstance();
            cdj.setDeviceNumber((byte) 4);

            cdj.start();
            cdj.setSynced(true);
            cdj.setPlaying(true);
            cdj.setSendingStatus(true);


            BeatFinder beatFinder = BeatFinder.getInstance();

            beatFinder.addBeatListener(new BeatListener() {
                @Override
                public void newBeat(Beat beat) {
                    System.out.println("_________________");
                    System.out.println("Beat");
                    System.out.println("    -BPM" + beat.getBpm());
                    System.out.println("    -Dev-Name" + beat.getDeviceName());
                    System.out.println("    -Next-Bar" + beat.getNextBar());
                    System.out.println("_________________");
                }
            });
    }

}
