package penknife.segment.preprocess;

import penknife.segment.util.CharUtils;
import penknife.segment.util.StringUtils;

public class CleanProcessor {

    private static final String WHITESPACE_CODE_POINTS = StringUtils.toString(32, 12288);

    private String cleanup(String sentence) {
        StringBuilder cleaned = new StringBuilder();

        int[] codePoints = StringUtils.toCodePoints(sentence);
        boolean spaceFlag = false;
        for (int c : codePoints) {
            //空白字符
            if (WHITESPACE_CODE_POINTS.indexOf(c) != -1) {
                if (spaceFlag) {
                    continue;     //上一个是space, 忽略当前
                }
                spaceFlag = true;
            } else {
                spaceFlag = false;
            }

            cleaned.appendCodePoint(CharUtils.regularize(c));
        }
        return cleaned.toString();
    }

    public String process(String raw) {
        return this.cleanup(raw);
    }
}
