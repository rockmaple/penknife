package penknife.classification.regression

import org.apache.commons.math3.linear.RealVector
import java.util.*

object GradientDescent {

    val COST_HISTORY = 3
    val alpha = 0.0  //TODO

    /*fun minimize(costFunction: LogisticRegressionCostFunction,
                 pInput: RealVector,
                 maxIterations: Int): RealVector {

        val lastCosts = DoubleArray(COST_HISTORY, {Double.MAX_VALUE})
        val lastIndex = lastCosts.size - 1
        var lastTheta: RealVector? = null
        var lastGradient: RealVector? = null
        var theta = pInput
        var alpha = this.alpha

        for (iteration in 0 until maxIterations) {
            val evaluateCost = costFunction.evaluateCost(theta)

        }

    }*/

}