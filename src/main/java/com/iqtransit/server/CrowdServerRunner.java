/* 
javac  -cp .:build/libs/iqtransit.jar MultiThreadedServerTest.java 
java -cp .:build/libs/iqtransit.jar MultiThreadedServerTest
*/
package com.iqtransit.server;
import com.iqtransit.server.MultiThreadedServer;
import com.iqtransit.server.LocatableItemList;
import java.util.Date;

public class CrowdServerRunner {

    public static void main(String[] args) {

        LocatableItemList passengerlist = new LocatableItemList();

        System.out.println("open connection to port 9000");
        MultiThreadedServer server = new MultiThreadedServer(9000, new HttpConnectionManager());
        new Thread(server).start();
 
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