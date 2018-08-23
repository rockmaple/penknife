package penknife.segment.preprocess;

import com.google.common.io.Resources;
import penknife.segment.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class T2SProcessor {

    private static final Logger logger = Logger.getLogger(T2SProcessor.class.getName());

    private static final String t2sFile = "dictionary/t2s.txt";
    private Map<Integer, Integer> t2sMap = new HashMap<>();

    public T2SProcessor() {
        loadT2SMap();
    }

    public String process(String raw) {
        return convertT2S(raw);
    }

    private int getSimplifiedCodePoint(int c) {
        if (this.t2sMap.containsKey(c)) {
            return this.t2sMap.get(c);
        }
        return c;
    }

    private String convertT2S(String sentence) {
        int[] codePoints = StringUtils.toCodePoints(sentence);
        StringBuilder sb = new StringBuilder();
        for (int codePoint : codePoints) {
            sb.appendCodePoint(this.getSimplifiedCodePoint(codePoint));
        }
        return sb.toString();
    }


    private void loadT2SMap() {

        try {
            Files.lines(Paths.get(Resources.getResource(t2sFile).getFile()), StandardCharsets.UTF_8).forEach(line -> {
                String[] splitted = line.split(" ");
                Integer t = Integer.parseInt(splitted[0].substring(2), 16);
                Integer s = Integer.parseInt(splitted[1].substring(2), 16);
                t2sMap.put(t, s);
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to load t2s file", e);
        }

    }
}
