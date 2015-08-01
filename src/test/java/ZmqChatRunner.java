
// javac -classpath /Users/cdalsass/experimental/0mq/jeromq/build/libs/jeromq-all.jar:/Users/cdalsass/dev/iqtransit/build/libs/iqtransit.jar:. ZmqChatRunner.java 

// java -classpath /Users/cdalsass/experimental/0mq/jeromq/build/libs/jeromq-all.jar:/Users/cdalsass/dev/iqtransit/build/libs/iqtransit.jar:. ZmqChatRunner

import com.iqtransit.socket.*; 

public class ZmqChatRunner {


	public static void main (String[] args) {

		final ZmqChatClient chat = new ZmqChatClient("tcp://localhost:5001","tcp://localhost:5000","ipc://anything");
		
		chat.connect();
		// we still continue to run after the call to connect. 
		
		chat.send("start by sending one message");

		new Thread()
		{
		    public void run() {
		    	while (true) {
					try {
			       		System.out.println("sending message now from thread " + Thread.currentThread().getName());
						chat.send("hi there");
			        	Thread.sleep(3000);
				      } catch (InterruptedException e) {
				        System.out.println("caught InterruptedException");
				      }
						
				    }
		    	}

		}.start();

		chat.setClientEventListener(new ZmqChatClient.ClientEventListener() {
            public void onClientEvent(String event) {
                System.out.println("YAY GOT CLIENT with event " + event + " on thread = " + Thread.currentThread().getName());
                System.out.flush();
            }
        });

	}
}