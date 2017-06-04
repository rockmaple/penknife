package penknife.data.test;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class DatTest {

    private static final Logger logger = Logger.getLogger(DatTest.class.getName());

    //@Test
    public void doTest0() throws Exception{
        InputStream in = Resources.getResource("dictionary/CoreNatureDictionary.txt").openStream();
        Dat dat = DatMaker.readFromInputStream(in);
        int length = dat.dat.length;
        int i = dat.match("BBCCDD");
        logger.info("i: " + i);
        logger.info("length: " + length);

        in = Resources.getResource("dictionary/CoreNatureDictionary.txt").openStream();
        List<String> lines = CharStreams.readLines(new InputStreamReader(in, Charsets.UTF_8));

        System.out.println("dat size: " + dat.datSize);
        int maxIndex = 0;
        for (String line : lines) {
            final int match = dat.match(line);
            //logger.info("line: " + line + " index: " + match);
            if(match >maxIndex){
                maxIndex = match;
            }
        }
        System.out.println("max Index: " + maxIndex);
    }

    //@Test
    public void doTest1() throws Exception {
        logger.info("loading default dictionary...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            try (InputStream is = Resources.getResource("dictionary/CoreNatureDictionary.txt").openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8))
            ) {
                List<String> words = CharStreams.readLines(reader, new LineProcessor<List<String>>() {

                    private List<String> dictItems = new ArrayList<>();
                    private int count = 0;

                    @Override
                    public boolean processLine(String line) throws IOException {
                        String[] splitted = line.split("\\s");
                        if (splitted.length > 2) {
                            final String word = splitted[0];
                            dictItems.add(word);
                            count++;
                        }
                        return true;
                    }

                    @Override
                    public List<String> getResult() {
                        logger.info("dict item count: " + count);
                        return dictItems;
                    }
                });
                logger.info("load file: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
                logger.info("size: " + words.size());
                final DatMaker dat = new DatMaker();
                dat.buildDat(words);
                stopwatch.stop();
                logger.info("default dictionary load finished. time cost: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

                /*try (
                        OutputStream file = new FileOutputStream("dat.ser");
                        OutputStream buffer = new BufferedOutputStream(file);
                        ObjectOutput output = new ObjectOutputStream(buffer)
                ) {
                    output.writeObject(dat);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
            }
        }catch (IOException e) {
            logger.log(Level.SEVERE, "failed to load default dict", e);
        }
    }

    //@Test
    public void doTest2(){
        try (
                InputStream buffer = new BufferedInputStream(Resources.getResource("dat.ser").openStream());
                ObjectInput input = new ObjectInputStream(buffer)
        ) {
            Stopwatch stopwatch = Stopwatch.createStarted();

            //deserialize the dat
            penknife.data.test.DatMaker dat = (penknife.data.test.DatMaker) input.readObject();

            stopwatch.stop();

            System.out.println("time: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
