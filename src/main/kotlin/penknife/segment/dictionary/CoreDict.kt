package penknife.segment.dictionary

import com.google.common.base.Stopwatch
import com.google.common.io.Resources
import penknife.segment.logic.WordAttribute
import penknife.segment.util.BufferUtils
import penknife.segment.util.dat.DoubleArrayTrie
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object CoreDict {

    val path = "dictionary/CoreNatureDictionary.txt.bin"
    val dat = CordDictDat()

    init {
        dat.loadDataFromDat(path)
    }

    fun getAttribute(word: String): WordAttribute? {
        return dat.get(word)
    }

    fun contains(word: String): Boolean {
        return dat.containsKey(word)
    }

}

class CordDictDat : DoubleArrayTrie<WordAttribute>() {

    val logger = Logger.getLogger("penknife.segment.dictionary.CordDictDat")!!
    val totalFrequency = 221894

    override fun loadDataFromDat(path: String) {
        val stopWatch = Stopwatch.createStarted()
        Files.newByteChannel(Paths.get(Resources.getResource(path).file)).use { channel ->
            val size = channel.size().toInt().shr(2)
            val data = IntArray(size)
            val natureIndexArray = Nature.values()
            val bb = ByteBuffer.allocateDirect(64 * 1024)
            BufferUtils.readInts(channel, bb, data)

            //logger.info("load from channel cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)}")
            var i = 0
            val vsize = data[i++]
            val attributes = Array<WordAttribute?>(vsize, { null })
            for (j in 0 until vsize) {
                val freq = data[i++]
                val length = data[i++]
                val nmap = mutableMapOf<Nature, Int>()
                for (k in 0 until length) {
                    val nindex = data[i++]
                    val nfreq = data[i++]
                    nmap[natureIndexArray[nindex]] = nfreq
                }
                attributes[j] = WordAttribute(freq, nmap)
            }
            this.values = attributes
            //logger.info("load attribute cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)}")
            val dsize = data[i++]
            this.base = IntArray(dsize + 65535)
            this.check = IntArray(dsize + 65535)
            for (j in 0 until dsize) {
                this.base[j] = data[i++]
                this.check[j] = data[i++]
            }
            logger.info("core dictionary loaded. cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)} ms")
            stopWatch.stop()
        }
    }

    /*fun loadDataFromDat(dis: DataInputStream?) {
        Preconditions.checkNotNull(dis)
        val stopWatch = Stopwatch.createStarted()
        //填充values
        val vsize = dis!!.readInt()
        this.values = (0 until vsize).map {
            val freq = dis.readInt()
            val length = dis.readInt()
            val nmap = (0 until length).map {
                val nindex = dis.readInt()
                val nfreq = dis.readInt()
                Pair(Nature.values()[nindex], nfreq)
            }.toMap()
            WordAttribute(freq, nmap)
        }.toTypedArray()
        //填充dat
        val dsize = dis.readInt()
        this.base = IntArray(dsize + 65535)   // 多留一些，防止越界
        this.check = IntArray(dsize + 65535)
        for (i in 0 until dsize) {
            base[i] = dis.readInt()
            check[i] = dis.readInt()
        }
        logger.info("load core dat cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)}")
        this.used = null
    }*/

}

fun main(args: Array<String>) {
    val attr = CoreDict.getAttribute("关键词")
    println("attr: " + attr)
    //CoreDict.saveToDatFile("coreDict.dat")
}