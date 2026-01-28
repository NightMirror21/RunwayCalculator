package ru.nightmirror.runway;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LiftCalculator {

    public static double computeLift(Plane plane) {
        return 0.5D * Constants.AIR_DENSITY *
                plane.getWingArea() *
                plane.getSpeedInMeterPerSecond() * plane.getSpeedInMeterPerSecond()
                * plane.getLiftCoefficient();
    }
}
