package penknife.segment.dictionary

import com.google.common.base.Charsets
import com.google.common.io.Resources
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

object NRTransformMatrixDict {

    val path = "dictionary/nr.tr.txt"
    val data = NRTransformMatrixData()

    init {
        data.load(path)
    }
}

class NRTransformMatrixData {
    /**
     * 内部标签下标最大值不超过这个值，用于矩阵创建
     */
    private var ordinaryMax: Int = 0

    /**
     * 储存转移矩阵
     */
    private var matrix: Array<IntArray>? = null

    /**
     * 储存每个标签出现的次数
     */
    private var total: IntArray? = null

    /**
     * 所有标签出现的总次数
     */
    private var totalFrequency: Int = 0
        get

    // HMM的五元组
    /**
     * 隐状态
     */
    private var states: IntArray? = null
    /**
     * 初始概率
     */
    private var startProbability: DoubleArray? = null
    /**
     * 转移概率
     */
    var transitProbability: Array<DoubleArray>? = null
        get
        private set

    fun getFrequency(from: String, to: String): Int {
        return getFrequency(NR.valueOf(from), NR.valueOf(to))
    }

    /**
     * 获取转移频次

     * @param from
     * *
     * @param to
     * *
     * @return
     */
    fun getFrequency(from: NR, to: NR): Int {
        return matrix!![from.ordinal][to.ordinal]
    }

    /**
     * 获取e的总频次

     * @param e
     * *
     * @return
     */
    fun getTotalFrequency(e: NR): Int {
        return total!![e.ordinal]
    }

    fun load(path: String) {

        val lines = BufferedReader(InputStreamReader(Resources.getResource(path).openStream(), Charsets.UTF_8)).readLines()

        var line = lines[0]
        val labels = line.split(",".toRegex()).drop(1)
        // 为了制表方便，第一个label是废物，所以要抹掉它
        val ordinaryArray = IntArray(labels.size)
        ordinaryMax = 0
        for (i in ordinaryArray.indices) {
            ordinaryArray[i] = NR.valueOf(labels[i]).ordinal
            ordinaryMax = Math.max(ordinaryMax, ordinaryArray[i])
        }
        ++ordinaryMax
        matrix = Array(ordinaryMax) { IntArray(ordinaryMax) }
        for (i in 0..ordinaryMax - 1) {
            for (j in 0..ordinaryMax - 1) {
                matrix!![i][j] = 0
            }
        }

        (1 until lines.size).forEach { idx ->
            line = lines[idx]
            val paramArray = line.split(",".toRegex())
            val currentOrdinary = NR.valueOf(paramArray[0]).ordinal
            for (i in ordinaryArray.indices) {
                matrix!![currentOrdinary][ordinaryArray[i]] = Integer.valueOf(paramArray[1 + i])!!
            }
        }

        total = IntArray(ordinaryMax)
        for (j in 0..ordinaryMax - 1) {
            total!![j] = 0
            for (i in 0..ordinaryMax - 1) {
                total!![j] += matrix!![j][i] // 按行累加
            }
        }
        for (j in 0..ordinaryMax - 1) {
            if (total!![j] == 0) {
                for (i in 0..ordinaryMax - 1) {
                    total!![j] += matrix!![i][j] // 按列累加
                }
            }
        }
        for (j in 0..ordinaryMax - 1) {
            totalFrequency += total!![j]
        }
        // 下面计算HMM四元组
        states = ordinaryArray
        startProbability = DoubleArray(ordinaryMax)
        states!!.toList().forEach { s ->
            val frequency = total!![s] + 1e-8
            startProbability!![s] = -Math.log(frequency / totalFrequency)
        }
        transitProbability = Array(ordinaryMax) { DoubleArray(ordinaryMax) }
        for (from in states!!.toList()) {
            for (to in states!!.toList()) {
                val frequency = matrix!![from][to] + 1e-8
                transitProbability!![from][to] = -Math.log(frequency / total!![from])
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder("TransformMatrixDictionary{")
        sb.append(", ordinaryMax=").append(ordinaryMax)
        sb.append(", matrix=").append(Arrays.toString(matrix))
        sb.append(", total=").append(Arrays.toString(total))
        sb.append(", totalFrequency=").append(totalFrequency)
        sb.append('}')
        return sb.toString()
    }

}

fun main(args: Array<String>) {
    print(NRTransformMatrixDict.data)
}