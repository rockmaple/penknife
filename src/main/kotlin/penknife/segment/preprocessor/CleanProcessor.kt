package penknife.segment.preprocessor

import penknife.segment.preprocess.CleanProcessor

object CleanProcessor : AbstractPreprocessor() {

    private val delegate = CleanProcessor()

    override fun process(sentence: String): String {
        return delegate.process(sentence)
    }
}