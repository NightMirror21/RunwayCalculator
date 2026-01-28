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

    public static double computeGroundRollInMeters(Plane plane, Ground ground, Environment environment) {
        plane.setSpeedInMeterPerSecond(plane.getLandingSpeedInMeterPerSecond());
        plane.setMileage(0.0D);

        // Время с момента касания ВПП (сек)
        double timeFromTouchdown = 0.0D;

        // Масса самолёта (кг) = Вес / g
        double aircraftMass = plane.getWeight() / Constants.G;

        // Коэффициент трения покрытия ВПП
        double frictionCoefficient = ground.getMu();

        // Начальная боковая скорость от бокового ветра (м/с)
        double initialCrosswindSpeed = environment.getCrosswindSpeedInMeterPerSecond();

        while (plane.getSpeedInMeterPerSecond() > MINIMUM_SPEED) {
            // Текущая продольная скорость (м/с)
            double longitudinalSpeed = plane.getSpeedInMeterPerSecond();

            // Текущая боковая скорость (затухает экспоненциально)
            double currentCrosswindSpeed = initialCrosswindSpeed * Math.exp(-timeFromTouchdown * CROSSWIND_DECAY_RATE);

            // Истинная скорость относительно воздуха (учёт встречного ветра)
            double airspeedLongitudinal = Math.max(0.0D, longitudinalSpeed - environment.getHeadwindSpeedInMeterPerSecond());

            // ВРЕМЕННО меняем скорость для расчёта аэродинамики
            plane.setSpeedInMeterPerSecond(airspeedLongitudinal);
            double liftForce = LiftCalculator.computeLift(plane);
            double aerodynamicDrag = AerodynamicDragCalculator.computeDrag(plane);
            plane.setSpeedInMeterPerSecond(longitudinalSpeed);

            // Общая нормальная реакция шасси (Вес - Подъёмная сила)
            double totalNormalForce = Math.max(0.0D, plane.getWeight() - liftForce);

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
            double reverseThrustForce = ReverseThrustCalculator.computeReverseThrust(plane);
            double parachuteForce = BrakeChuteCalculator.computeBrakeChuteForce(plane, timeFromTouchdown);

            // Общая продольная тормозная сила
            double totalLongitudinalBrakingForce = wheelBrakingForce + additionalLongitudinalDrag +
                    aerodynamicDrag + reverseThrustForce + parachuteForce;

            // Продольное ускорение (замедление)
            double longitudinalAcceleration = -totalLongitudinalBrakingForce / aircraftMass;

            // Новая скорость (метод Эйлера с коррекцией)
            double newLongitudinalSpeed = Math.max(0.0D, longitudinalSpeed + longitudinalAcceleration * TIME_STEP);

            // Средняя скорость на шаге (трапецеидальное правило)
            double averageLongitudinalSpeed = (longitudinalSpeed + newLongitudinalSpeed) / 2.0D;

            // Обновление пробега
            plane.setMileage(plane.getMileage() + averageLongitudinalSpeed * TIME_STEP);
            plane.setSpeedInMeterPerSecond(newLongitudinalSpeed);

            // Следующий шаг времени
            timeFromTouchdown += TIME_STEP;
        }

        return plane.getMileage();
    }
}
