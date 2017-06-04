package penknife.segment.process;

import com.google.common.collect.ImmutableMap;
import penknife.segment.dictionary.Nature;
import penknife.segment.logic.Term;
import penknife.segment.logic.WordAttribute;
import penknife.segment.util.CharUtils;

import java.util.ArrayList;
import java.util.List;

public class NumberProcessor {

    public List<Term> process(List<Term> sentence) {
        List<Term> result = new ArrayList<>();
        for (int i = 0; i < sentence.size(); i++) {
            Term tagged = sentence.get(i);
            if (isNumber(tagged.getWord())) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(tagged.getWord());
                int k = i + 1;
                for (; k < sentence.size(); k++) {
                    Term tagged1 = sentence.get(k);
                    //数字或者小数点
                    if (isNumberOrDot(tagged1.getWord())) {
                        tmp.append(tagged1.getWord());
                    } else {
                        break;
                    }
                }
                //数字后面是百分号
                if (k < sentence.size() && ("%".equals(sentence.get(k).getWord()) || "%".equals(sentence.get(k).getWord()))) {
                    tmp.append(sentence.get(k).getWord());
                    k++;
                }
                result.add(new Term(tmp.toString(), -1, -1, new WordAttribute(100, ImmutableMap.of(Nature.m, 100))));
                i = k - 1;
            } else {
                result.add(tagged);
            }
        }
        return result;
    }

    private boolean isNumber(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!CharUtils.isDigit(c)) {
                return false;
            }
        }
        return true;
    }


    private boolean isNumberOrDot(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!(CharUtils.isDigit(c) || isDot(c))) {
                return false;
            }
        }
        return true;
    }

    public boolean isDot(char ch) {
        return ch == '.' || ch == '．';
    }

    public static void main(String[] args) {
        List<Term> taggedWords = new ArrayList<>();
        String s = "嘿嘿12345哈哈哈";
        for (int i = 0; i < s.length(); i++) {
            taggedWords.add(new Term(String.valueOf(s.charAt(i)), -1, -1, null));
        }
        List<Term> taggedWords1 = new NumberProcessor().process(taggedWords);

        taggedWords1.forEach(tw -> System.out.print(tw.getWord() + "__"));
    }
}
