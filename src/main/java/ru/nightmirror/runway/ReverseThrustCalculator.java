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

        if (currentSpeed >= landingSpeed) {
            return plane.getReverseMaxThrustInNewtons();
        }

        double kReverse = (currentSpeed - reverseCutoffSpeed) / (landingSpeed - reverseCutoffSpeed);
        return plane.getReverseMaxThrustInNewtons() * Math.min(1.0D, Math.max(0.0D, kReverse));
    }
}
