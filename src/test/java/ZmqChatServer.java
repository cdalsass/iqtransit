// javac -classpath .:/Users/cdalsass/experimental/0mq/jeromq/build/libs/jeromq-all.jar ZmqChatServer.java 
// java -classpath .:/Users/cdalsass/experimental/0mq/jeromq/build/libs/jeromq-all.jar ZmqChatServer

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class ZmqChatServer {
  public static void main(String[] args) {
    Context context = ZMQ.context(1);

    String host = "192.168.1.5";
    Socket pub = context.socket(ZMQ.PUB);
    pub.bind("tcp://" + host + ":5000");

    Socket receive = context.socket(ZMQ.PULL);
    receive.bind("tcp://" + host  + ":5001");

    while (!Thread.currentThread().isInterrupted()) {
      String message = receive.recvStr(0);
      System.out.println("Received: " + message);
      pub.send(message, 0);
    }

    receive.close();
    pub.close();
    context.term();
  }
}