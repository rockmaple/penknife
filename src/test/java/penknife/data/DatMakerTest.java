package penknife.data;

import org.junit.Test;

import java.io.IOException;

//import penknife.segment.util.dat.Dat;
//import penknife.segment.util.dat.DatMaker;

public class DatMakerTest {
    @Test
    public void test() throws IOException {
       /* InputStream in = Resources.getResource("dat_maker_test_1.txt").openStream();
        Dat dat = DatMaker.readFromInputStream(in);

        in = Resources.getResource("dat_maker_test_1.txt").openStream();
        List<String> lines = CharStreams.readLines(new InputStreamReader(in, Charsets.UTF_8));

        for (String line : lines) assertTrue(line, dat.contains(line));

        int index = dat.match("AB");
        System.out.println("index: " + index);*/
    }

    //@Test
    public void test1() throws Exception {
        /*DatMaker datMaker = new DatMaker();
        DictItem item1 = new DictItem("天空", 2, "n");
        DictItem item2 = new DictItem("海洋", 2, "n");

        datMaker.buildDatFromDictItems(Lists.newArrayList(item1, item2));

        int i = datMaker.match("天空");

        System.out.println("i: " + i);

        Element ele = datMaker.dat[i];

        System.out.println("ele: " + ele);

        Optional<Element> eleOpt = datMaker.getElement("天空");

        eleOpt.ifPresent(element -> System.out.println("ele: " + element));*/
    }


}
