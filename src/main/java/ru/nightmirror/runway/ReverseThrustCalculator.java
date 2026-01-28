package ru.nightmirror.runway;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReverseThrustCalculator {

    public static double computeReverseThrust(Plane plane) {
        double currentSpeed = plane.getSpeedInMeterPerSecond();
        double reverseCutoffSpeed = plane.getReverseCutoffSpeedInMeterPerSecond();
        double landingSpeed = plane.getLandingSpeedInMeterPerSecond();

        if (currentSpeed <= reverseCutoffSpeed) {
            return 0.0D;
        }

        double k_reverse = (currentSpeed - reverseCutoffSpeed) / (landingSpeed - reverseCutoffSpeed);
        return plane.getReverseMaxThrustInNewtons() * Math.max(0.0, k_reverse);
    }
}

