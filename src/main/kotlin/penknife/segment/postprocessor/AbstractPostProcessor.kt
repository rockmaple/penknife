package penknife.segment.postprocessor

import penknife.segment.logic.Term

abstract class AbstractPostProcessor {

    abstract fun process(terms: List<Term>): List<Term>

    infix fun andThen(afterProcessor: AbstractPostProcessor): AbstractPostProcessor {
        return object : AbstractPostProcessor() {
            override fun process(terms: List<Term>): List<Term> {
                val processed = this@AbstractPostProcessor.process(terms)
                return afterProcessor.process(processed)
            }

        }
    }

}