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
        final ChatConnectionManager connectionManager = new ChatConnectionManager();
        MultiThreadedServer server2 = new MultiThreadedServer(8011, connectionManager);
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

        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    
                    Thread.sleep(2000);

                    System.out.println("writing something on client now");

                    while (true) {
                        try {
                            connectionManager.sendMessage("----HELLOW WORLD FROM SERVER");
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            System.out.println("caught exception e");
                        }
                    }

                    //System.out.println("stop trying to connect after 10s");
                    // sock_listener.setPaused(true);

                } catch (java.lang.InterruptedException e) {

                }

            }
        }).start();
        
             

                
    }
}