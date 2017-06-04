package penknife.classification.regression

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.util.FastMath
import penknife.util.RealMatrixExt.logMatrix
import penknife.util.RealMatrixExt.substractBy
import penknife.util.RealMatrixExt.sum
import penknife.util.RealVectorExt.pow
import penknife.util.RealVectorExt.sparseForEachIndexed
import penknife.util.RealVectorExt.sum


/**
 * @param x feature matrix
 * @param y outcome matrix
 * @param lambda
 */
class LogisticRegressionCostFunction(val x: RealMatrix, val y: RealMatrix, val lambda: Double) {

    val CLIP = 30.0

    val m = x.rowDimension
    val xTransposed = x.transpose()!!

    fun evaluateCost(theta: RealVector): CostGradientTuple {

        val activation = ArrayRealVector(theta.dimension)

        x.preMultiply(theta).sparseForEachIndexed { i, entry ->
            activation.setEntry(i, sigmoid(entry))
        }

        //1行n列数组
        val hypo = Array2DRowRealMatrix(activation.toArray())

        val error = calculateLoss(y, hypo)

        //对于当前theta, 计算值-实际值
        val loss = hypo.subtract(y)

        var j = error / m

        var gradient = xTransposed.preMultiply(loss.getRowVector(0)).mapDivide(m.toDouble())

        if (lambda != 0.0) {
            val reg = theta.mapMultiply(lambda / m)
            // don't regularize the bias
            reg.setEntry(0, 0.0)
            gradient = gradient.add(reg)
            j += lambda * theta.pow(2.0).sum() / m
        }

        return CostGradientTuple(j, gradient)
    }

    fun sigmoid(input: Double): Double {
        return FastMath.min(CLIP, FastMath.max(-CLIP, 1.0 / (1.0 + FastMath.exp(-input))))
    }

    fun calculateLoss(y: RealMatrix, hypothesis: RealMatrix): Double {

        val negativeOutcome = y.substractBy(1.0)
        val inverseOutcome = y.scalarMultiply(-1.0)
        val negativeHypo = hypothesis.substractBy(1.0)
        val negativeLogHypo = negativeHypo.logMatrix()
        val positiveLogHypo = hypothesis.logMatrix()
        val negativePenalty = negativeOutcome.multiply(negativeLogHypo)
        val positivePenalty = inverseOutcome.multiply(positiveLogHypo)

        return positivePenalty.subtract(negativePenalty).sum() / y.rowDimension
    }
}

data class CostGradientTuple(val cost: Double, val gradient: RealVector)