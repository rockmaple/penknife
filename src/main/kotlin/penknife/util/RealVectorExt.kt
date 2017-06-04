package penknife.util

import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.util.FastMath

object RealVectorExt {

    val sparseMissingEntryValue = 0.0

    //忽略missingEntries
    fun RealVector.getLength(): Int {
        //无值的为NaN
        return (0 until this.dimension).count { this.getEntry(it) != sparseMissingEntryValue }
    }

    //忽略missingEntries
    fun RealVector.sparseForEachIndexed(f: (Int, Double) -> Unit) {
        (0 until this.dimension).forEach {
            val value = this.getEntry(it)
            //OpenMapRealVector的missingEntries是0.0
            if (value != sparseMissingEntryValue) {
                f(it, value)
            }
        }
    }

    fun RealVector.pow(x: Double): RealVector {

        val v = ArrayRealVector(this.dimension)

        for (i in 0 until v.dimension) {
            var value: Double
            // it is faster to multiply when we having ^2
            if (x == 2.0) {
                value = this.getEntry(i) * this.getEntry(i)
            } else {
                value = FastMath.pow(this.getEntry(i), x)
            }
            v.setEntry(i, value)
        }
        return v

    }

    fun RealVector.sum(): Double {
        return (0 until this.dimension).sumByDouble { this.getEntry(it) }
    }

}
