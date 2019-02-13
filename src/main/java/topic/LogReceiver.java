package topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LogReceiver {
    private final static Logger logger = LoggerFactory.getLogger(LogReceiver.class);
    private static final String EXCHANGE_NAME = "topicc_logs";
    private static final Date creationDate = new Date();

    public static void main(String[] argv) throws Exception {
        if (argv.length > 0)
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();
            for (String bindingKey : argv) {
                channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
                logger.info("Receiver waiting for " + bindingKey + " messages.");
            }

            logger.info("Logger[" + creationDate + "] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) ->
            {
                String message = new String(delivery.getBody(), "UTF-8");
                logger.info(" [x] Received '" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
    }
}
