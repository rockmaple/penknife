package penknife.segment.process;

import penknife.segment.logic.HMM;
import penknife.segment.logic.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * hmm
 */
public class HmmProcessor {

    private static final Logger logger = Logger.getLogger(HmmProcessor.class.getName());
    private HMM hmm = HMM.Companion.create();

    public List<Term> process(List<Term> wordList) {

        if (wordList.isEmpty()) return wordList;

        List<Term> result = new ArrayList<>();

        StringBuilder tmp = new StringBuilder();

        for (Term taggedWord : wordList) {
            if (taggedWord.getWord().length() == 1) {
                tmp.append(taggedWord.getWord());
            } else if (taggedWord.getWord().length() > 1) {
                if (tmp.length() > 0) {
                    processTmp(result, tmp);
                    tmp = new StringBuilder();
                }
                result.add(taggedWord);
            }
        }

        if (tmp.length() > 0) {
            processTmp(result, tmp);
        }

        return result;
    }

    private void processTmp(List<Term> result, StringBuilder tmp) {
        if (tmp.length() == 1) {
            result.add(new Term(tmp.toString(), -1, -1, null));
        } else {
            //使用hmm分词
            result.addAll(hmm.split(tmp.toString()));
        }
    }
}
