package workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;


/*
mechanizm 'message acknowledgments'
- jak ustawimy manualne, to odbiorca musi okreslic ze obsluzyl wiadomosc
- jak w trakcie zginie to jego zadanie zostaje w pierwszej kolejnosci przekazane innemu odbiorcy
mechanizm message durability
- jak serwer rabbita pada to zapisuje stan na dysku
- trzeba ustawic zarowno wiadomosci i kolejki jako durable
    - kolejka jako parametr durable=true
    - wiadomosc jako parametr BasicProperties= MessageProperties.PERSISTENT
    - cos jeszcze -> https://www.rabbitmq.com/confirms.html
*/
public class Receiver {
    private final static String QUEUE_NAME = "task_queue";
    private final static Logger logger = LoggerFactory.getLogger(Receiver.class);

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        channel.basicQos(1);    //mozna mu przydzielic tylko jedna wiadomosc na raz, inaczej sa dla niego konkretnie kolejkowane, czekaja az skonczy
        logger.info(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            logger.info(" [x] Received '" + message + "'");
            try{
                doWork(message);
            }
            catch (InterruptedException e)
            {
                logger.info("Caught InterruptedException.");
            }
            finally {
                logger.info(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);   //trzeba okreslic ze wiadomosc zostala osluzona
            }
        };
        //true - wystarczy zlapac wiadomosc ze strony odbiorcy i zostaje ustawiona jako obsluzona
        //false - trzeba recznie ustawic, ze wiadomosc zostala obsluzona, jak w finally
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
            if (ch == '.') Thread.sleep(1000);
        }
    }
}
