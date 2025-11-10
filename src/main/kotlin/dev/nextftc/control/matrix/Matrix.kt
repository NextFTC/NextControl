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
import org.ojalgo.type.context.NumberContext


/**
 * Represents a matrix of doubles.
 * Internally represented as a MatrixR064 from OjAlgo.
 */
class Matrix internal constructor(internal val store: MatrixR064) {
    /**
     * Constructor to create a [Matrix] from a 2D array.
     */
    @Suppress("DEPRECATION")
    constructor(data: Array<DoubleArray>) : this(MatrixR064.FACTORY.rows(data))

    /**
     * Constructor to create a [Matrix] from a list of lists.
     */
    constructor(data: Collection<Collection<Double>>) : this(data.map { it.toDoubleArray() }.toTypedArray())

    companion object {
        /**
         * Creates a zero matrix with the given dimensions.
         */
        @JvmStatic
        fun zero(rows: Int, cols: Int) =
                Matrix(MatrixR064.FACTORY.make(rows, cols))

        /**
         * Creates a zero matrix with dimensions [size] by [size].
         */
        @JvmStatic
        fun zero(size: Int) = zero(size, size)

        /**
         * Creates an identity matrix with dimensions [size] by [size].
         */
        @JvmStatic
        fun identity(size: Int) = Matrix(MatrixR064.FACTORY.makeEye(size, size))

        /**
         * Creates a matrix with [data] along the diagonal
         * and all other elements set to 0.
         */
        @JvmStatic
        fun diagonal(vararg data: Double) = Matrix(MatrixR064.FACTORY.makeDiagonal(*data))

        @JvmStatic
        fun diagonal(data: Collection<Double>) = diagonal(*data.toDoubleArray())

        /**
         * Creates a 1 by n matrix with [data] as its elements,
         * where n is [data].size.
         */
        @JvmStatic
        fun row(vararg data: Double) = Matrix(MatrixR064.FACTORY.row(data))

        /**
         * Creates a 1 by n matrix with [data] as its elements,
         * where n is [data].size.
         */
        @JvmStatic
        fun row(data: Collection<Double>) = row(*data.toDoubleArray())

        /**
         * Creates an n by 1 matrix with [data] as its elements,
         * where n is [data].size.
         */
        @JvmStatic
        fun column(vararg data: Double) = Matrix(MatrixR064.FACTORY.column(data))

        /**
         * Creates an n by 1 matrix with [data] as its elements,
         * where n is [data].size.
         */
        @JvmStatic
        fun column(data: Collection<Double>) = column(*data.toDoubleArray())
    }

    /**
     * The number of columns in the matrix.
     */
    @JvmField val numColumns = store.colDim

    /**
     * The number of rows in the matrix.
     */
    @JvmField val numRows = store.rowDim

    /**
     * The size of the matrix.
     *
     * @return a pair of integers representing the number of rows and columns respectively.
     */
    @JvmField val size = numRows to numColumns

    /**
     * The transpose of this matrix.
     */
    @get:JvmName("transpose")
    val transpose: Matrix
        get() = Matrix(store.transpose())

    /**
     * Returns a copy of this matrix.
     */
    fun copy() = Matrix(MatrixR064.FACTORY.copy(store))

    /**
     * The best possible inverse of this matrix.
     */
    @get:JvmName("inverse")
    val inverse: Matrix
        get() = Matrix(store.invert())

    /**
     * Returns the Frobenius norm of this matrix.
     * The Frobenius norm is the square root of the sum of squares of all elements.
     */
    @get:JvmName("norm")
    val norm: Double
        get() = store.norm()

    /**
     * Returns the matrix with all elements negated.
     * This is equivalent to multiplying the matrix by -1.
     */
    operator fun unaryMinus() = Matrix(store.multiply(-1.0))

    /**
     * Adds another matrix to this matrix.
     * The matrices must have the same dimensions.
     */
    operator fun plus(other: Matrix) = Matrix(this.store.add(other.store))

    /**
     * Subtracts another matrix from this matrix.
     * The matrices must have the same dimensions.
     */
    operator fun minus(other: Matrix) = Matrix(this.store.subtract(other.store))

    /**
     * Multiplies this matrix by another matrix.
     * The number of columns in this matrix must match the number of rows in the other matrix.
     */
    operator fun times(other: Matrix) = Matrix(this.store.multiply(other.store))

    /**
     * Multiplies this matrix by a scalar.
     */
    operator fun times(scalar: Double) = Matrix(store.multiply(scalar))

    /**
     * @usesMathJax
     *
     * Solves for X in the equation \(AX = B)\,
     * where A is this matrix and B is other.
     */
    fun solve(other: Matrix): Matrix = Matrix(this.store.solve(other.store))

    /**
     * Returns the element at the given indices.
     */
    operator fun get(i: Int, j: Int): Double = store[i.toLong(), j.toLong()]

    /**
     * Returns the [n]th row of the matrix.
     */
    fun row(n: Int) = Matrix(store.row(n))

    /**
     * Returns the [n]th column of the matrix.
     */
    fun column(n: Int) = Matrix(store.column(n))

    /**
     * Returns the diagonal elements of this matrix.
     */
    fun diagonals() = Matrix(store.diagonal())

    /**
     * Returns a submatrix of this matrix.
     * @param startRow First row to include in the submatrix, inclusive.
     * @param endRow Last row to include in the submatrix, exclusive.
     * @param startCol First column to include in the submatrix, inclusive.
     * @param endCol Last column to include in the submatrix, exclusive.
     */
    fun slice(startRow: Int, endRow: Int, startCol: Int, endCol: Int) = Matrix(
        store.select((startRow..<endRow).toList().toIntArray(), (startCol..<endCol).toList().toIntArray())
    )

    /**
     * Returns the LLT (Cholesky) decomposition of this matrix.
     * Only works for symmetric, positive-definite matrices.
     * Provides in-place rank-1 update/downdate methods.
     */
    fun llt(): LLTDecomposition {
        val chol = Cholesky.R064.make()
        val mat = MatrixR064.FACTORY.copy(store)
        require(chol.decompose(mat)) { "Matrix is not symmetric positive-definite" }
        return LLTDecomposition(chol, mat)
    }

    /**
     * Returns the LDLT decomposition of this matrix.
     * Only works for symmetric matrices.
     */
    fun ldlt(): LDLTDecomposition {
        val ldlt = Cholesky.R064.make()
        val mat = MatrixR064.FACTORY.copy(store)
        require(ldlt.decompose(mat)) { "Matrix is not positive definite" }
        return LDLTDecomposition(ldlt, mat)
    }

    /**
     * Returns the LU decomposition of this matrix.
     */
    fun lu(): LUDecomposition {
        val lu = LU.R064.make()
        val mat = MatrixR064.FACTORY.copy(store)
        require(lu.decompose(mat)) { "Matrix is singular or not square" }
        return LUDecomposition(lu, mat)
    }

    /**
     * Returns the QR decomposition of this matrix.
     */
    fun qr(): QRDecomposition {
        val qr = QR.R064.make()
        val mat = MatrixR064.FACTORY.copy(store)
        require(qr.decompose(mat)) { "QR decomposition failed" }
        return QRDecomposition(qr, mat)
    }

    override fun toString(): String = store.toRawCopy2D().contentDeepToString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Matrix && this.store.equals(other.store, NumberContext.of(6))
    }

    override fun hashCode(): Int {
        return store.hashCode()
    }
}