package penknife.segment.processor

import penknife.segment.logic.Term
import penknife.segment.logic.TransliterationRecgonizer
import penknife.segment.logic.WordNet

object TransliterationProcessor : AbstractProcessor() {

    override fun process(terms: List<Term>, wordNetOptimized: WordNet, wordNetOrigin: WordNet) {
        TransliterationRecgonizer.rebuildWordNet(terms, wordNetOptimized, wordNetOrigin)
    }
}