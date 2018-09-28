import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws IOException {
        Long terabyte = 1024L*1024*1024*1024;
        String s = terabyte.toString();

        //System.out.println(Arrays.toString(s.getBytes("UTF-8")));

        BigInteger bt = new BigInteger(s);
        byte[] tb = bt.toByteArray();
        System.out.println(Arrays.toString(tb));
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        bb.putLong(0, terabyte);
        System.out.println(Arrays.toString(bb.array()));

    }

}
//1024^4 = 2^40
//6 bytes