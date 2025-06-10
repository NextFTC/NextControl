package dev.nextftc.control.interpolators

import dev.nextftc.control.KineticState
import kotlin.math.pow
import kotlin.math.sign
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

/**
 * Parameters for a trapezoid motion profile.
 * @param maxVel maximum velocity for the profile
 * @param accel constant acceleration for the acceleration phase
 * @param decel constant deceleration for the deceleration phase
 */
data class TrapezoidProfileParameters @JvmOverloads constructor(
    var maxVel: Double,
    var accel: Double,
    var decel: Double = accel
) {
    init {
        require(maxVel.sign == accel.sign && accel.sign == decel.sign)
    }
}

/**
 * Implements a trapezoid motion profile for smooth motion planning.
 *
 * A trapezoid profile creates a motion curve with three phases:
 * 1. Acceleration phase - constant acceleration to reach maximum velocity
 * 2. Constant velocity phase - maintaining maximum velocity
 * 3. Deceleration phase - constant deceleration to reach the goal
 */
class TrapezoidProfile(val params: TrapezoidProfileParameters) {
    /**
     * Represents a segment of the trapezoid profile with constant acceleration.
     *
     * Each segment is defined by its initial position, initial velocity, and constant acceleration.
     * The segment can calculate the kinetic state at any point in time.
     */
    internal class Segment(val x0: () -> Double, val v0: () -> Double, val a: () -> Double) {
        /**
         * Calculates the kinetic state at the specified time.
         */
        operator fun get(t: Double) = KineticState(
            x0() + v0() * t + a() * t.pow(2) / 2,
            v0() + a() * t,
            a()
        )

        /**
         * Calculates the time needed to reach the specified velocity.
         */
        fun inverseByVel(vel: () -> Double) = (vel() - v0()) / a()
    }

    /** The target kinetic state (position, velocity, acceleration) */
    var goal: KineticState = KineticState()

    private val tAccel get() = params.maxVel / params.accel

    private val tDecel get() = params.maxVel / params.decel

    private val constDist get() =
        goal.position - (0.5 * params.accel * tAccel.pow(2) + 0.5 * params.decel * tDecel.pow(2))

    private val tConst get() = constDist / params.maxVel

    private val tDecelStart get() = tAccel + tConst

    private val accelSegment = Segment({ 0.0 }, { 0.0 }, params::accel)

    private val constSegment =
        Segment({ accelSegment[accelSegment.inverseByVel(params::maxVel)].position }, params::maxVel) { 0.0 }

    private val decelSegment = Segment(
        { accelSegment[tAccel].position + params.maxVel * tConst },
        params::maxVel,
        { -params.decel }
    )

    /**
     * Calculates the kinetic state at the specified time.
     */
    operator fun get(t: Double) = when {
        t <= 0 -> KineticState()
        t <= tAccel -> accelSegment[t]
        t <= tDecelStart -> constSegment[t-tAccel]
        else -> decelSegment[t-tDecelStart]
    }
}

/**
 * Implements an interpolator using a trapezoid motion profile.
 *
 * This class provides time-based interpolation using a trapezoid profile, which creates
 * smooth motion with controlled acceleration and velocity. It implements the InterpolatorElement
 * interface for integration with control systems.
 */
class TrapezoidInterpolator(
    params: TrapezoidProfileParameters,
    val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic
) : InterpolatorElement {
    /**
     * Convenience constructor that creates a TrapezoidInterpolator with individual motion parameters.
     */
    @JvmOverloads constructor(
        maxVel: Double,
        accel: Double,
        decel: Double = accel,
        timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic
    ) : this(
        TrapezoidProfileParameters(maxVel, accel, decel),
        timeSource
    )

    val profile = TrapezoidProfile(params)

    val tStart by lazy { timeSource.markNow() }

    /** The target kinetic state to reach */
    override var goal: KineticState by profile::goal

    /**
     * The current reference state based on elapsed time since start.
     *
     * This property calculates the appropriate kinetic state (position, velocity, acceleration)
     * based on the time elapsed since the interpolator was started.
     */
    override val currentReference: KineticState
        get() = profile[tStart.elapsedNow().toDouble(DurationUnit.SECONDS)]
}
