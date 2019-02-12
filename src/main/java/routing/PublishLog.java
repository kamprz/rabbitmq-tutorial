package routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
- binding - polaczenie pomiedzy kolejka a dyspozytorem
    - ustawiane poprzez: channel.queueBind(nazwaKolejki, nazwaDyspozytora, routingKey);
        - routingKey
            - interpretacja zalezna od rodzaju dyspozytora
            - ignorowany dla dyspozytora typu fanout, daje sie ""
            - dla dyspozytora typu direct - wiadomosci ida do kolejek o takim samym routingKey, mozliwe dowiazania na zasadzie N do M

 */
public class PublishLog
{
    private final static Logger logger = LoggerFactory.getLogger(PublishLog.class.getName());
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception
    {
        if(argv.length > 1)
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.exchangeDeclare(EXCHANGE_NAME, "direct");

                String severity = argv[0];
                String message = argv[1];
                channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
                logger.info(" [x] Sent " + severity + " '" + message + "'");
            }
        }
    }
}