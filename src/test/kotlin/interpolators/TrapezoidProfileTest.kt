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
import dev.nextftc.control.interpolators.TrapezoidProfileConstraints
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

class TrapezoidProfileTest : FunSpec({
    val tolerance = 1e-6

    context("TrapezoidProfileConstraints") {
        test("should create valid constraints with positive values") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            constraints.maxVelocity shouldBe 5.0
            constraints.maxAcceleration shouldBe 2.0
        }

        test("should accept zero values") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 0.0, maxAcceleration = 0.0)
            constraints.maxVelocity shouldBe 0.0
            constraints.maxAcceleration shouldBe 0.0
        }

        test("should throw exception for negative maxVelocity") {
            shouldThrow<IllegalArgumentException> {
                TrapezoidProfileConstraints(maxVelocity = -1.0, maxAcceleration = 2.0)
            }
        }

        test("should throw exception for negative maxAcceleration") {
            shouldThrow<IllegalArgumentException> {
                TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = -2.0)
            }
        }
    }

    context("TrapezoidProfile basic functionality") {
        test("should start at initial state") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            val result = profile.calculate(0.seconds, initial, goal)

            result.position shouldBe (initial.position plusOrMinus tolerance)
            result.velocity shouldBe (initial.velocity plusOrMinus tolerance)
        }

        test("should reach goal state at end of profile") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime
            val result = profile.calculate(totalTime.seconds, initial, goal)

            result.position shouldBe (goal.position plusOrMinus tolerance)
            result.velocity shouldBe (goal.velocity plusOrMinus tolerance)
        }

        test("should respect maximum velocity constraint") {
            val maxVelocity = 5.0
            val constraints = TrapezoidProfileConstraints(maxVelocity = maxVelocity, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 50.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime

            // Sample the profile at multiple points
            for (i in 0..100) {
                val t = (totalTime * i / 100.0).seconds
                val state = profile.calculate(t, initial, goal)
                abs(state.velocity) shouldBeLessThanOrEqual (maxVelocity + tolerance)
            }
        }

        test("should respect maximum acceleration constraint") {
            val maxAcceleration = 2.0
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = maxAcceleration)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 50.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime

            // Sample the profile at multiple points
            for (i in 0..100) {
                val t = (totalTime * i / 100.0).seconds
                val state = profile.calculate(t, initial, goal)
                abs(state.acceleration) shouldBeLessThanOrEqual (maxAcceleration + tolerance)
            }
        }
    }

    context("TrapezoidProfile motion types") {
        test("should generate full trapezoidal profile for long distance") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 50.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime

            // Check that we reach max velocity somewhere in the middle
            var reachedMaxVelocity = false
            for (i in 0..100) {
                val t = (totalTime * i / 100.0).seconds
                val state = profile.calculate(t, initial, goal)
                if (abs(state.velocity - constraints.maxVelocity) < tolerance) {
                    reachedMaxVelocity = true
                    break
                }
            }
            reachedMaxVelocity shouldBe true
        }

        test("should generate triangular profile for short distance") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 10.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 5.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime

            // Check that we never reach max velocity
            var reachedMaxVelocity = false
            for (i in 0..100) {
                val t = (totalTime * i / 100.0).seconds
                val state = profile.calculate(t, initial, goal)
                if (abs(state.velocity - constraints.maxVelocity) < tolerance) {
                    reachedMaxVelocity = true
                    break
                }
            }
            reachedMaxVelocity shouldBe false
        }

        test("should handle backward motion") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 10.0, velocity = 0.0)
            val goal = KineticState(position = 0.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime
            val result = profile.calculate(totalTime.seconds, initial, goal)

            result.position shouldBe (goal.position plusOrMinus tolerance)
            result.velocity shouldBe (goal.velocity plusOrMinus tolerance)
        }
    }

    context("TrapezoidProfile with non-zero initial velocity") {
        test("should handle positive initial velocity in forward direction") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 2.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime
            val result = profile.calculate(totalTime.seconds, initial, goal)

            result.position shouldBe (goal.position plusOrMinus tolerance)
            result.velocity shouldBe (goal.velocity plusOrMinus tolerance)
        }

        test("should handle positive initial velocity in backward direction") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 10.0, velocity = 2.0)
            val goal = KineticState(position = 0.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime
            val result = profile.calculate(totalTime.seconds, initial, goal)

            result.position shouldBe (goal.position plusOrMinus tolerance)
        }
    }

    context("TrapezoidProfile with non-zero goal velocity") {
        test("should reach non-zero goal velocity") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 3.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime
            val result = profile.calculate(totalTime.seconds, initial, goal)

            result.position shouldBe (goal.position plusOrMinus tolerance)
            result.velocity shouldBe (goal.velocity plusOrMinus tolerance)
        }
    }

    context("TrapezoidProfile edge cases") {
        test("should handle zero distance movement") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 5.0, velocity = 0.0)
            val goal = KineticState(position = 5.0, velocity = 0.0)

            val result = profile.calculate(0.seconds, initial, goal)

            result.position shouldBe (initial.position plusOrMinus tolerance)
            result.velocity shouldBe (initial.velocity plusOrMinus tolerance)
        }

        test("should handle already at goal") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 10.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime

            totalTime shouldBe (0.0 plusOrMinus tolerance)
        }

        test("should clamp initial velocity exceeding max velocity") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 10.0)
            val goal = KineticState(position = 20.0, velocity = 0.0)

            val result = profile.calculate(0.seconds, initial, goal)

            // Should clamp to max velocity
            abs(result.velocity) shouldBeLessThanOrEqual (constraints.maxVelocity + tolerance)
        }
    }

    context("TrapezoidProfile isFinished") {
        test("should not be finished at start") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)

            profile.isFinished(0.0) shouldBe false
        }

        test("should be finished at total time") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime

            profile.isFinished(totalTime) shouldBe true
        }

        test("should be finished after total time") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime

            profile.isFinished(totalTime + 1.0) shouldBe true
        }
    }

    context("TrapezoidProfile timeLeftUntil") {
        test("should return zero time for current position") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val timeLeft = profile.timeLeftUntil(0.0)

            timeLeft shouldBe (0.0 plusOrMinus tolerance)
        }

        test("should return positive time for target ahead") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val timeLeft = profile.timeLeftUntil(5.0)

            timeLeft shouldBeGreaterThan 0.0
        }

        test("should return negative time for target behind") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 5.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val timeLeft = profile.timeLeftUntil(3.0)

            timeLeft shouldBeLessThan 0.0
        }
    }

    context("TrapezoidProfile continuity") {
        test("position should be continuous throughout profile") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 20.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime
            val dt = 0.01

            var previousState = profile.calculate(0.seconds, initial, goal)
            for (i in 1..((totalTime / dt).toInt())) {
                val t = (i * dt).seconds
                val currentState = profile.calculate(t, initial, goal)

                // Position should always increase (or stay same)
                currentState.position shouldBeGreaterThanOrEqual (previousState.position - tolerance)

                previousState = currentState
            }
        }

        test("velocity should be continuous throughout profile") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 20.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime = profile.totalTime
            val dt = 0.01

            var previousState = profile.calculate(0.seconds, initial, goal)
            for (i in 1..((totalTime / dt).toInt())) {
                val t = (i * dt).seconds
                val currentState = profile.calculate(t, initial, goal)

                // Velocity change should be bounded by acceleration * dt
                val velocityChange = abs(currentState.velocity - previousState.velocity)
                velocityChange shouldBeLessThanOrEqual (constraints.maxAcceleration * dt + tolerance)

                previousState = currentState
            }
        }
    }

    context("TrapezoidProfile totalTime property") {
        test("should have positive total time for non-zero movement") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)

            profile.totalTime shouldBeGreaterThan 0.0
        }

        test("should have consistent total time across multiple calls") {
            val constraints = TrapezoidProfileConstraints(maxVelocity = 5.0, maxAcceleration = 2.0)
            val profile = TrapezoidProfile(constraints)
            val initial = KineticState(position = 0.0, velocity = 0.0)
            val goal = KineticState(position = 10.0, velocity = 0.0)

            profile.calculate(0.seconds, initial, goal)
            val totalTime1 = profile.totalTime

            profile.calculate(1.seconds, initial, goal)
            val totalTime2 = profile.totalTime

            totalTime1 shouldBe (totalTime2 plusOrMinus tolerance)
        }
    }
})
