package penknife.segment.logic

import com.google.common.base.Charsets
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.google.common.io.Resources
import penknife.segment.util.CharUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Logger

class HMM {

    companion object {
        val logger = Logger.getLogger("penknife.segment.logic.HMM")!!
        fun create(): HMM {
            val hmm = HMM()
            hmm.loadModel()
            return hmm
        }
    }

    private val states = listOf('B', 'M', 'E', 'S')
    val startP: MutableMap<Char, Double> = mutableMapOf()
    val transP: Table<Char, Char, Double> = HashBasedTable.create()
    var emitP: Table<Char, Char, Double> = HashBasedTable.create()
    var prevStatus: MutableMap<Char, CharArray> = mutableMapOf()

    val MIN_VALUE = -3.14e100
    val PROB_EMIT = "hmm/prob_emit.txt"

    fun split(sentence: String): List<Term> {
        val words = mutableListOf<Term>()
        var chinese = StringBuilder()
        var other = StringBuilder()

        sentence.forEach { c ->
            if (CharUtils.isCnLetter(c)) {
                if (other.isNotEmpty()) {
                    val taggedWords = processOtherWords(other.toString())
                    words.addAll(taggedWords)
                    other = StringBuilder()
                }
                chinese.append(c)
            } else {
                if (chinese.isNotEmpty()) {
                    val taggedWords = viterbi(chinese.toString())
                    words.addAll(taggedWords)
                    chinese = StringBuilder()
                }
                other.append(c)
            }
        }

        if (chinese.isNotEmpty())
            words.addAll(viterbi(chinese.toString()))
        else {
            words.addAll(processOtherWords(other.toString()))
        }
        return words
    }

    fun viterbi(input: String): MutableList<Term> {
        val weightMatrix: Table<Int, Char, Double> = HashBasedTable.create(input.length, states.size)
        //比如 path[0]['B'] 代表 weightMatrix[0]['B']取到最大时，前一个字的状态
        val path: Table<Int, Char, Char> = HashBasedTable.create(input.length, states.size)

        states.forEach { state ->
            //初始概率 * 发射概率
            val v = (startP[state] ?: 0.0) + (emitP.get(state, input[0]) ?: MIN_VALUE)
            //初始状态
            weightMatrix.put(0, state, v)
        }

        for (i in 1 until input.length) {
            states.forEach { state ->
                //发射概率
                val emp = emitP.get(state, input[i]) ?: MIN_VALUE

                //前值*发射概率*转移概率最大时当前概率值以及前一个状态
                val max = prevStatus[state]?.map { prevState ->
                    //前一状态概率*发射概率*转移概率
                    val v = weightMatrix.get(i - 1, prevState) + (transP.get(prevState, state) ?: MIN_VALUE) + emp
                    StateAndProb(prevState, v)
                }?.maxBy(StateAndProb::prob)

                //logger.info("max: " + max?.prob + " " + max?.state)
                weightMatrix.put(i, state, max?.prob ?: MIN_VALUE)
                path.put(i, state, max?.state!!)
            }
        }
        //结尾只能是'E'或者'S',用来找最后一个path的值
        val max = listOf('E', 'S').map { s ->
            StateAndProb(s, weightMatrix.get(input.length - 1, s))
        }.maxBy(StateAndProb::prob)

        //跟据最后一个状态的值倒推最大路径
        var s = max?.state!!
        val maxPath = CharArray(input.length)
        maxPath[input.length - 1] = s
        ((input.length - 1) downTo 1).forEach { i ->
            s = path.get(i, s)
            maxPath[i - 1] = s
        }

        val taggedWords = mutableListOf<Term>()
        //根据标记切分词
        var begin = 0
        var next = 0
        for (i in 0 until input.length) {
            val pos = maxPath[i]
            when (pos) {
                'B' -> begin = i
                'E' -> {
                    taggedWords.add(Term(input.substring(begin, i + 1)))
                    next = i + 1
                }
                'S' -> {
                    taggedWords.add(Term(input.substring(i, i + 1)))
                    next = i + 1
                }
            }
        }

        if (next < input.length) taggedWords.add(Term(input.substring(next)))

        return taggedWords
    }

    private fun processOtherWords(other: String): List<Term> {
        val tokens = ArrayList<Term>()
        val mat = CharUtils.RE_SKIP.matcher(other)
        var offset = 0
        while (mat.find()) {
            if (mat.start() > offset) {
                tokens.add(Term(other.substring(offset, mat.start())))
            }
            tokens.add(Term(mat.group()))
            offset = mat.end()
        }
        if (offset < other.length)
            tokens.add(Term(other.substring(offset)))
        return tokens
    }

    private fun loadModel() {

        initValues()

        var current = ' '
        BufferedReader(InputStreamReader(Resources.getResource(PROB_EMIT).openStream(), Charsets.UTF_8)).forEachLine { line ->
            val tokens = line.split("\t".toRegex())
            if (tokens.size == 1) {
                current = tokens[0][0]
            } else {
                emitP.put(current, tokens[0][0], tokens[1].toDouble())
            }
        }

    }

    private fun initValues() {

        prevStatus.put('B', charArrayOf('E', 'S'))
        prevStatus.put('M', charArrayOf('M', 'B'))
        prevStatus.put('S', charArrayOf('S', 'E'))
        prevStatus.put('E', charArrayOf('B', 'M'))

        startP.put('B', -0.26268660809250016)
        startP.put('E', -3.14e+100)
        startP.put('M', -3.14e+100)
        startP.put('S', -1.4652633398537678)

        transP.put('B', 'E', -0.510825623765990)
        transP.put('B', 'M', -0.916290731874155)
        transP.put('E', 'B', -0.5897149736854513)
        transP.put('E', 'S', -0.8085250474669937)
        transP.put('M', 'E', -0.33344856811948514)
        transP.put('M', 'M', -1.2603623820268226)
        transP.put('S', 'B', -0.7211965654669841)
        transP.put('S', 'S', -0.6658631448798212)
    }

}

data class StateAndProb(val state: Char, val prob: Double)

fun main(args: Array<String>) {
    val hmm = HMM()
    hmm.transP.put('a', 'b', 1.2)
    println(hmm.transP.get('a', 'b'))
    println(hmm.transP.get('c', 'd') ?: hmm.MIN_VALUE)
}
