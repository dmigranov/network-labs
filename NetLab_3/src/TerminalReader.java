import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class TerminalReader implements Runnable {

    private final TreeNode node;

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
                data = new byte[strBytes.length + 9];
                byte[] millisBytes = new byte[8];
                ByteBuffer bb = ByteBuffer.wrap(millisBytes);
                bb.putLong(System.currentTimeMillis());
                data[0] = TreeNode.msgByte;
                System.arraycopy(millisBytes, 0, data, 1, 8);
                System.arraycopy(strBytes, 0, data, 9, strBytes.length);
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
