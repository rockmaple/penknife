package penknife;

import com.google.common.base.CharMatcher;
import org.junit.Assert;
import org.junit.Test;

public class MiscTest {

    @Test
    public void doTest(){
        Character c1 = new Character('a');
        Assert.assertTrue('a' == c1);
    }

    @Test
    public void doTest1(){
        System.out.println(0x09&0x01);
    }

    @Test
    public void doTest2(){
        String s = CharMatcher.digit().retainFrom("abcdef37.5abcd");
        System.out.println("s: " + s);
    }

}
