package dev.nextftc.nextcontrol.builder

import dev.nextftc.nextcontrol.feedback.PIDCoefficients

fun main() {
    val basicPid = controlSystem {
        posPid(PIDCoefficients(1.0, 0.1, 0.01))
    }

    val angularPid = controlSystem {
        posFilter {
            lowPass(0.5)
            custom { ticks -> ticks / 400 } // ticks to radians
        }
        angular(AngleType.RADIANS) {
            posPid(PIDCoefficients(1.0, 0.1, 0.01))
        }
    }

    val velPid = controlSystem {
        velPid(PIDCoefficients(1.0, 0.1, 0.01))
    }
}