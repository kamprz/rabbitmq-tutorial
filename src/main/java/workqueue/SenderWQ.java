package workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenderWQ {
    private final static String QUEUE_NAME = "task_queue";
    private final static Logger logger = LoggerFactory.getLogger(SenderWQ.class.getName());
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");   //tu adres IP
        try(Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            boolean durable = true;
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            String message = String.join(" ", argv);
            channel.basicPublish("", QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            logger.info(" [x] Sent '" + message + "'");
        }

    }
}