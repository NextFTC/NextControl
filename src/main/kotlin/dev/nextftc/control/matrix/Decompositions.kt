/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.matrix

import org.ojalgo.matrix.MatrixR064
import org.ojalgo.matrix.decomposition.Cholesky
import org.ojalgo.matrix.decomposition.LU
import org.ojalgo.matrix.decomposition.QR

/** Data class for LLT (Cholesky) decomposition, with rank-1 update/downdate. */
@ConsistentCopyVisibility
data class LLTDecomposition internal constructor(
    private val chol: Cholesky<Double>,
    private var mat: MatrixR064
) {
    /** Lower-triangular matrix L such that mat = L*L^T */
    val L: Matrix get() = Matrix(MatrixR064.FACTORY.copy(chol.getL()))

    /** In-place rank-1 update: mat := mat + x*x^T */
    fun update(x: DoubleArray) {
        // x must be column vector of correct size
        // Update the matrix with rank-1 update
        val n = mat.countRows().toInt()
        require(x.size == n) { "Vector size must match matrix dimension" }

        val updated = mat.copy()
        for (i in 0 until n) {
            for (j in 0 until n) {
                updated.set(i.toLong(), j.toLong(), updated.get(i.toLong(), j.toLong()) + x[i] * x[j])
            }
        }
        chol.decompose(updated)
        mat = MatrixR064.FACTORY.copy(updated)
    }

    /** In-place rank-1 downdate: mat := mat - x*x^T */
    fun downdate(x: DoubleArray) {
        // x must be column vector of correct size
        val n = mat.countRows().toInt()
        require(x.size == n) { "Vector size must match matrix dimension" }

        val updated = mat.copy()
        for (i in 0 until n) {
            for (j in 0 until n) {
                updated.set(i.toLong(), j.toLong(), updated.get(i.toLong(), j.toLong()) - x[i] * x[j])
            }
        }
        chol.decompose(updated)
        mat = MatrixR064.FACTORY.copy(updated)
    }
}

/** Data class for LDLT decomposition. */
@ConsistentCopyVisibility
data class LDLTDecomposition internal constructor(
    private val ldlt: Cholesky<Double>,
    private val mat: MatrixR064
) {
    val L: Matrix get() {
        val cholL = ldlt.getL()
        // Extract unit lower triangular L from Cholesky L
        val n = cholL.countRows().toInt()
        val result = MatrixR064.FACTORY.makeEye(n, n).copy()
        for (i in 0 until n) {
            for (j in 0 until i) {
                val diag = cholL.get(i.toLong(), i.toLong())
                if (diag != 0.0) {
                    result.set(i.toLong(), j.toLong(), cholL.get(i.toLong(), j.toLong()) / diag)
                }
            }
        }
        return Matrix(MatrixR064.FACTORY.copy(result))
    }

    val D: Matrix get() {
        val cholL = ldlt.getL()
        // Extract diagonal D from Cholesky L
        val n = cholL.countRows().toInt()
        val result = MatrixR064.FACTORY.make(n, n).copy()
        for (i in 0 until n) {
            val diag = cholL.get(i.toLong(), i.toLong())
            result.set(i.toLong(), i.toLong(), diag * diag)
        }
        return Matrix(MatrixR064.FACTORY.copy(result))
    }
}

/** Data class for LU decomposition. */
@ConsistentCopyVisibility
data class LUDecomposition internal constructor(
    private val lu: LU<Double>,
    private val mat: MatrixR064
) {
    val L: Matrix get() = Matrix(MatrixR064.FACTORY.copy(lu.getL()))
    val U: Matrix get() = Matrix(MatrixR064.FACTORY.copy(lu.getU()))
}

/** Data class for QR decomposition. */
@ConsistentCopyVisibility
data class QRDecomposition internal constructor(
    private val qr: QR<Double>,
    private val mat: MatrixR064
) {
    val Q: Matrix get() = Matrix(MatrixR064.FACTORY.copy(qr.getQ()))
    val R: Matrix get() = Matrix(MatrixR064.FACTORY.copy(qr.getR()))
}