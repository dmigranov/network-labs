public class TreeNode
{
    public static void main(String[] args)
    {
        String nodeName;
        double lossQuota;
        if(args.length < 3)
        {
            System.err.println("Not enough arguments");
            System.exit(1);
        }
        nodeName = args[0];
        lossQuota = Double.parseDouble(args[1]); //0 <= lQ <= 1 ?
        int port = Integer.parseInt(args[2]);
    }
}
