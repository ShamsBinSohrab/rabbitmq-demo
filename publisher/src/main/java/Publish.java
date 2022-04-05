import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class Publish {

  private static final String QUEUE_NAME = "hello";
  private static final String EXCHANGE_NAME = "";

  public static void main(String[] args) throws InterruptedException {
    var latch = new CountDownLatch(10);
    var factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setPort(567);

    try (var connection = factory.newConnection()) {
      try (var channel = connection.createChannel()) {
        channel.queueDeclare(QUEUE_NAME, false, false, false, Map.of());

        while (latch.getCount() > 0) {
          var message = "hello world! =>  " + latch.getCount();
          channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, message.getBytes());
          System.out.println("Message sent: " + message);
          latch.countDown();
        }
      }
    } catch (IOException | TimeoutException ex) {
      System.err.println(ex.getMessage());
    }
    latch.await();
  }
}
