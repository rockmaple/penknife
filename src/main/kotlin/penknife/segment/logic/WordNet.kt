package penknife.segment.logic

import penknife.segment.dictionary.BigramDict
import penknife.segment.dictionary.CharTypeDict
import penknife.segment.dictionary.CoreDict
import penknife.segment.dictionary.Nature
import java.util.*

class WordNet(val sentence: String, fromSentence: Boolean = true) {

    val nodes = Array<MutableList<Term>>(sentence.length) { mutableListOf() }

    val dSmoothingPara = 0.1

    //构建完整词网
    init {
        if (fromSentence) {
            initNodes()
        }
    }

    //根据结果构建最优化词网
    constructor(sentence: String, terms: List<Term>) : this(sentence, fromSentence = false) {
        terms.forEach { term ->
            nodes[term.startIndex].add(term)
        }
    }

    fun calculateRoute(): List<Term> {

        //val logTotalFrequency = Math.log(CoreDict.totalFrequency.toDouble())
        val route = arrayOfNulls<PathInfo>(sentence.length)
        for (i in sentence.length - 1 downTo 0) {
            route[i] = nodes[i].map { term ->
                val w = calculateScore(term, route)
                //val w = Math.log(term.attribute.frequency.toDouble()) - logTotalFrequency + if (term.endIndex < route.size - 1) (route[term.endIndex + 1]?.maxWeight ?: 0.0) else 0.0
                PathInfo(term, w)
            }.minBy { it.maxWeight }
        }

        return route.fold(mutableListOf<Term>()) { result, pathInfo ->
            when {
                result.isNotEmpty() -> {
                    val last = result.last()
                    if (pathInfo != null && last.endIndex < pathInfo.term.startIndex) {
                        result.add(pathInfo.term)
                    }
                }
                else -> result.add(pathInfo!!.term)
            }
            result
        }.toList()
    }

    private fun calculateScore(term: Term, route: Array<PathInfo?>): Double {
        val dTemp = 1.toDouble() / CoreDict.dat.totalFrequency.toDouble() + 0.00001
        val nTwoWordsFreq = if (term.endIndex >= sentence.length - 1) 0 else BigramDict.getBiFrequency(term.word, route[term.endIndex + 1]!!.term.word)
        val freq = if (term.attribute.frequency == 0) 1 else term.attribute.frequency
        val maxWeight = if (term.endIndex >= sentence.length - 1) 0.0 else route[term.endIndex + 1]!!.maxWeight
        val freqFactor = dSmoothingPara * freq / (CoreDict.dat.totalFrequency)
        val ngramFactor = (1 - dSmoothingPara) * ((1 - dTemp) * nTwoWordsFreq / freq + dTemp)
        val w = maxWeight + Math.abs(-Math.log(freqFactor + ngramFactor))
        println("tem: $term w: $w")
        return w
    }


    /**
     * 将terms插入wordNetOptimized，并利用wordNetOrigin补全联通性
     */
    fun addTermsToWordNetOptimized(terms: List<Term>, wordNetOrigin: WordNet) {

        val nodes = this.nodes

        terms.forEach termLoop@ { term ->
            val index = term.startIndex
            val exists = nodes[index].find { it.word.length == term.word.length }
            if (exists != null) return@termLoop   //已经存在了
            nodes[index].add(term)
            //向前找
            (index - 1 downTo 1).forEach prevTermLoop@ { i ->
                val t = nodes[i].find { it.word.length == 1 }
                if (t == null) {
                    val first = (if (wordNetOrigin.nodes[i].size > 0) wordNetOrigin.nodes[i][0] else null) ?: return@prevTermLoop
                    nodes[i].add(first)
                    if (nodes[i].size > 1) return@prevTermLoop
                } else {
                    return@prevTermLoop
                }
            }

            val followIndex = term.endIndex + 1
            if (nodes[followIndex].size == 0) {
                val target = wordNetOrigin.nodes[followIndex]
                if (target.size == 0) return@termLoop
                nodes[followIndex].addAll(target)
            }

            (followIndex + 1 until nodes.size).forEach followTermLoop@ { j ->
                if (nodes[j].size > 0) return@followTermLoop
                val first = (if (wordNetOrigin.nodes[j].size > 0) wordNetOrigin.nodes[j][0] else null) ?: return@followTermLoop
                nodes[j].add(first)
                if (nodes[j].size > 1) return@followTermLoop
            }
        }

    }

    private fun initNodes() {
        //根据词库构建
        for (i in 0 until sentence.length) {
            for (k in (i + 1)..sentence.length) {
                val current = sentence.substring(i, k)
                //println("current: " + current)
                val attribute = CoreDict.getAttribute(current)
                //println("attribute: " + attribute)
                if (attribute != null) {
                    nodes[i].add(Term(current, i, k - 1, attribute))
                }
            }
        }

        //处理空节点
        (0 until sentence.length).fold(mutableListOf<Triple<Int, Int, Byte>>()) { result, i ->
            val row = nodes[i]
            if (row.isEmpty()) {
                if (result.isEmpty()) {
                    result.add(Triple(i, i, CharTypeDict.get(sentence[i])))
                } else {
                    val last = result.last()
                    val currentType = CharTypeDict.get(sentence[i])
                    if (currentType == last.third) {
                        result[result.size - 1] = last.copy(second = i)
                    } else if (sentence[i] == '.' && last.third == CharTypeDict.CT_NUM) {
                        result[result.size - 1] = last.copy(second = i)
                    } else {
                        nodes[last.first].add(getTerm(last))
                        result.clear()
                        result.add(Triple(i, i, currentType))
                    }
                }
            } else {
                if (result.isNotEmpty()) {
                    val last = result.last()
                    nodes[last.first].add(getTerm(last))
                    result.clear()
                }
            }
            result
        }
    }

    private fun getTerm(triple: Triple<Int, Int, Byte>): Term {
        var nature = Nature.n
        val word = sentence.substring(triple.first, triple.second + 1)
        when (triple.third) {
            CharTypeDict.CT_INDEX, CharTypeDict.CT_NUM -> nature = Nature.m
            CharTypeDict.CT_DELIMITER, CharTypeDict.CT_OTHER -> nature = Nature.w
            CharTypeDict.CT_SINGLE -> nature = Nature.nx
        }
        return Term(word, triple.first, triple.second, WordAttribute(10000, mapOf(nature to 10000)))
    }

    override fun toString(): String {
        return "WordNet(nodes=${Arrays.toString(nodes)})"
    }

}

data class Term(val word: String, val startIndex: Int = -1, val endIndex: Int = -1, val attribute: WordAttribute = WordAttribute(1, mapOf(Nature.n to 1))) {
    fun guessNature(): Nature {
        return attribute.natures.maxBy { it.value }!!.key
    }

    override fun toString(): String {
        return "$word/${guessNature()}"
    }


}

//当前path下选中的词，和到当前path时的总权重
data class PathInfo(val term: Term, val maxWeight: Double)

/**
 * @frequence: 词频
 * @natures: 词性->频率
 */
data class WordAttribute(val frequency: Int, val natures: Map<Nature, Int>)

fun main(args: Array<String>) {
    val s1 = "碧桂园做事靠谱，理性，而恒大的老许像个疯子，29元还接盘万科，12元收购盛京银行，100亿香港买楼"
    val s2 = "南京市长江大桥"
    val s5 = "万科股权之争的故事发展令人应接不暇。从证监会刘主席指斥“妖精”、证监会、保监会联手祭出一堆处罚开始，又是华润将股份转让给深铁，3月16日又是中国恒大集团十家下属企业把所持的股东表决权、提案权及股东大会参会权不可撤销地委托给深铁。深铁行权份额高达29.38%，站在了要约收购的边上。此时，离万科于3月24日召开董事会会议只有一周时间。"

    val s6 = "first, 叀叁参叄叅, 优酷总裁魏明介绍了优酷2015年的内容战略，表示要以“大电影、大网剧、大综艺”为关键词"

    val wn = WordNet(s2)
    println("WordNet: $wn")
    val r = wn.calculateRoute()
    r.forEach { t ->
        print(s2.substring(t.startIndex, t.endIndex + 1))
        print("_")
    }
}
