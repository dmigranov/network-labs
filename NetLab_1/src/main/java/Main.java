import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if(args.length == 2) {
            try {
                Thread serverThread = new Thread(new CopyReceiver(args[0], args[1]));
                serverThread.start();
                new CopySender(args[0], args[1]).run();
            }
            catch(IOException e)
            {
                System.err.println("Can't start threads: " + e.getMessage());
            }
        }
        else
            System.err.println("Not enough arguments");
    }
}
