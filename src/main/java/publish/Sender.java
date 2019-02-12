package publish;

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
    - ustawiane poprzez: channel.queueBind(nazwaKolejki, nazwaDyspozytora, routingKey); routingKey - pozniej, mozna dac ""
 */
public class Sender {
    private final static String QUEUE_NAME = "task_queue";
    private final static Logger logger = LoggerFactory.getLogger(Sender.class.getName());
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");   //tu adres IP
        try(Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            boolean durable = true;
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            channel.exchangeDeclare("logs", "fanout");
            String message = String.join(" ", argv);
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            logger.info(" [x] Sent '" + message + "'");
        }
    }
}