package penknife.segment.preprocessor

import penknife.segment.preprocess.T2SProcessor

object T2SProcessor : AbstractPreprocessor() {

    val t2s = T2SProcessor()

    override fun process(sentence: String): String {
        return t2s.process(sentence)
    }

}