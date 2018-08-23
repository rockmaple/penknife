package penknife.segment.dictionary

import com.google.common.io.Resources
import penknife.segment.logic.WordAttribute
import penknife.segment.util.dat.DoubleArrayTrie
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Logger

object UserDict {

    private const val path = "dictionary/userDict.txt"
    val dat = UserDictDat()

    init {
        dat.loadDataFromTxt(path)
    }

    fun contains(word: String): Boolean {
        return this.dat.containsKey(word)
    }

    fun getAttribute(word: String): WordAttribute? {
        return this.dat.get(word)
    }

    fun containPrefix(prefix: String): Boolean {
        return this.dat.transit(prefix, 1) > 0
    }

}

class UserDictDat : DoubleArrayTrie<WordAttribute>() {

    val logger = Logger.getLogger("penknife.segment.dictionary.UserDictDat")!!

    override fun loadDataFromTxt(path: String?) {
        val wordToAttributeMap = TreeMap<String, WordAttribute>()
        BufferedReader(InputStreamReader(Resources.getResource(path).openStream(), Charsets.UTF_8)).forEachLine { line ->
            val splitted = line.split("\\s".toRegex())
            //logger.info("line: " + line)
            val size = splitted.size
            when {
                (size > 2) -> {
                    val natureCount = (size - 1) / 2
                    val word = splitted[0]
                    var freq = 0
                    val map = (0 until natureCount).map { i ->
                        val currentFreq = splitted[2 + 2 * i].toInt()
                        freq += currentFreq
                        Pair(Nature.valueOf(splitted[1 + 2 * i]), currentFreq)
                    }.toMap()
                    wordToAttributeMap[word] = WordAttribute(freq, map)
                }
                (size == 1) -> {
                    val word = splitted[0]
                    val frequency = 100
                    wordToAttributeMap[word] = WordAttribute(frequency, mapOf(Nature.n to frequency))
                }
                else -> {
                    logger.warning("splitted size: $size")
                }
            }
        }
        build(wordToAttributeMap)
    }

    override fun saveData(path: String?) {
    }

}

fun main(args: Array<String>) {
    val result = UserDict.dat.exactMatchSearch("盛京银")
    println("result: $result")
    val t = UserDict.dat.transit("盛京银行", 1)
    println("t: $t")
    val prefix = UserDict.containPrefix("盛京银行")
    println("prefix: $prefix")
}