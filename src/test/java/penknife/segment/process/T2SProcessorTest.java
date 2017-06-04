package penknife.segment.process;

import org.junit.Assert;
import org.junit.Test;
import penknife.segment.preprocess.T2SProcessor;

public class T2SProcessorTest {

    private final String s = "參選國民黨主席？ 胡志強首度鬆口稱“會考慮";

    @Test
    public void doTest1() {
        T2SProcessor t2SProcessor = new T2SProcessor();
        String s1 = t2SProcessor.process(s);
        Assert.assertEquals("参选国民党主席？ 胡志强首度松口称“会考虑", s1);
    }

}
