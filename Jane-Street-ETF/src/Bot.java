/* HOW TO RUN
   1) Configure things in the Configuration class
   2) Compile: javac Bot.java
   3) Run in loop: while true; do java Bot; sleep 1; done
*/
import java.lang.*;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;

class Configuration {
    String exchange_name;
    int    exchange_port;
    /* 0 = prod-like
       1 = slow
       2 = empty
    */
    final Integer test_exchange_kind = 0;
    /* replace REPLACEME with your team name! */
    final String  team_name          = "thebigdipper";

    Configuration(Boolean test_mode) {
        if(!test_mode) {
            exchange_port = 20000;
            exchange_name = "production";
        } else {
            exchange_port = 20000 + test_exchange_kind;
            exchange_name = "test-exch-" + this.team_name;
        }
    }

    String  exchange_name() { return exchange_name; }
    Integer port()          { return exchange_port; }
}

public class Bot
{
    public static void main(String[] args)
    {
    	//ADD "prod" AS AN ARG TO RUN IN TEST MODE
    	boolean testing = true;
    	if (args[0].contentEquals("prod")){
    		testing = false;
    	}
    	else if (args[0].contentEquals("test")){
    		testing = true;
    	}
    	
        /* The boolean passed to the Configuration constructor dictates whether or not the
           bot is connecting to the prod or test exchange. Be careful with this switch! */
        Configuration config = new Configuration(testing);
        try
        {
            Socket skt = new Socket(config.exchange_name(), config.port());
            BufferedReader from_exchange = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            PrintWriter to_exchange = new PrintWriter(skt.getOutputStream(), true);

            /*
              A common mistake people make is to to_exchange.println() > 1
              time for every from_exchange.readLine() response.
              Since many write messages generate marketdata, this will cause an
              exponential explosion in pending messages. Please, don't do that!
            */
            to_exchange.println(("HELLO " + config.team_name).toUpperCase());
            String reply = from_exchange.readLine().trim();
            System.err.printf("The exchange replied: %s\n", reply);
            int order_id = 0;
            while (true) {
                String message[] = from_exchange.readLine().trim().split(" ");
                if (message[0].equals("CLOSE")) {
                    System.out.println("The round has ended");
                    break;
                }

                to_exchange.println("ADD " + order_id + " BOND BUY 999 1");
                System.out.println("order placed");
                order_id++;
                to_exchange.println("ADD " + order_id + " BOND SELL 1001 1");
                System.out.println("sell order placed");
                order_id++;
                
                
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
    }
}
