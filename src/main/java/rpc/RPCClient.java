package rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient implements AutoCloseable
{
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public String call(String message) throws IOException, InterruptedException
    {
        //wysylanie calla
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes(StandardCharsets.UTF_8));

        //odbior wyniku
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        long time = System.currentTimeMillis();
        String ctag = channel.basicConsume(replyQueueName,
                true,
                (consumerTag, delivery) -> //BiConsumer
                {
                    if (delivery.getProperties().getCorrelationId().equals(corrId))
                    {
                        response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
                    }
                },
                consumerTag -> { });
        String result = response.take();
        System.out.println("Time: " + (System.currentTimeMillis() - time));
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}