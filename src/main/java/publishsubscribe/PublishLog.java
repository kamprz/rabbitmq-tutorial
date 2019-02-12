package publishsubscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
Mechanizm subskrypcji/pulikacji : wiadomosc dostarczana do wielu odbiorcow
Exchange - dyspozytor wiadomosci: mechanizm kolejkowania rabbita to: producenci -> dyspozytor(zy) -> kolejki -> odbiorcy
exchange types:
    - direct
    - topic
    - headers
    - fanout - do wszystkich znanych dyspozytorowi kolejek
- w metodzie channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes()); pierwszy string to nazwa dyspozytora, pusty to domyslny
- metoda channel.queueDeclare() tworzy non-durable, exclusive, autodelete queue z wygenerowana nazwa, exclusive dla danego odbiorcy
- binding - polaczenie pomiedzy kolejka a dyspozytorem
    - ustawiane poprzez: channel.queueBind(nazwaKolejki, nazwaDyspozytora, routingKey);
        - routingKey - ignorowany dla dyspozytora typu fanout, daje sie ""
        - routingKey - interpretacja zalezna od rodzaju dyspozytora
 */
public class PublishLog
{
    private final static Logger logger = LoggerFactory.getLogger(PublishLog.class.getName());
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = argv.length < 1 ? "info: Hello World!" : String.join(" ", argv);

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            logger.info(" [x] Sent '" + message + "'");
        }
    }
}