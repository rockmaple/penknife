package penknife.segment.process;

import penknife.segment.dictionary.UserDict;
import penknife.segment.logic.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * 对已经切分好的词使用词典进行匹配
 */
public class DictProcessor {

    public List<Term> process(List<Term> terms) {

        if (terms.isEmpty()) return terms;

        List<Term> result = new ArrayList<>();

        List<String> tmp = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            Term tagged = terms.get(i);
            StringBuilder cur = new StringBuilder(tagged.getWord());

            //词库中没有cur做为前缀的词
            if (!UserDict.INSTANCE.containPrefix(cur.toString())) {
                result.add(tagged);
                continue;
            }

            tmp.clear();
            for (int j = i + 1; j < terms.size(); j++) {
                cur.append(terms.get(j).getWord());
                if (!UserDict.INSTANCE.containPrefix(cur.toString())) {
                    break;
                }
                //每加一个word,向tmp里加一下
                tmp.add(cur.toString());
            }

            int k = tmp.size() - 1;
            while (k >= 0 && !UserDict.INSTANCE.contains(tmp.get(k))) {
                k--;
            }

            //找到需要合并的位置
            if (k >= 0) {
                cur.setLength(0);
                for (int j = i; j < i + k + 2; j++) cur.append(terms.get(j).getWord());
                result.add(new Term(cur.toString(), terms.get(i).getStartIndex(), terms.get(i + k + 1).getEndIndex(), UserDict.INSTANCE.getAttribute(cur.toString())));
                i = i + k + 1;
            } else { //没找到
                result.add(tagged);
            }
        }

        return result;
    }

}
