package penknife.segment.dictionary

import com.google.common.base.Stopwatch
import com.google.common.io.Resources
import java.io.BufferedInputStream
import java.io.ObjectInputStream
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object BigramDict {

    val logger = Logger.getLogger("penknife.segment.dictionary.BigramDict")!!

    val datPath = "dictionary/CoreNatureDictionary.ngram.txt.table.bin"

    /**
     * 描述了词在pair中的范围，具体说来<br></br>
     * 给定一个词idA，从pair[start[idA]]开始的start[idA + 1] - start[idA]描述了一些接续的频次
     */
    private var start: IntArray = IntArray(0)
    /**
     * pair[偶数n]表示key，pair[n+1]表示frequency
     */
    private var pair: IntArray = IntArray(0)

    init {
        loadDat()
    }

    fun loadDat() {
        val stopWatch = Stopwatch.createStarted()
        ObjectInputStream(BufferedInputStream(Resources.getResource(datPath).openStream())).use { input ->
            this.start = input.readObject() as IntArray
            this.pair = input.readObject() as IntArray
        }
        logger.info("bigram dat loaded. cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)} ms")
        stopWatch.stop()
    }

    /**
     * 二分搜索，由于二元接续前一个词固定时，后一个词比较少，所以二分也能取得很高的性能
     * @param a 目标数组
     * *
     * @param fromIndex 开始下标
     * *
     * @param length 长度
     * *
     * @param key 词的id
     * *
     * @return 共现频次
     */
    private fun binarySearch(a: IntArray, fromIndex: Int, length: Int, key: Int): Int {

        return a.binarySearch(key, fromIndex, fromIndex + length)

        /*var low = fromIndex
        var high = fromIndex + length - 1

        while (low <= high) {
            val mid = (low + high).ushr(1)
            val midVal = a[mid shl 1]

            if (midVal < key)
                low = mid + 1
            else if (midVal > key)
                high = mid - 1
            else
                return mid // key found
        }
        return -(low + 1)  // key not found.*/
    }

    /**
     * 获取共现频次

     * @param a 第一个词
     * *
     * @param b 第二个词
     * *
     * @return 第一个词@第二个词出现的频次
     */
    fun getBiFrequency(a: String, b: String): Int {
        val idA = CoreDict.dat.exactMatchSearch(a)
        if (idA == -1) {
            return 0
        }
        val idB = CoreDict.dat.exactMatchSearch(b)
        if (idB == -1) {
            return 0
        }
        var index = binarySearch(pair, start[idA], start[idA + 1] - start[idA], idB)
        if (index < 0) return 0
        index = index shl 1
        return pair[index + 1]
    }

    /**
     * 获取共现频次
     * @param idA 第一个词的id
     * *
     * @param idB 第二个词的id
     * *
     * @return 共现频次
     */
    fun getBiFrequency(idA: Int, idB: Int): Int {
        if (idA == -1 || idB == -1) {
            return 1000   // -1表示用户词典，返回正值增加其亲和度
        }
        var index = binarySearch(pair, start[idA], start[idA + 1] - start[idA], idB)
        if (index < 0) return 0
        index = index shl 1
        return pair[index + 1]
    }

    /**
     * 获取词语的ID

     * @param a 词语
     * *
     * @return id
     */
    fun getWordID(a: String): Int {
        return CoreDict.dat.exactMatchSearch(a)
    }

}

fun main(args: Array<String>) {
    //val file = "coreDict.ngram.dat"
    //BigramDict.loadFromTxtFile()
    //BigramDict.saveDatFile(file)
    val freq = BigramDict.getBiFrequency("三沙市 ", "海上")
    print("freq: $freq")
}