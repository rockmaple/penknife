package penknife.segment.dictionary

import penknife.segment.logic.Term
import penknife.segment.logic.WordAttribute
import penknife.segment.util.dat.AhoCorasickDoubleArrayTrie
import java.util.*

object PersonDict {

    val trie = NRAhoCorasickDat()

    init {
        trie.load()
    }

    fun parsePattern(terms: List<Term>, nrList: List<NR>): List<Term> {

        val patternBuffer = StringBuilder()
        val result = (0 until terms.size).fold(mutableListOf<Pair<Term, NR>>()) { result, i ->
            val currentTerm = terms[i]
            val currentNr = nrList[i]
            when (currentNr) {
                NR.U -> {
                    val nowK = currentTerm.word.substring(0, currentTerm.word.length - 1)
                    val nowB = currentTerm.word.substring(currentTerm.word.length - 1)
                    result.add(Pair(Term(nowK, currentTerm.startIndex, currentTerm.endIndex - 1, CoreDict.getAttribute(nowK)
                            ?: WordAttribute(1, mapOf(Nature.n to 1))), NR.K))
                    result.add(Pair(Term(nowB, currentTerm.endIndex - 1, currentTerm.endIndex, CoreDict.getAttribute(nowK)
                            ?: WordAttribute(1, mapOf(Nature.n to 1))), NR.B))
                    patternBuffer.append(NR.K).append(NR.B)
                }
                NR.V -> {
                    val prevNr = if (result.isNotEmpty()) result.last().second else NR.A
                    val nrFirst = if (prevNr == NR.B) NR.E else NR.D
                    val nrSecond = NR.L
                    val nowED = currentTerm.word.substring(currentTerm.word.length - 1)
                    val nowL = currentTerm.word.substring(0, currentTerm.word.length - 1)
                    result.add(Pair(Term(nowL, currentTerm.startIndex, currentTerm.endIndex - 1, CoreDict.getAttribute(nowL)
                            ?: WordAttribute(1, mapOf(Nature.n to 1))), nrSecond))
                    result.add(Pair(Term(nowED, currentTerm.endIndex - 1, currentTerm.endIndex, CoreDict.getAttribute(nowED)
                            ?: WordAttribute(1, mapOf(Nature.n to 1))), nrFirst))
                    patternBuffer.append(nrFirst).append(nrSecond)
                }
                else -> {
                    result.add(Pair(currentTerm, currentNr))
                    patternBuffer.append(currentNr)
                }
            }
            result
        }

        val pattern = patternBuffer.toString()
        val resultTerms = result.map { it.first }
        val hits = trie.parseText(pattern)

        return hits.fold(mutableListOf()) { result, hit ->

            val parsedName = resultTerms.subList(hit.begin, hit.end).joinToString("") { it.word }

            if (!isBadCase(parsedName) && !(hit.value == NRPattern.BCD && parsedName[0] == parsedName[2])) {
                val beginTerm = resultTerms[hit.begin]
                val endTerm = resultTerms[hit.end - 1]
                //识别出人名
                result.add(Term(parsedName, beginTerm.startIndex, endTerm.endIndex, WordAttribute(100, mapOf(Nature.nr to 100))))
            }

            result
        }

    }

    /**
     * 因为任何算法都无法解决100%的问题，总是有一些bad case，这些bad case会以“盖公章 A 1”的形式加入词典中<BR>
     * 这个方法返回人名是否是bad case
     */
    private fun isBadCase(name: String): Boolean {
        val nrEnumItem = NRDict.get(name) ?: return false
        return nrEnumItem.containsLabel(NR.A)
    }

}

class NRAhoCorasickDat : AhoCorasickDoubleArrayTrie<NRPattern>() {

    fun load() {
        val map = TreeMap<String, NRPattern>()
        for (pattern in NRPattern.values()) {
            map[pattern.toString()] = pattern
        }
        build(map)
    }

}