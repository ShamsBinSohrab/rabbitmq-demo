import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class Consume {

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
        channel.basicConsume(QUEUE_NAME, true, getDeliverCallback(latch), getCancelCallback());
        System.out.println(latch.getCount());
      }
    } catch (IOException | TimeoutException ex) {
      System.err.println(ex.getMessage());
    }
    latch.await();
  }

  private static DeliverCallback getDeliverCallback(CountDownLatch latch) {
    return (tag, delivery) -> {
      var message = new String(delivery.getBody());
      System.out.println("Received at " + Thread.currentThread() + " : " + message);
      latch.countDown();
    };
  }

  private static CancelCallback getCancelCallback() {
    return tag -> System.out.println("Cancel: " + tag);
  }
}
