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

package dev.nextftc.nextcontrol

import dev.nextftc.control.KineticState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.any
import io.kotest.matchers.doubles.beNaN
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.property.assume
import io.kotest.property.checkAll

class KineticStateTest : FunSpec({

    context("KineticState constructor") {
        test("default values should be zeros") {
            val state = KineticState()
            state.position shouldBe 0.0
            state.velocity shouldBe 0.0
            state.acceleration shouldBe 0.0
        }

        test("constructor should set values correctly") {
            val state = KineticState(1.0, 2.0, 3.0)
            state.position shouldBe 1.0
            state.velocity shouldBe 2.0
            state.acceleration shouldBe 3.0
        }

        test("partial constructor should set provided values and default others") {
            val state = KineticState(position = 1.0)
            state.position shouldBe 1.0
            state.velocity shouldBe 0.0
            state.acceleration shouldBe 0.0

            val state2 = KineticState(position = 1.0, velocity = 2.0)
            state2.position shouldBe 1.0
            state2.velocity shouldBe 2.0
            state2.acceleration shouldBe 0.0
        }
    }

    context("KineticState operators") {
        test("plus operator should add components") {
            val state1 = KineticState(1.0, 2.0, 3.0)
            val state2 = KineticState(4.0, 5.0, 6.0)
            val result = state1 + state2

            result.position shouldBe 5.0
            result.velocity shouldBe 7.0
            result.acceleration shouldBe 9.0
        }

        test("minus operator should subtract components") {
            val state1 = KineticState(5.0, 7.0, 9.0)
            val state2 = KineticState(1.0, 2.0, 3.0)
            val result = state1 - state2

            result.position shouldBe 4.0
            result.velocity shouldBe 5.0
            result.acceleration shouldBe 6.0
        }

        test("times operator should multiply by scalar") {
            val state = KineticState(1.0, 2.0, 3.0)
            val scalar = 2.0
            val result = state * scalar

            result.position shouldBe 2.0
            result.velocity shouldBe 4.0
            result.acceleration shouldBe 6.0
        }
    }

    context("Property-based testing") {
        test("plus is commutative") {
            checkAll<KineticState, KineticState> { a, b ->
                val result1 = a + b
                val result2 = b + a

                result1.position shouldBe result2.position
                result1.velocity shouldBe result2.velocity
                result1.acceleration shouldBe result2.acceleration
            }
        }

        test("multiplying by 1 doesn't change the state") {
            checkAll<KineticState> { state ->
                val result = state * 1.0

                result.position shouldBe state.position
                result.velocity shouldBe state.velocity
                result.acceleration shouldBe state.acceleration
            }
        }

        test("multiplying by 0 results in all zeros") {
            checkAll<KineticState> { state ->
                assume {
                    state.position shouldNot beInfiniteOrNaN()
                    state.velocity shouldNot beInfiniteOrNaN()
                    state.acceleration shouldNot beInfiniteOrNaN()
                }
                val result = state * 0.0

                result.position shouldBe (0.0 plusOrMinus 1e-12)
                result.velocity shouldBe (0.0 plusOrMinus 1e-12)
                result.acceleration shouldBe (0.0 plusOrMinus 1e-12)
            }
        }
    }
})

fun beInfinite() = Matcher<Double> { value ->
    MatcherResult(
        value.isInfinite(),
        { "$value should be infinite" },
        { "$value should not be infinite" }
    )
}

fun beInfiniteOrNaN() = Matcher.any(beInfinite(), beNaN())