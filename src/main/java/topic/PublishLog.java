package topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
exchangetype - topic
- podobne do direct, z tym ze:
    - '*' odpowiada dokladnie jednemu slowu
    - '#' odpowiada dowolnej liczbie slow (od 0)
    - uzycie tylko znaku # w binding key = zachowanie jak fanout
    - nie uzywanie * i # = jak w direct
 */
public class PublishLog
{
    private final static Logger logger = LoggerFactory.getLogger(PublishLog.class.getName());
    private static final String EXCHANGE_NAME = "topicc_logs";

    public static void main(String[] argv) throws Exception
    {
        //if(argv.length > 1)
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.exchangeDeclare(EXCHANGE_NAME, "topic");

                String routingKey = argv[0];
                String message = argv[1];
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                logger.info(" [x] Sent " + routingKey + " '" + message + "'");
            }
        }
    }
}