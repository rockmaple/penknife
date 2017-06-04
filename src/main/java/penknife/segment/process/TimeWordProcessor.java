package penknife.segment.process;

import com.google.common.collect.ImmutableMap;
import penknife.segment.dictionary.Nature;
import penknife.segment.logic.Term;
import penknife.segment.logic.WordAttribute;
import penknife.segment.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeWordProcessor {

    //0123456789０１２３４５６７８９
    private static final String ARABIC_NUMBER_CODE_POINTS =
            StringUtils.toString(48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
                    65296, 65297, 65298, 65299, 65300, 65301, 65302, 65303, 65304, 65305);

    //年月日号时点分秒
    private static final String TIME_WORD_CODE_POINTS =
            StringUtils.toString(24180, 26376, 26085, 21495, 26102, 28857, 20998, 31186);

    //，。？！：；‘’“”【】、《》~·@|#￥%…&*（）—-+=,.<>?/!;:'"{}[]\|#$%^&*()_-+=◤☆★ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789
    private static final String OTHER_CODE_POINTS =
            StringUtils.toString(65292, 12290, 65311, 65281, 65306, 65307, 8216, 8217,
                    8220, 8221, 12304, 12305, 12289, 12298, 12299, 126, 183, 64, 124, 35,
                    65509, 37, 8230, 38, 42, 65288, 65289, 8212, 45, 43, 61, 44, 46, 60,
                    62, 63, 47, 33, 59, 58, 39, 34, 123, 125, 91, 93, 92, 124, 35, 36, 37,
                    94, 38, 42, 40, 41, 95, 45, 43, 61, 9700, 9734, 9733, 65, 66, 67,
                    68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
                    85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105,
                    106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
                    120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57);

    private boolean isArabicNum(String word) {
        int len = word.codePointCount(0, word.length());
        for (int i = 0; i < len; i++) {
            if (ARABIC_NUMBER_CODE_POINTS.indexOf(word.codePointAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    private boolean isTimeWord(String word) {
        return word.length() == 1 && TIME_WORD_CODE_POINTS.indexOf(word.charAt(0)) != -1;
    }

    private boolean isDoubleWord(String word, String postWord) {
        if (word.length() != 1 || postWord.length() != 1) {
            return false;
        } else {
            int wordInt = word.codePointAt(0);
            int postWordInt = postWord.codePointAt(0);
            return wordInt == postWordInt && OTHER_CODE_POINTS.indexOf(wordInt) != -1;
        }
    }

    private boolean isHttpWord(String word) {
        return word.length() >= 5 && word.startsWith("http");
    }

    public List<Term> process(List<Term> terms) {
        List<Term> taggedWords = this.processTimeWords(terms);
        taggedWords = this.processDoubleWords(taggedWords);
        //taggedWords = this.processHttpWords(taggedWords);
        //taggedWords = this.processMailAddress(taggedWords);
        return taggedWords;
    }

    private List<Term> processDoubleWords(List<Term> sentence) {
        if (sentence.size() == 0) {
            return sentence;
        }
        List<Term> result = new ArrayList<>();
        Term tagged, last = sentence.get(sentence.size() - 1);
        result.add(last);
        for (int i = sentence.size() - 2; i >= 0; i--) {
            tagged = sentence.get(i);
            last = result.get(result.size() - 1);
            if (this.isDoubleWord(tagged.getWord(), last.getWord())) {
                result.remove(result.size() - 1);
                //result.add(TaggedWord.of(tagged.getWord() + last.getWord(), tagged.getWord()));
                result.add(new Term(tagged.getWord() + last.getWord(), tagged.getStartIndex(), last.getEndIndex(), new WordAttribute(100, ImmutableMap.of(Nature.x, 100))));
            } else {
                result.add(tagged);
            }
        }
        Collections.reverse(result);
        return result;
    }

    private List<Term> processTimeWords(List<Term> sentence) {
        List<Term> result = new ArrayList<>();
        boolean hasTimeWord = false;
        for (int i = sentence.size() - 1; i >= 0; i--) {
            Term tagged = sentence.get(i);
            if (this.isTimeWord(tagged.getWord())) {
                hasTimeWord = true;
                result.add(tagged);
            } else if (hasTimeWord) {
                if (this.isArabicNum(tagged.getWord())) {
                    Term last = result.get(result.size() - 1);
                    result.remove(result.size() - 1);
                    //result.add(TaggedWord.of(tagged.getWord() + last.getWord(), "t"));
                    result.add(new Term(tagged.getWord() + last.getWord(), tagged.getStartIndex(), last.getEndIndex(), new WordAttribute(100, ImmutableMap.of(Nature.t, 100))));
                } else {
                    hasTimeWord = false;
                    result.add(tagged);
                }
            } else {
                result.add(tagged);
            }
        }
        Collections.reverse(result);
        return result;
    }

    private List<Term> processHttpWords(List<Term> sentence) {
        List<Term> result = new ArrayList<>();
        for (Term tagged : sentence) {
            if (this.isHttpWord(tagged.getWord())) {
                //result.add(TaggedWord.of(tagged.getWord(), "x"));
                result.add(new Term(tagged.getWord(), tagged.getStartIndex(), tagged.getEndIndex(), new WordAttribute(100, ImmutableMap.of(Nature.x, 100))));
            } else {
                result.add(tagged);
            }
        }
        return result;
    }

    private List<Term> processMailAddress(List<Term> sentence) {
        if (sentence.isEmpty()) {
            return sentence;
        }
        List<Term> result = new ArrayList<>();
        Term last = sentence.get(0), tagged;
        result.add(last);
        for (int i = 1, size = sentence.size(); i < size; i++) {
            tagged = sentence.get(i);
            if ("@".equals(last.getWord()) && !"@".equals(tagged.getWord())) {
                //result.add(TaggedWord.of(tagged.getWord(), "np"));
                result.add(new Term(tagged.getWord(), tagged.getStartIndex(), tagged.getEndIndex(), new WordAttribute(100, ImmutableMap.of(Nature.n, 100))));
            } else {
                result.add(tagged);
            }
            last = tagged;
        }
        return result;
    }

    public static void main(String[] args) {
        List<Term> taggedWords = new ArrayList<>();
        String s = "Twitter";
        for (int i = 0; i < s.length(); i++) {
            //taggedWords.add(TaggedWord.of(String.valueOf(s.charAt(i)), "u"));
            taggedWords.add(new Term(String.valueOf(s.charAt(i)), -1, -1, null));
        }
        List<Term> taggedWords1 = new TimeWordProcessor().process(taggedWords);

        taggedWords1.forEach(tw -> System.out.print(tw + "__"));
    }
}
