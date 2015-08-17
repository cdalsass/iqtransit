
// javac -classpath /Users/cdalsass/experimental/0mq/jeromq/build/libs/jeromq-all.jar:/Users/cdalsass/dev/iqtransit/build/libs/iqtransit.jar:. ZmqChatRunner.java 

// java -classpath /Users/cdalsass/experimental/0mq/jeromq/build/libs/jeromq-all.jar:/Users/cdalsass/dev/iqtransit/build/libs/iqtransit.jar:. ZmqChatRunner
import com.iqtransit.socket.*; 

public final class ZmqChatRunner {

	public void testWithDisconnect() {

		while (true) {

			final ZmqChatClient chat = new ZmqChatClient("tcp://192.168.1.5:5001","tcp://192.168.1.5:5000","tcp://localhost:5000");
							
			chat.connect();
			// we still continue to run after the call to connect. 
			
			chat.send("start by sending one message from " + Thread.currentThread().getName());

	    	chat.setClientEventListener(new ZmqChatClient.ClientEventListener() {
	            public void onClientEvent(String event) {
	                
	                System.out.println("Received message back with event " + event + " on thread = " + Thread.currentThread().getName());
	                System.out.flush();

	            }
	        });

	        try {
	        	Thread.sleep(3000);
	        	System.out.println("wait a sec...");
	        } catch (InterruptedException e) {

	        }

	        /* this disconnect doesn't work properly in a loop. However, due to the "blocking" interface which ZMQ provides, we don't get a callback for disconnect state. This is actually OK in Android's Activity context. */

	        chat.disconnect();

	        try {
	            Thread.sleep(3000);
	        	System.out.println("wait a sec...");
	        } catch (InterruptedException e) {

	        }

		}



	}


	public static void main (String[] args) {

		ZmqChatRunner zmr = new ZmqChatRunner();
		zmr.testWithDisconnect();

	}
}