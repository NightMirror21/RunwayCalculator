package ru.nightmirror.runway;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BrakeChuteCalculator {

    public static double computeBrakeChuteForce(Plane plane, double airspeedInMeterPerSecond, double timeFromTouchdown) {
        if (!plane.hasBrakeChute()) {
            return 0.0;
        }

        double V = airspeedInMeterPerSecond;
        double deploySpeed = plane.getBrakeChuteDeploymentSpeedInMeterPerSecond();

        // Парашют не работает, если скорость слишком низкая
        if (V < deploySpeed * 0.7) {
            return 0.0;
        }

        // Задержка раскрытия (2-3 сек на бросок и раскрытие)
        double delay = plane.getBrakeChuteDeploymentDelayInSeconds();
        if (timeFromTouchdown < delay) {
            return 0.0;
        }

        // Раскрытие постепенно за 1.5 сек (0-100%)
        double deploymentProgress = Math.min(1.0,
                (timeFromTouchdown - delay) / 1.5);

        // Сила парашюта
        double chuteDrag = 0.5 * Constants.AIR_DENSITY *
                plane.getBrakeChuteDragArea() * plane.getBrakeChuteCoefficient() *
                V * V * deploymentProgress;

        return chuteDrag;
    }
}
