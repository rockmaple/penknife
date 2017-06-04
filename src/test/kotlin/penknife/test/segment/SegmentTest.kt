package penknife.test.segment

import org.junit.Assert
import org.junit.Test
import penknife.segment.Segmenter

class SegmentTest {

    val s1 = "据乐施会12日公布的最新报告，包括苹果和微软在内的美国50家最大的跨国企业，在全球各地避税天堂藏了1.6万亿美元，其主要目的是为了减少在美国的纳税额。"
    val s2 = "碧桂园做事靠谱，理性，而恒大的老许像个疯子，29元还接盘万科，12元收购盛京银行，100亿香港买楼"

    @Test
    fun doTest1() {
        val result = Segmenter.split(s1)
        result.forEach { w ->
            val word = w.word
            //val t = w.tag
            print(word + "_")
        }
    }

    @Test
    fun doTest2(){
        val result = Segmenter.split(s2)
        result.forEach { w ->
            val word = w.word
            //val t = w.tag
            print(word + "_")
        }
        Assert.assertEquals(result.isNotEmpty(), true)
    }

}
