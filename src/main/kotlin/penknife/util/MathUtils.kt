package penknife.util

import org.apache.commons.math3.util.FastMath


object MathUtils {

    /**
     * @return a log'd value of the input that is guarded.
     */
    fun guardedLogarithm(input: Double): Double {
        if (java.lang.Double.isNaN(input) || java.lang.Double.isInfinite(input)) {
            return 0.0
        } else if (input <= 0.0 || input <= -0.0) {
            // assume a quite low value of log(1e-5) ~= -11.51
            return -10.0
        } else {
            return FastMath.log(input)
        }
    }
}