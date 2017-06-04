package penknife.segment.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Pattern;

public class CharUtils {

    public static final Pattern RE_SKIP = Pattern.compile("(\\d+\\.\\d+|[a-zA-Z0-9]+)");

    private static final List<Character> connectors = Lists.newArrayList('+', '#', '&', '.', '_', '-');

    public static boolean isCnLetter(char ch) {
        return ch >= 0x4E00 && ch <= 0x9FA5;
    }

    public static boolean isEnLetter(char ch) {
        return (ch >= 0x0041 && ch <= 0x005A) || (ch >= 0x0061 && ch <= 0x007A);
    }

    public static boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    public static boolean isConnector(char ch) {
        return connectors.stream().anyMatch(c -> c == ch);
    }

    /**
     * 全角 to 半角
     *
     * @param codePoint 输入字符
     * @return 转换后的字符
     */
    public static int regularize(int codePoint) {
        if (codePoint == 12288) {
            return 32;
        } else if (codePoint > 65280 && codePoint < 65375) {
            return codePoint - 65248;
        }
        /*else if (codePoint >= 'A' && codePoint <= 'Z') {
            return codePoint + 32;
        }*/
        return codePoint;
    }
}
