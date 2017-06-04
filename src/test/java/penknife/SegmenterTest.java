package penknife;

import org.junit.Test;
import penknife.segment.Segmenter;
import penknife.segment.logic.HMM;
import penknife.segment.logic.Term;

import java.util.List;

public class SegmenterTest {

    private HMM hmm = HMM.Companion.create();

    private static final String s = "沪指收盘小幅上扬，贵州茅台再创历史新高，创业板指三连阳。 沪指今日表现波澜不惊，全日都围绕昨收盘点位震荡整理，最终小幅收涨0.2%结束一天交易。创业板指表现稍强，小幅收涨0.7%，日K线收出三连阳。";

    private static final String s1 = "董事局主席许家印近日在公司2017年度工作会上表示，恒大人寿万能险占比过高，今后要调整产品结构，力争将原保费收入占比提至不低于50%。他称，绝不允许恒大金融成为恒大的融资平台。";

    private static final String s2 = "在钱堆里出生的飞凡，并没有真正的意识到，在它出生的时候，阿里巴巴和京东早已练就了铜墙铁壁，\n" +
            "即使它有一个首富爸爸，也很难打开拥有自己独立空间。除了管理原因，企业诞生之初的基因也决定了万达飞凡的成败。作为传统巨头的万达，\n" +
            "而在互联网和移动互联网的冲击这么多年才布局，作为一个互联网的新人，难免有些勉强仓促。在万达电商组成的时候，电商的市场已经是一片红海，\n" +
            "除了淘宝、京东这样全品类的电商，还有像聚美优品、唯品会这样的垂直电商，甚至还出现了许多单品类电商。万达在这样的时间段进入电商行业，成功的几率远比想象的要小。\n" +
            "虽然万达网络科技集团总裁曲德君表示：万达从来没说过要做电商，万达董事长王健林很早就说过，万达做的不是电商，既不是淘宝也不是京东，是一个完全创新的东西。但这完全没有说服力。\n" +
            "因为目前为止，飞凡的创新并没有为大众所发现，或者为消费者带来惊喜。如果硬要说它不算是电商，那么，飞凡看起来似乎也像低配版的大众点评、美团、百度糯米";


    private static final String s3 = "碧桂园做事靠谱，理性，而恒大的老许像个疯子，29元还接盘万科，12元收购盛京银行，100亿香港买楼";

    private static final String s5 = "万科股权之争的故事发展令人应接不暇。从证监会刘主席指斥“妖精”、证监会、保监会联手祭出一堆处罚开始，又是华润将股份转让给深铁，3月16日又是中国恒大集团十家下属企业把所持的股东表决权、提案权及股东大会参会权不可撤销地委托给深铁。深铁行权份额高达29.38%，站在了要约收购的边上。此时，离万科于3月24日召开董事会会议只有一周时间。";

    private static final String s6 = "据俄罗斯卫星网22日报道，推特(Twitter)社交网络在2016年下半年关闭了约37.7万个与宣传恐怖主义有关的用户账号。据该社交网络报告称：“2016年7月1日至12月31日期间，总共有376890个账号由于涉及宣传恐怖主义的违规行为而被中止。”同时74%的此类账号通过借助Twitter自己的反垃圾邮件工具被发现。根据报告所提供的统计数字，俄罗斯发出了522个有关删除该社交网络上不同内容的要求。因此，俄罗斯仅次于土耳其(3076个)和法国(1334个)居第三位。";

    private static final String s7 = "2016年，中国经济向“新常态”深度调整和转型，对金融业而言更是深刻变革、充满挑战的一年。我们保持一贯以来的稳健增长步伐，实现了持续领先市场的发展。2016年，平安实现净利润723.68亿元，同比增长11%，归属于母公司股东的净利润623.94亿元，同比增长15.1%；截至2016年12月31日，归属于母公司股东权益为3834.49亿元，较2015年底增长14.7%。同时，平安跻身《财富》世界500强前50名。截至2016年12月31日，平安的市值位居全球上市公司第57名、金融集团第15名、保险集团第1名";



    @Test
    public void doTest1() {
        List<Term> taggedWords = Segmenter.INSTANCE.split(s3);
        taggedWords.forEach(w -> System.out.print(w.getWord() + "| "));
    }

    @Test
    public void doTest2() {
        List<Term> taggedWords = hmm.split(s2);
        taggedWords.forEach(w -> System.out.print(w.getWord() + ", "));
    }

    @Test
    public void doTest3() {
        /*List<Term> taggedWords = new DAG(s2).splitByPath();
        taggedWords.forEach(w -> System.out.print(w.word + ", "));*/
    }

    @Test
    public void doTest4(){
        /*List<Term> t = new DAG(s3).splitByPath();
        t.forEach(w -> System.out.print(w.word + ", "));
        List<Term> taggedWords = Segmenter.dp(s3);
        taggedWords.forEach(w -> System.out.print(w.word + ", "));*/
    }

    @Test
    public void doTest5(){
        List<Term> taggedWords = Segmenter.INSTANCE.split(s2);
        taggedWords.forEach(w -> System.out.print(w.getWord() + "_"));
    }
}
