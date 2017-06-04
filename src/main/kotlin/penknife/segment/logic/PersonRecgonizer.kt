package penknife.segment.logic

import penknife.segment.dictionary.*

object PersonRecgonizer {

    fun rebuildWordNet(splittedResult: List<Term>, wordNetOptimized: WordNet, wordNetOrigin: WordNet) {
        val persons = getTermsByRecgonize(splittedResult)
        wordNetOptimized.addTermsToWordNetOptimized(persons, wordNetOrigin)
    }

    //根据当前
    fun getTermsByRecgonize(splittedResult: List<Term>): List<Term> {

        val nrList = splittedResult.fold(mutableListOf<EnumItem<NR>>()) { result, term ->

            var nrEnumItem = NRDict.get(term.word)

            if (nrEnumItem == null) {
                when (term.guessNature()) {
                    Nature.nr -> {
                        if (term.attribute.frequency <= 1000 && term.word.length == 2) {
                            nrEnumItem = EnumItem(NR.X, NR.G)
                        } else {
                            nrEnumItem = EnumItem<NR>(NR.A, NRTransformMatrixDict.data.getTotalFrequency(NR.A))
                        }
                    }
                    Nature.nnt -> {
                        nrEnumItem = EnumItem(NR.G, NR.K)
                    }
                    else -> {
                        nrEnumItem = EnumItem<NR>(NR.A, NRTransformMatrixDict.data.getTotalFrequency(NR.A))
                    }
                }
            }

            result.add(nrEnumItem)

            result
        }.fold(mutableListOf<NR>()) { result, item ->

            var prev = NR.A

            if (result.isNotEmpty()) {
                prev = result.last()
            }

            val max = item.labelMap.keys.minBy {
                NRTransformMatrixDict.data.transitProbability!![prev.ordinal][it.ordinal] - Math.log((item.getFrequency(it) + (1e-8)) / NRTransformMatrixDict.data.getTotalFrequency(it))
            }

            result.add(max!!)

            result
        }.toList()

        return PersonDict.parsePattern(splittedResult, nrList)
    }
}

fun main(args: Array<String>) {
    val s = "龚学平等领导, 优酷总裁魏明介绍了优酷2015年的内容战略"
    val wn = WordNet(s)
    val terms = wn.calculateRoute()
    println("terms: " + terms.map { it.word + "/" + it.attribute.natures.entries + "_" })
    val r = PersonRecgonizer.getTermsByRecgonize(terms)
    println(r)
}