package penknife.segment.logic

import penknife.segment.dictionary.Nature
import penknife.segment.dictionary.TransliterationDict

object TransliterationRecgonizer {

    fun rebuildWordNet(splittedResult: List<Term>, wordNetOptimized: WordNet, wordNetOrigin: WordNet) {

        (0 until splittedResult.size).fold(mutableListOf<Term>()) { result, i ->

            val term = splittedResult[i]
            if (result.size > 0) {

                if (term.guessNature() == Nature.nrf || TransliterationDict.containsKey(term.word)) {
                    result.add(term)
                } else {

                    if (result.size > 1) {
                        val value = result.map { it.word }.joinToString("")
                        val startIndex = result[0].startIndex
                        val endIndex = result.last().endIndex
                        wordNetOptimized.addTermsToWordNetOptimized(listOf(Term(value, startIndex, endIndex, WordAttribute(1000, mapOf(Nature.nrf to 1000)))), wordNetOrigin)
                    }

                    result.clear()
                }

            } else {
                if (term.guessNature() == Nature.nrf || term.guessNature() == Nature.nsf) {
                    result.add(term)
                }
            }
            result
        }
    }
}