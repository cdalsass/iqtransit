/* 
javac  -cp .:build/libs/iqtransit.jar:lib/socketio.jar  SocketIOListenerTest.java
java -cp .:build/libs/iqtransit.jar:lib/socketio.jar  SocketIOListenerTest
*/

import com.iqtransit.socketio.SocketIOListener;
import java.util.Date;

public class SocketIOListenerTest {

    public static void main(String[] args) {

        SocketIOListener sock_listener;

        try {

            sock_listener = new SocketIOListener("stage.doodle4.com", 8011);
            sock_listener.start();

            sock_listener.setSocketIOEventListener(new SocketIOListener.SocketIOEventListener() {
                public void onSocketIOEvent(String event, Object... args) {
                    System.out.println("HERE NOW HELLO WORLD with arg[0] " + args[0]);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    


}

