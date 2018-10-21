public class ChatResender implements Runnable {

    private TreeNode node;
    public ChatResender(TreeNode node)
    {
        this.node = node;
    }


    @Override
    public void run() {
        while(true)
        {
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e)
            {}

            node.getMessageQueue().addAll(node.getSentMessages());
            node.getSentMessages().clear();
        }

    }
}
