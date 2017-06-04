package penknife.classification.bayes

import penknife.util.RealVectorExt.getLength
import penknife.util.RealVectorExt.sparseForEachIndexed
import org.apache.commons.math3.linear.*
import org.apache.commons.math3.util.FastMath

object MultinomialNaiveBayes {

    private val LOW_PROBABILITY = FastMath.log(1e-8)

    private var probabilityMatrix: RealMatrix? = null
    private var classPriorProbability: RealVector? = null

    fun predict(features: RealVector): RealVector {
        return getProbabilityDistribution(features)
    }

    fun getProbabilityDistribution(document: RealVector): RealVector {

        val numClasses = classPriorProbability?.dimension ?: 0
        val distribution = ArrayRealVector(numClasses)
        // loop through all classes and get the max probable one
        for (i in 0 until numClasses) {
            val probability = getProbabilityForClass(document, i)
            distribution.setEntry(i, probability)
        }

        val probabilitySum = (0 until numClasses).map {
            val normalizedProbability = FastMath.exp(distribution.getEntry(it) - distribution.maxValue + (classPriorProbability?.getEntry(it) ?: 0.0))
            distribution.setEntry(it, normalizedProbability)
            normalizedProbability
        }.sum()

        // since the sum is sometimes not 1, we need to divide by the sum
        val result = distribution.mapDivideToSelf(probabilitySum) as ArrayRealVector

        return result
    }

    private fun getProbabilityForClass(document: RealVector, classIndex: Int): Double {
        var probabilitySum = 0.0

        document.sparseForEachIndexed { index, wordCount ->
            var probabilityOfToken: Double = probabilityMatrix?.getEntry(classIndex, index) ?: 0.0
            probabilityOfToken = if (probabilityOfToken == 0.0) LOW_PROBABILITY else probabilityOfToken

            probabilitySum += wordCount * probabilityOfToken
        }

        return probabilitySum
    }

    fun train(features: List<RealVector>, outcome: List<RealVector>): Unit {

        val numDistinctClasses = if (outcome.first().dimension == 1) 2 else outcome.first().dimension

        //每个分类下每个词的概率
        probabilityMatrix = OpenMapRealMatrix(numDistinctClasses, features.first().dimension)

        // 每个分类中的元素个数
        val tokenPerClass = IntArray(numDistinctClasses)
        // 每个分类中的文档数
        val numDocumentsPerClass = IntArray(numDistinctClasses)

        var numDocumentsSeen = 0
        features.zip(outcome).forEach {
            observe(it.first, it.second, numDistinctClasses, tokenPerClass, numDocumentsPerClass)
            numDocumentsSeen++
        }

        //P(c|i) = P(i|c)p(c)/p(i)    //p(i)可忽略

        //计算P(i|c)用
        for (row in 0 until numDistinctClasses) {
            val rowVector: RealVector = probabilityMatrix?.getRowVector(row) ?: OpenMapRealVector()
            val v: Int = probabilityMatrix?.columnDimension ?: 0
            val normalizer = FastMath.log((tokenPerClass[row] + v - 1).toDouble())

            //数量转成log
            rowVector.sparseForEachIndexed { index, value ->
                val logProbability = FastMath.log(value) - normalizer
                probabilityMatrix?.setEntry(row, index, logProbability)
            }
        }

        //每个分类出现的概率P(c)
        classPriorProbability = ArrayRealVector(numDistinctClasses)

        for (i in 0 until numDistinctClasses) {
            val prior = FastMath.log(numDocumentsPerClass[i].toDouble()) - FastMath.log(numDocumentsSeen.toDouble())
            classPriorProbability!!.setEntry(i, prior)
        }


    }

    fun observe(document: RealVector, outcome: RealVector, numDistinctClasses: Int, tokenPerClass: IntArray, numDocumentsPerClass: IntArray) {
        //当前分类，如果2分类，则outcome里只有一个元素
        val predictedClass: Int = if (numDistinctClasses == 2) outcome.getEntry(0).toInt() else outcome.maxIndex

        //该分类中的元素数增加
        tokenPerClass[predictedClass] += document.getLength()
        //包含该分类的文档数增加一个
        numDocumentsPerClass[predictedClass]++

        //增加该分类下的token数量
        document.sparseForEachIndexed { index, value ->
            probabilityMatrix?.addToEntry(predictedClass, index, value)
        }

    }


}

fun main(args: Array<String>) {
    val features = arrayOf<RealVector>(OpenMapRealVector(doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0)), OpenMapRealVector(doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0)), OpenMapRealVector(doubleArrayOf(1.0, 1.0, 0.0, 0.0, 0.0)), OpenMapRealVector(doubleArrayOf(0.0, 0.0, 1.0, 1.0, 1.0)), OpenMapRealVector(doubleArrayOf(0.0, 0.0, 0.0, 1.0, 1.0)))
    val outcome = arrayOf(ArrayRealVector(doubleArrayOf(1.0)), ArrayRealVector(doubleArrayOf(1.0)), ArrayRealVector(doubleArrayOf(1.0)), ArrayRealVector(doubleArrayOf(0.0)), ArrayRealVector(doubleArrayOf(0.0)))

    MultinomialNaiveBayes.train(features.toList(), outcome.toList())

    val result = MultinomialNaiveBayes.predict(ArrayRealVector(doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0)))


    println("$result")
}

