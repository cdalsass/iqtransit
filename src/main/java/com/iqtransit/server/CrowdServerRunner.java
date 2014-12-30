/* 
javac  -cp .:build/libs/iqtransit.jar CrowdServerRunner.java 
java -cp .:build/libs/iqtransit.jar com.iqtransit.server.CrowdServerRunner
*/
package com.iqtransit.server;
import com.iqtransit.server.MultiThreadedServer;
import com.iqtransit.server.LocatableList;
import java.util.Date;

public class CrowdServerRunner {

    public static void main(String[] args) {

        LocatableList passengerlist = new LocatableList();

        System.out.println("open connection to port 8011 in thread " + Thread.currentThread().getId());
        MultiThreadedServer server2 = new MultiThreadedServer(8011, new ChatConnectionManager());
        new Thread(server2).start();

        /*
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Stopping Server");
        server.stop();
        */
                
    }
}