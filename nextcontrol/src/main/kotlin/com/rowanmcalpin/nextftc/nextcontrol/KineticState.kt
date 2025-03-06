package com.rowanmcalpin.nextftc.nextcontrol

sealed interface KineticType {
    val value: Double
}

@JvmInline value class Position(override val value: Double) : KineticType {
    override fun toString(): String = "Position $value"
    operator fun plus(other: Position): Position = Position(this.value + other.value)
    operator fun minus(other: Position): Position = Position(this.value - other.value)
    operator fun times(other: Position): Position = Position(this.value * other.value)
    operator fun div(other: Position): Position = Position(this.value / other.value)

    companion object {
        @JvmStatic val TYPE = Position(0.0)
    }
}

@JvmInline value class Velocity(override val value: Double) : KineticType {
    override fun toString(): String = "Velocity $value"
    operator fun plus(other: Velocity): Velocity = Velocity(this.value + other.value)
    operator fun minus(other: Velocity): Velocity = Velocity(this.value - other.value)
    operator fun times(other: Velocity): Velocity = Velocity(this.value * other.value)
    operator fun div(other: Velocity): Velocity = Velocity(this.value / other.value)

    companion object {
        @JvmStatic val TYPE = Velocity(0.0)
    }
}

@JvmInline value class Acceleration(override val value: Double) : KineticType {
    override fun toString(): String = "Acceleration $value"
    operator fun plus(other: Acceleration): Acceleration = Acceleration(this.value + other.value)
    operator fun minus(other: Acceleration): Acceleration = Acceleration(this.value - other.value)
    operator fun times(other: Acceleration): Acceleration = Acceleration(this.value * other.value)
    operator fun div(other: Acceleration): Acceleration = Acceleration(this.value / other.value)

    companion object {
        @JvmStatic val TYPE = Acceleration(0.0)
    }
}

data class KineticState @JvmOverloads constructor(
    val position: Position = Position(0.0),
    val velocity: Velocity = Velocity(0.0),
    val acceleration: Acceleration = Acceleration(0.0)
) {
    operator fun minus(other: KineticState): KineticState = KineticState(
            this.position - other.position,
            this.velocity - other.velocity,
            this.acceleration - other.acceleration,
    )

    companion object
}