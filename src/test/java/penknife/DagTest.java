package penknife;

public class DagTest {


    /*private static final String s = "沪指收盘小幅上扬，贵州茅台再创历史新高，创业板指三连阳。 沪指今日表现波澜不惊，全日都围绕昨收盘点位震荡整理，最终小幅收涨0.2%结束一天交易。创业板指表现稍强，小幅收涨0.7%，日K线收出三连阳。";

    private static final String s1 = "董事局主席许家印近日在公司2017年度工作会上表示，恒大人寿万能险占比过高，今后要调整产品结构，力争将原保费收入占比提至不低于50%。他称，绝不允许恒大金融成为恒大的融资平台。";

    private static final String s2 = "在钱堆里出生的飞凡，并没有真正的意识到，在它出生的时候，阿里巴巴和京东早已练就了铜墙铁壁，\n" +
            "即使它有一个首富爸爸，也很难打开拥有自己独立空间。除了管理原因，企业诞生之初的基因也决定了万达飞凡的成败。作为传统巨头的万达，\n" +
            "而在互联网和移动互联网的冲击这么多年才布局，作为一个互联网的新人，难免有些勉强仓促。在万达电商组成的时候，电商的市场已经是一片红海，\n" +
            "除了淘宝、京东这样全品类的电商，还有像聚美优品、唯品会这样的垂直电商，甚至还出现了许多单品类电商。万达在这样的时间段进入电商行业，成功的几率远比想象的要小。\n" +
            "虽然万达网络科技集团总裁曲德君表示：万达从来没说过要做电商，万达董事长王健林很早就说过，万达做的不是电商，既不是淘宝也不是京东，是一个完全创新的东西。但这完全没有说服力。\n" +
            "因为目前为止，飞凡的创新并没有为大众所发现，或者为消费者带来惊喜。如果硬要说它不算是电商，那么，飞凡看起来似乎也像低配版的大众点评、美团、百度糯米";

    private static final  String s3 = "碧桂园做事靠谱，理性，而恒大的老许像个疯子，29元还接盘万科，12元收购盛京银行，100亿香港买楼";

    private static final String s5 = "在盈透证券门外，刘士余探望了波多野结衣，后来又去了雪盈证券";

    private static final String s6 = "万科股权之争的故事发展令人应接不暇。从证监会刘主席指斥“妖精”、证监会、保监会联手祭出一堆处罚开始，又是华润将股份转让给深铁，3月16日又是中国恒大集团十家下属企业把所持的股东表决权、提案权及股东大会参会权不可撤销地委托给深铁。深铁行权份额高达29.38%，站在了要约收购的边上。此时，离万科于3月24日召开董事会会议只有一周时间。";


    @Test
    public void doTest() {
        DAG dag = new DAG(s);
        int[][] nodes = dag.nodes;
        double[][] weights = dag.weights;
        System.out.println("nodes: " + nodes);
    }

    @Test
    public void testRoute(){
        DAG dag = new DAG(s3);
        DAG.PathInfo[] route = dag.calculateRoute();
        for(int i =0; i<route.length - 1; i++){
            //System.out.println(i + ", " + route[i].idx);
            String str = dag.sentence.substring(i, route[i].idx + 1);
            System.out.print(str + "_");
        }
    }

    @Test
    public void testSegWithNoHmm(){
        DAG dag = new DAG(s6);
        List<Term> result = dag.splitByPath();
        result.forEach(w -> System.out.print(w.word + "_"));
    }*/
}
