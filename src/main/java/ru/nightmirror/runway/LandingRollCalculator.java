package ru.nightmirror.runway;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LandingRollCalculator {
    // Шаг интегрирования по времени (сек)
    private static final double TIME_STEP = 0.05D;

    // Минимальная скорость (ниже = остановка, м/с)
    private static final double MINIMUM_SPEED = 0.1D;

    // Скорость затухания бокового ветра (1/сек).
    // V_cross(t) = V0 * exp(-t * 2.0). За 1.5с боковик падает до 5%
    private static final double CROSSWIND_DECAY_RATE = 2.0D;

    // Максимальная доля нормальной реакции на боковое трение (50%)
    private static final double MAX_LATERAL_FRACTION = 0.5D;

    // Угол скольжения 15° в радианах (лимит для шасси)
    private static final double SLIP_ANGLE_LIMIT_RAD = 0.26D;

    // Доля бокового трения, помогающего продольному торможению (25%)
    private static final double SIDE_TO_LONG_DRAG_RATIO = 0.25D;

    private static final String LOG_FORMAT = "%-6s %-8s %-8s %-8s %-10s %-10s %-12s %-12s%n";

    public static double computeGroundRollInMeters(Plane plane, Ground ground, Environment environment) {
        return computeGroundRollInMeters(plane, ground, environment, false);
    }

    public static double computeGroundRollInMetersWithLog(Plane plane, Ground ground, Environment environment) {
        return computeGroundRollInMeters(plane, ground, environment, true);
    }

    private static double computeGroundRollInMeters(Plane plane, Ground ground, Environment environment, boolean logIterations) {
        plane.setSpeedInMeterPerSecond(plane.getLandingSpeedInMeterPerSecond());
        plane.setMileage(0.0D);

        // Время с момента касания ВПП (сек)
        double timeFromTouchdown = 0.0D;

        // Масса самолёта (кг)
        double aircraftMass = plane.getMassInKilograms();

        // Коэффициент трения покрытия ВПП
        double frictionCoefficient = ground.getMu();

        // Начальная боковая скорость от бокового ветра (м/с)
        double initialCrosswindSpeed = environment.getCrosswindSpeedInMeterPerSecond();

        int step = 0;
        if (logIterations) {
            System.out.printf(LOG_FORMAT,
                    "шаг",
                    "t, c",
                    "V, м/с",
                    "Vвозд",
                    "a, м/с^2",
                    "S, м",
                    "Тормоз, Н",
                    "Парашют, Н");
        }

        while (plane.getSpeedInMeterPerSecond() > MINIMUM_SPEED) {
            // Текущая продольная скорость (м/с)
            double longitudinalSpeed = plane.getSpeedInMeterPerSecond();

            // Текущая боковая скорость (затухает экспоненциально)
            double currentCrosswindSpeed = initialCrosswindSpeed * Math.exp(-timeFromTouchdown * CROSSWIND_DECAY_RATE);

            // Истинная продольная скорость относительно воздуха (встречный ветер задаётся со знаком "+")
            double airspeedLongitudinal = Math.max(0.0D, longitudinalSpeed + environment.getHeadwindSpeedInMeterPerSecond());

            // Полная воздушная скорость с учётом бокового компонента
            double airspeed = Math.hypot(airspeedLongitudinal, currentCrosswindSpeed);

            // ВРЕМЕННО меняем скорость для расчёта аэродинамики
            plane.setSpeedInMeterPerSecond(airspeed);
            double liftForce = LiftCalculator.computeLift(plane);
            double aerodynamicDrag = AerodynamicDragCalculator.computeDrag(plane);
            plane.setSpeedInMeterPerSecond(longitudinalSpeed);

            // Общая нормальная реакция шасси (Вес - Подъёмная сила)
            double totalNormalForce = Math.max(0.0D, plane.getMassInKilograms() * Constants.G - liftForce);

            // УГОЛ СКОЛЬЖЕНИЯ от бокового ветра
            double slipAngleRadians = Math.atan2(currentCrosswindSpeed, longitudinalSpeed);

            // Доля нагрузки на боковое трение (0..0.5)
            double lateralLoadFraction = Math.min(MAX_LATERAL_FRACTION, Math.abs(slipAngleRadians) / SLIP_ANGLE_LIMIT_RAD);

            // Нормальная реакция для продольного торможения
            double longitudinalNormalForce = totalNormalForce * (1.0D - lateralLoadFraction);

            // Нормальная реакция для бокового трения
            double lateralNormalForce = totalNormalForce * lateralLoadFraction;

            // Тормозная сила колёс (продольная, ослаблена боковиком)
            double wheelBrakingForce = frictionCoefficient * longitudinalNormalForce;

            // Боковое трение
            double lateralFrictionForce = frictionCoefficient * lateralNormalForce;

            // Дополнительное продольное торможение от бокового скольжения
            double additionalLongitudinalDrag = lateralFrictionForce * SIDE_TO_LONG_DRAG_RATIO;

            // Реверс тяги и парашют (независимы от боковика)
            double parachuteForce = BrakeChuteCalculator.computeBrakeChuteForce(plane, airspeed, timeFromTouchdown);

            // Общая продольная тормозная сила
            double totalLongitudinalBrakingForce = wheelBrakingForce + additionalLongitudinalDrag +
                    aerodynamicDrag + parachuteForce;

            // Продольное ускорение (замедление)
            double longitudinalAcceleration = -totalLongitudinalBrakingForce / aircraftMass;

            // Новая скорость
            double newLongitudinalSpeed = Math.max(0.0D, longitudinalSpeed + longitudinalAcceleration * TIME_STEP);

            // Средняя скорость на шаге (трапецеидальное правило)
            double averageLongitudinalSpeed = (longitudinalSpeed + newLongitudinalSpeed) / 2.0D;

            // Обновление пробега
            plane.setMileage(plane.getMileage() + averageLongitudinalSpeed * TIME_STEP);
            plane.setSpeedInMeterPerSecond(newLongitudinalSpeed);

            if (logIterations) {
                System.out.printf(LOG_FORMAT,
                        step,
                        String.format("%.2f", timeFromTouchdown),
                        String.format("%.2f", longitudinalSpeed),
                        String.format("%.2f", airspeed),
                        String.format("%.2f", longitudinalAcceleration),
                        String.format("%.2f", plane.getMileage()),
                        String.format("%.0f", wheelBrakingForce),
                        String.format("%.0f", parachuteForce));
            }

            // Следующий шаг времени
            timeFromTouchdown += TIME_STEP;
            step++;
        }

        return plane.getMileage();
    }
}