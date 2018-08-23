package penknife.segment.logic.hmm;

import org.junit.Test;
import penknife.segment.logic.HMM;
import penknife.segment.logic.Term;

import java.util.List;

public class HmmTest {

    private static final String s3 = "碧桂园做事靠谱，理性，而恒大的老许像个疯子，29元还接盘万科，12元收购盛京银行，100亿香港买楼";
    private static final String s4 = "飞凡的创新并没有为大众所发现";

    @Test
    public void doTest() {
        List<Term> words = HMM.Companion.create().split(s3);
        words.forEach(w -> System.out.print(w.getWord() + "_"));
    }

}
