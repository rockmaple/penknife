package penknife.segment.process;

import org.junit.Test;
import penknife.segment.preprocess.CleanProcessor;

import java.util.logging.Logger;

public class CleanProcessorTest {

    private static final Logger logger = Logger.getLogger(CleanProcessorTest.class.getName());

    private String s = "《一本书》讲的什么，碧桂园做事靠谱，理性，而恒大的老许像个疯子，29元还接盘万科，12元收购盛京银行，100亿香港买楼";

    private static final String s2 = "在钱堆里出生的飞凡，并没有真正的意识到，在它出生的时候，阿里巴巴和京东早已练就了铜墙铁壁，\n" +
            "即使它有一个首富爸爸，也很难打开拥有自己独立空间。除了管理原因，企业诞生之初的基因也决定了万达飞凡的成败。作为传统巨头的万达，\n" +
            "而在互联网和移动互联网的冲击这么多年才布局，作为一个互联网的新人，难免有些勉强仓促。在万达电商组成的时候，电商的市场已经是一片红海，\n" +
            "除了淘宝、京东这样全品类的电商，还有像聚美优品、唯品会这样的垂直电商，甚至还出现了许多单品类电商。万达在这样的时间段进入电商行业，成功的几率远比想象的要小。\n" +
            "虽然万达网络科技集团总裁曲德君表示：万达从来没说过要做电商，万达董事长王健林很早就说过，万达做的不是电商，既不是淘宝也不是京东，是一个完全创新的东西。但这完全没有说服力。\n" +
            "因为目前为止，飞凡的创新并没有为大众所发现，或者为消费者带来惊喜。如果硬要说它不算是电商，那么，飞凡看起来似乎也像低配版的大众点评、美团、百度糯米";


    @Test
    public void doTest1(){
        CleanProcessor cleanProcessor = new CleanProcessor();
        String s1 = cleanProcessor.process(s2);
        /*Segmenter.dp(s1);*/
        System.out.println("finished.");
    }
}
