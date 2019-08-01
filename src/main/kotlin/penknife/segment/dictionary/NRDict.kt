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

object NRDict {

    val path = "dictionary/nr.txt"
    val dat = NRDictDat()

    init {
        dat.loadDataFromDat(path)
    }

    fun get(key: String): EnumItem<NR>? {
        return dat.get(key)
    }

}

class NRDictDat : DoubleArrayTrie<EnumItem<NR>>() {

    val logger = Logger.getLogger("penknife.segment.dictionary.NRDictDat")!!

    override fun loadDataFromDat(path: String) {

        val stopWatch = Stopwatch.createStarted()

        Files.newByteChannel(Paths.get(Resources.getResource("$path.value.dat").file)).use { channel ->
            val data = IntArray(channel.size().toInt().shr(2))
            val bb = ByteBuffer.allocateDirect(64 * 1024)
            BufferUtils.readInts(channel, bb, data)

            val nrArray = NR.values()

            var index = 0
            val size = data[index++]
            val valueArray = arrayOfNulls<EnumItem<NR>>(size)

            (0 until size).forEach { i ->
                val currentSize = data[index++]
                val item = EnumItem<NR>()
                (0 until currentSize).forEach { _ ->
                    val nr = nrArray[data[index++]]
                    val freq = data[index++]
                    item.labelMap[nr] = freq
                }
                valueArray[i] = item
            }
            this.values = valueArray
        }

        Files.newByteChannel(Paths.get(Resources.getResource(path + ".trie.dat").file)).use { channel ->
            val data = IntArray(channel.size().toInt().shr(2))
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

        logger.info("nr dictionary loaded. cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)} ms")
        stopWatch.stop()
    }
}

fun main(args: Array<String>) {
    val v = NRDict.dat.get("12")
    println("v: $v")
}