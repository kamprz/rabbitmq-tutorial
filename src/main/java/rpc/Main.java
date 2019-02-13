package rpc;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] argv)
    {
        try (RPCClient rpcClient = new RPCClient())
        {
            for (int i = 30; i < 32; i++)
            {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
                String response = rpcClient.call(i_str);
                System.out.println(" [.] Got '" + response + "'");
            }
        }
        catch (IOException | TimeoutException | InterruptedException e) { e.printStackTrace(); }
    }
}
