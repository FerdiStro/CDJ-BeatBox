package MinimalRunner;

import org.deepsymmetry.beatlink.DeviceFinder;
import org.main.util.Logger;

import java.net.SocketException;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {

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

        Logger.info("Devices found");

    }
}
