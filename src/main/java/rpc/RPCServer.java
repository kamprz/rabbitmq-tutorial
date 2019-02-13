package rpc;

import com.rabbitmq.client.*;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME); //czysci zawartosc

            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) ->
            {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";
                try
                {
                    String message = new String(delivery.getBody(), "UTF-8");
                    FibonacciCalculator calculator = new FibonacciCalculator();
                    System.out.println("Processing Fibonacci of " + message);
                    response += calculator.calculate(message);
                }
                catch (RuntimeException e) { System.out.println(" [.] " + e.toString()); }
                finally
                {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            System.out.println(" [x] Awaiting RPC requests");
            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            Object monitor = new Object();
            synchronized (monitor)
            {
                try { monitor.wait(); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }
}