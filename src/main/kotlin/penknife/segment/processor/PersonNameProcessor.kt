package penknife.segment.processor

import penknife.segment.logic.PersonRecgonizer
import penknife.segment.logic.Term
import penknife.segment.logic.WordNet

object PersonNameProcessor : AbstractProcessor() {

    override fun process(terms: List<Term>, wordNetOptimized: WordNet, wordNetOrigin: WordNet) {
        PersonRecgonizer.rebuildWordNet(terms, wordNetOptimized, wordNetOrigin)
    }

}