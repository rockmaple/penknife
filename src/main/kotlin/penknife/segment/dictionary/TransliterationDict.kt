package penknife.segment.dictionary

import com.google.common.base.Stopwatch
import com.google.common.io.Resources
import penknife.segment.util.BufferUtils
import penknife.segment.util.dat.DoubleArrayTrie
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object TransliterationDict {

    val path = "dictionary/nrf.txt.trie.dat"
    val dat = TransliterationDat()

    init {
        load()
    }

    fun containsKey(key: String): Boolean {
        return dat.containsKey(key)
    }

    /**
     * 时报包含key，且key至少长length
     * @param key
     * *
     * @param length
     * *
     * @return
     */
    fun containsKey(key: String, length: Int): Boolean {
        if (!dat.containsKey(key)) {
            return false
        }
        return key.length >= length
    }

    fun load() {
        dat.loadDataFromDat(path)
    }

}

class TransliterationDat : DoubleArrayTrie<Boolean>() {

    val logger = Logger.getLogger("penknife.segment.dictionary.TransliterationDat")!!

    override fun loadDataFromDat(path: String) {
        val stopWatch = Stopwatch.createStarted()
        Files.newByteChannel(Paths.get(Resources.getResource(path).file)).use { channel ->
            val fileSize = channel.size().toInt().shr(2)
            val data = IntArray(fileSize)
            val bb = ByteBuffer.allocateDirect(64 * 1024)
            BufferUtils.readInts(channel, bb, data)
            var i = 0
            val dsize = data[i++]
            this.base = IntArray(dsize + 65535)
            this.check = IntArray(dsize + 65535)
            for (j in 0 until dsize) {
                this.base[j] = data[i++]
                this.check[j] = data[i++]
            }
        }
        logger.info("transliteration dict loaded. cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)}")
        stopWatch.stop()
    }
}

fun main(args: Array<String>) {
    val result = TransliterationDict.containsKey("丁达尼亚亚")
    println("result: $result")
}