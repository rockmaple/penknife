package penknife.test.segment

import org.junit.Test
import penknife.segment.logic.HMM

class HMMTest {

    private val s3 = "碧桂园做事靠谱，理性，而恒大的老许像个疯子，29元还接盘万科，12元收购盛京银行，100亿香港买楼"

    @Test
    fun doTest() {
        val words = HMM.create().split(s3)
        words.forEach { w -> print(w.word + "_") }
    }
}