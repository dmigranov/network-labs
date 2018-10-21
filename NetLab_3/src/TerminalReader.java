import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class TerminalReader implements Runnable {


    private TreeNode node = null;

    public TerminalReader(TreeNode node)
    {
        this.node = node;
    }


    @Override
    public void run() {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            String str;
            while((str = br.readLine()) != null) {
                byte [] data;
                String resStr = node.getNodeName() + ": " + str;
                byte[] strBytes = resStr.getBytes("UTF-8");
                //UUID uuid = UUID.nameUUIDFromBytes(strBytes); //16 bytes. I guess there is no need to send it - it can easily be recalculated!

                data = new byte[strBytes.length + 1];
                data[0] = TreeNode.msgByte;
                System.arraycopy(strBytes, 0, data, 1, strBytes.length);

                //node.getMessageQueue().add(new Message(data));
                node.addMessagesToAll(data);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(5);
        }
    }
}
