package dev.nextftc.nextcontrol.builder;

import dev.nextftc.nextcontrol.ControlSystem;
import dev.nextftc.nextcontrol.feedback.PIDCoefficients;

public class JavaControlSystemBuilderExample {
    public static void main(String[] args) {
        ControlSystem posPid = ControlSystem.builder()
                .posPid(new PIDCoefficients(1.0, 0.1, 0.01))
                .build();

        ControlSystem angularPid = ControlSystem.builder()
                .posFilter(filter -> {
                    filter.lowPass(0.5);
                    filter.custom(ticks -> (ticks / 400));
                    return filter;
                })
                .angular(
                        AngleType.RADIANS,
                        pid -> pid.posPid(new PIDCoefficients(1.0, 0.1, 0.01))
                )
                .build();
        ControlSystem velPid = ControlSystem.builder()
                .velPid(new PIDCoefficients(1.0, 0.1, 0.01))
                .build();
    }
}
