package penknife.segment.dictionary

import com.google.common.base.Stopwatch
import com.google.common.io.Resources
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object CharTypeDict {

    val logger = Logger.getLogger("penknife.segment.dictionary.CharTypeDict")!!

    /**
     * 单字节
     */
    val CT_SINGLE: Byte = 5

    /**
     * 分隔符"!,.?()[]{}+=
     */
    val CT_DELIMITER = (CT_SINGLE + 1).toByte()

    /**
     * 中文字符
     */
    val CT_CHINESE = (CT_SINGLE + 2).toByte()

    /**
     * 字母
     */
    val CT_LETTER = (CT_SINGLE + 3).toByte()

    /**
     * 数字
     */
    val CT_NUM = (CT_SINGLE + 4).toByte()

    /**
     * 序号
     */
    val CT_INDEX = (CT_SINGLE + 5).toByte()

    /**
     * 其他
     */
    val CT_OTHER = (CT_SINGLE + 12).toByte()

    private val dictPath = "dictionary/CharType.bin"
    private val charTypes = ByteArray(65536)

    init {
        load()
    }

    fun get(c: Char): Byte {
        return charTypes[c.toInt()]
    }

    fun load() {

        val stopWatch = Stopwatch.createStarted()

        val path = Paths.get(Resources.getResource(dictPath).toURI())
        val bytes = Files.readAllBytes(path)

        var i = 0
        while (i < bytes.size) {
            val begin = getChar(bytes, i)
            i += 2
            val end = getChar(bytes, i)
            i += 2
            val t = bytes[i++]
            (begin.toInt()..end.toInt()).forEach { k ->
                charTypes[k] = t
            }
        }

        logger.info("char type dictionary loaded. cost: ${stopWatch.elapsed(TimeUnit.MILLISECONDS)} ms")
        stopWatch.stop()

    }

    fun getChar(bytes: ByteArray, start: Int): Char {
        return (((bytes[start].toInt() and 0xff) shl 8) or (bytes[start + 1].toInt() and 0xFF)).toChar()
    }

}

fun main(args: Array<String>) {
    val r = CharTypeDict.get('叞')
    println("r: " + r)
}
