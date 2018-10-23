

public class ChatResender implements Runnable {

    private final TreeNode node;
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

            synchronized(node) {
                node.getMessageQueue().addAll(node.getSentMessages());
                node.getSentMessages().clear();
            }
            
            //каждые 500 миллисекундд (или больше) обновлять списки (тот, что сверху, плюс список прибывших сообщений). На это время заблокировать получение сообщений и добавление в список
        }

    }
}
