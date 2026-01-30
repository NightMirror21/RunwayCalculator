package ru.nightmirror.runway;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BrakeChuteCalculator {

    public static double computeBrakeChuteForce(Plane plane, double airspeedInMeterPerSecond) {
        if (!plane.hasBrakeChute()) {
            return 0.0;
        }

        double V = airspeedInMeterPerSecond;

        // Сила парашюта
        double chuteDrag = 0.5 * Constants.AIR_DENSITY *
                plane.getBrakeChuteDragArea() * plane.getBrakeChuteCoefficient() *
                V * V;

        return chuteDrag;
    }
}
