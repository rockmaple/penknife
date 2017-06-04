package penknife.util

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.RealMatrix

object RealMatrixExt {

    fun RealMatrix.logMatrix(): RealMatrix {
        val matrix = Array2DRowRealMatrix(this.rowDimension, this.columnDimension)
        for (i in 0 until this.rowDimension) {
            for (j in 0 until this.columnDimension) {
                matrix.setEntry(i, j, MathUtils.guardedLogarithm(this.getEntry(i, j)))
            }
        }
        return matrix
    }

    fun RealMatrix.substractBy(d: Double): RealMatrix {
        val matrix = Array2DRowRealMatrix(this.rowDimension, this.columnDimension)
        for (i in 0 until this.rowDimension) {
            for (j in 0 until this.columnDimension) {
                matrix.setEntry(i, j, this.getEntry(i, j) - d)
            }
        }
        return matrix
    }

    fun RealMatrix.sum(): Double {
        var sum = 0.0
        for (i in 0 until this.rowDimension) {
            for (j in 0 until this.columnDimension) {
                sum += this.getEntry(i, j)
            }
        }
        return sum
    }

}