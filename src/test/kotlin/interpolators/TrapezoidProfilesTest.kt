/*
 * NextFTC: a user-friendly control library for FIRST Tech Challenge
 * Copyright (C) 2025 Rowan McAlpin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nextftc.nextcontrol.interpolators

import dev.nextftc.control.KineticState
import dev.nextftc.control.interpolators.TrapezoidProfile
import dev.nextftc.control.interpolators.TrapezoidProfileParameters
import dev.nextftc.control.interpolators.TrapezoidInterpolator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.property.checkAll
import kotlin.time.TestTimeSource
import kotlin.time.Duration.Companion.seconds

class TrapezoidProfileTest : FunSpec({

    context("TrapezoidProfileParameters") {
        test("initialization with valid parameters") {
            // Arrange & Act
            val params = TrapezoidProfileParameters(10.0, 5.0, 5.0)

            // Assert
            params.maxVel shouldBe 10.0
            params.accel shouldBe 5.0
            params.decel shouldBe 5.0
        }

        test("initialization with default deceleration") {
            // Arrange & Act
            val params = TrapezoidProfileParameters(10.0, 5.0)

            // Assert
            params.maxVel shouldBe 10.0
            params.accel shouldBe 5.0
            params.decel shouldBe 5.0 // Default value should equal accel
        }

        test("property: deceleration defaults to acceleration") {
            checkAll<Double, Double> { maxVel, accel ->
                // Only test with positive values
                if (maxVel > 0 && accel > 0) {
                    val params = TrapezoidProfileParameters(maxVel, accel)
                    params.decel shouldBe accel
                }
            }
        }
    }

    context("TrapezoidProfile") {
        test("initialization sets default goal to zero") {
            // Arrange & Act
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val profile = TrapezoidProfile(params)

            // Assert
            profile.goal.position shouldBe 0.0
            profile.goal.velocity shouldBe 0.0
            profile.goal.acceleration shouldBe 0.0
        }

        test("calculates correct state during acceleration phase") {
            // Arrange
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val profile = TrapezoidProfile(params)
            profile.goal = KineticState(20.0)

            // Act
            val state = profile[0.5] // Half a second into acceleration phase

            // Assert
            state.position shouldBe 0.625 // x = 0 + 0*0.5 + 0.5*5*0.5^2 = 0.625
            state.velocity shouldBe 2.5 // v = 0 + 5*0.5 = 2.5
            state.acceleration shouldBe 5.0
        }

        test("calculates correct state during constant velocity phase") {
            // Arrange
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val profile = TrapezoidProfile(params)
            profile.goal = KineticState(40.0)

            // Act
            // First, calculate when acceleration phase ends (t = maxVel / accel = 10 / 5 = 2)
            // Then get state at t = 2.5 (0.5 seconds into constant velocity phase)
            val state = profile[2.5]

            // Assert
            // At t=2, position = 0.5*5*2^2 = 10, velocity = 10
            // At t=2.5, position = 10 + 10*0.5 = 15
            state.position shouldBe (15.0 plusOrMinus 0.15)
            state.velocity shouldBe 10.0
            state.acceleration shouldBe 0.0
        }

        test("calculates correct state during deceleration phase") {
            // Arrange
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val profile = TrapezoidProfile(params)
            profile.goal = KineticState(20.0)

            // Act
            // Calculate when deceleration phase starts
            // tAccel = maxVel / accel = 10 / 5 = 2
            // Position at end of acceleration: 0.5 * accel * tAccel^2 = 0.5 * 5 * 2^2 = 10
            // Distance for constant velocity: goal - accelDist - decelDist = 20 - 10 - 10 = 0
            // tConst = constDist / maxVel = 0 / 10 = 0
            // tDecelStart = tAccel + tConst = 2 + 0 = 2
            // Get state at t = 2.5 (0.5 seconds into deceleration phase)
            val state = profile[2.5]

            // Assert
            // At t=2, position = 10, velocity = 10
            // At t=2.5, position = 10 + 10*0.5 - 0.5*5*0.5^2 = 15 - 0.625 = 14.375
            // At t=2.5, velocity = 10 - 5*0.5 = 7.5
            state.position shouldBe (14.375 plusOrMinus 0.1)
            state.velocity shouldBe (7.5 plusOrMinus 0.1)
            state.acceleration shouldBe -5.0
        }

        test("returns initial state for negative time") {
            // Arrange
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val profile = TrapezoidProfile(params)
            profile.goal = KineticState(20.0)

            // Act
            val state = profile[-1.0]

            // Assert
            state.position shouldBe 0.0
            state.velocity shouldBe 0.0
            state.acceleration shouldBe 0.0
        }

        test("property: negative time always returns initial state") {
            checkAll<Double, Double, Double> { maxVel, accel, negTime ->
                // Only test with positive parameters and negative time
                if (maxVel > 0 && accel > 0 && negTime < 0) {
                    val params = TrapezoidProfileParameters(maxVel, accel)
                    val profile = TrapezoidProfile(params)
                    profile.goal = KineticState(20.0)

                    val state = profile[negTime]

                    state.position shouldBe 0.0
                    state.velocity shouldBe 0.0
                    state.acceleration shouldBe 0.0
                }
            }
        }

        test("property: acceleration is constant during acceleration phase") {
            checkAll<Double, Double, Double> { maxVel, accel, timeRatio ->
                // Only test with positive parameters and time ratio between 0 and 1
                if (maxVel > 0 && accel > 0 && timeRatio > 0 && timeRatio < 1) {
                    val params = TrapezoidProfileParameters(maxVel, accel)
                    val profile = TrapezoidProfile(params)
                    profile.goal = KineticState(100.0)

                    // Calculate a time that's within the acceleration phase
                    val accelTime = maxVel / accel
                    val t = accelTime * timeRatio

                    val state = profile[t]

                    state.acceleration shouldBe accel
                }
            }
        }
    }
})

class TrapezoidInterpolatorTest : FunSpec({

    context("TrapezoidInterpolator") {
        test("initialization with parameters") {
            // Arrange & Act
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val interpolator = TrapezoidInterpolator(params)

            // Assert
            interpolator.profile.params shouldBe params
            interpolator.goal.position shouldBe 0.0
            interpolator.goal.velocity shouldBe 0.0
            interpolator.goal.acceleration shouldBe 0.0
        }

        test("initialization with individual parameters") {
            // Arrange & Act
            val interpolator = TrapezoidInterpolator(10.0, 5.0, 4.0)

            // Assert
            interpolator.profile.params.maxVel shouldBe 10.0
            interpolator.profile.params.accel shouldBe 5.0
            interpolator.profile.params.decel shouldBe 4.0
        }

        test("goal setting updates profile goal") {
            // Arrange
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val interpolator = TrapezoidInterpolator(params)
            val goal = KineticState(20.0, 0.0, 0.0)

            // Act
            interpolator.goal = goal

            // Assert
            interpolator.goal shouldBe goal
            interpolator.profile.goal shouldBe goal
        }

        test("uses profile to calculate currentReference with TestTimeSource") {
            // Arrange
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val timeSource = TestTimeSource()
            val interpolator = TrapezoidInterpolator(params, timeSource)
            interpolator.goal = KineticState(30.0)

            // Act - at time 0
            val initialReference = interpolator.currentReference

            // Advance time by 0.5 seconds (during acceleration phase)
            timeSource += 0.5.seconds
            val accelerationReference = interpolator.currentReference

            // Advance time to 3 seconds (during constant velocity phase)
            // First, acceleration phase ends at t = maxVel / accel = 10 / 5 = 2
            timeSource += 2.5.seconds
            val constantVelocityReference = interpolator.currentReference

            // Advance time to 4.5 seconds (during deceleration phase)
            // Calculate when deceleration phase starts
            // tAccel = maxVel / accel = 10 / 5 = 2
            // Position at end of acceleration: 0.5 * accel * tAccel^2 = 0.5 * 5 * 2^2 = 10
            // Distance for constant velocity: goal - accelDist - decelDist = 20 - 10 - 10 = 0
            // tConst = constDist / maxVel = 0 / 10 = 0
            // tDecelStart = tAccel + tConst = 2 + 0 = 2
            // Get state at t = 2.5 (0.5 seconds into deceleration phase)
            timeSource += 1.5.seconds
            val decelerationReference = interpolator.currentReference

            // Assert
            // Initial state should be zeros
            initialReference.position shouldBe 0.0
            initialReference.velocity shouldBe 0.0
            initialReference.acceleration shouldBe 0.0

            // During acceleration phase
            accelerationReference.position shouldBe 0.625 // x = 0 + 0*0.5 + 0.5*5*0.5^2 = 0.625
            accelerationReference.velocity shouldBe 2.5 // v = 0 + 5*0.5 = 2.5
            accelerationReference.acceleration shouldBe 5.0

            // During constant velocity phase
            // At t=2, position = 0.5*5*2^2 = 10, velocity = 10
            // At t=3, position = 10 + 10*1 = 20
            constantVelocityReference.position shouldBe (20.0 plusOrMinus 0.15)
            constantVelocityReference.velocity shouldBe 10.0
            constantVelocityReference.acceleration shouldBe 0.0

            // During deceleration phase
            // At t=3, position = 20, velocity = 10
            // At t=4.5, position = 20 - 0.5*5*1.5^2 + 1.5*10 = 20 - 0.625 + 15 = 29.375
            // At t=4.5, velocity = 10 - 5*1.5 = 2.5
            decelerationReference.position shouldBe (29.375 plusOrMinus 0.1)
            decelerationReference.velocity shouldBe (2.5 plusOrMinus 0.1)
            decelerationReference.acceleration shouldBe -5.0
        }

        test("property: goal is correctly propagated to profile") {
            checkAll<Double, Double, Double> { maxVel, accel, position ->
                // Only test with positive parameters
                if (maxVel > 0 && accel > 0) {
                    val params = TrapezoidProfileParameters(maxVel, accel)
                    val interpolator = TrapezoidInterpolator(params)
                    val goal = KineticState(position)

                    interpolator.goal = goal

                    interpolator.goal shouldBe goal
                    interpolator.profile.goal shouldBe goal
                }
            }
        }

        test("handles goal changes during execution") {
            // Arrange
            val params = TrapezoidProfileParameters(10.0, 5.0)
            val timeSource = TestTimeSource()
            val interpolator = TrapezoidInterpolator(params, timeSource)

            // Set initial goal and advance time
            interpolator.goal = KineticState(20.0)
            timeSource += 1.0.seconds
            val referenceBeforeChange = interpolator.currentReference

            // Change goal and check that the reference changes appropriately
            interpolator.goal = KineticState(30.0)

            // The reference immediately after changing the goal should still be based on the old goal
            // and the elapsed time
            val referenceAfterChange = interpolator.currentReference
            referenceAfterChange shouldBe referenceBeforeChange

            // Advance time and check that the reference is now based on the new goal
            timeSource += 1.0.seconds
            val referenceAfterTimeAdvance = interpolator.currentReference

            // The reference should now be different from the reference before the goal change
            // We can't easily predict the exact value, but we can check that it's moving toward the new goal
            (referenceAfterTimeAdvance.position > referenceBeforeChange.position) shouldBe true
        }
    }
})
