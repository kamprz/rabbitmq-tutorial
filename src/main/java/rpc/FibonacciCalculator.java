package rpc;

public class FibonacciCalculator {
    public String calculate(String stringNumber)
    {
        try{
            Integer number = Integer.parseInt(stringNumber);
            if(number<1) throw new Exception();
            Integer fibo1 = 1;
            Integer fibo2 = 1;
            Integer result = 1;
            if(number<=2) return result.toString();
            for(int i = 2; i < number ; i++)
            {
                result = fibo1 + fibo2;
                fibo2 = fibo1;
                fibo1 = result;
            }
            return result.toString();
        }
        catch(Exception e)
        {
            return "Can't process " + stringNumber+ ". Pass positive integer value.";
        }
    }
}
