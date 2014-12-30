import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.iqtransit.socket.Client;
import java.util.Date;

public class TestSocketClient {
	
   @Test
   public void testPrintMessage() {

      	final Client sock_listener = new Client( "localhost", 8011);
      	final Thread clientthread = new Thread(sock_listener);
        clientthread.start();
        sock_listener.sendMessage("HI CHARLIE");

        sock_listener.setClientEventListener(new Client.ClientEventListener() {
                public void onClientEvent(String event) {
                    System.out.println("HERE NOW HELLO WORLD with event " + event);
                    System.out.flush();
                }
            });

        assertEquals(1,1);
  
/*
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    
                    Thread.sleep(2000);

                    System.out.println("writing something on client now");

                    while (true) {
                        try {
                            sock_listener.sendMessage("----HELLOW WORLD FROM CLIENT");
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            System.out.println("caight exception e");
                        }
                    }

                    //System.out.println("stop trying to connect after 10s");
                    
                   // sock_listener.setPaused(true);



                } catch (java.lang.InterruptedException e) {

                }

            }
        }).start();
        
      */          
    } 
}