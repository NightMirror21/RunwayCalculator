package ru.nightmirror.runway;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AerodynamicDragCalculator {

    public static double computeDrag(Plane plane) {
        return 0.5D * Constants.AIR_DENSITY * plane.getWingArea()
                * plane.getSpeedInMeterPerSecond() * plane.getSpeedInMeterPerSecond()
                * plane.getDragCoefficient();
    }
}
