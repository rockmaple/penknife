package penknife.segment.preprocessor

abstract class AbstractPreprocessor {

    abstract fun process(sentence: String): String

    infix fun andThen(nextPreprocessor: AbstractPreprocessor): AbstractPreprocessor {

        return object : AbstractPreprocessor() {
            override fun process(sentence: String): String {
                val processed = this@AbstractPreprocessor.process(sentence)
                return nextPreprocessor.process(processed)
            }
        }

    }

}