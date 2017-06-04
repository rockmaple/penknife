package penknife.segment.processor

import penknife.segment.logic.Term
import penknife.segment.logic.WordNet

abstract class AbstractProcessor {

    abstract fun process(terms: List<Term>, wordNetOptimized: WordNet, wordNetOrigin: WordNet): Unit

    infix fun andThen(nextProcessor: AbstractProcessor): AbstractProcessor {

        return object : AbstractProcessor() {
            override fun process(terms: List<Term>, wordNetOptimized: WordNet, wordNetOrigin: WordNet) {
                this@AbstractProcessor.process(terms, wordNetOptimized, wordNetOrigin)
                nextProcessor.process(terms, wordNetOptimized, wordNetOrigin)
            }
        }

    }
}