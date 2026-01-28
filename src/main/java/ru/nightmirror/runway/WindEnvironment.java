package ru.nightmirror.runway;

public class WindEnvironment implements Environment {
    @Override
    public double getHeadwindSpeedInMeterPerSecond() {
        return 10.0D;
    }

    @Override
    public double getCrosswindSpeedInMeterPerSecond() {
        return 7.0D;
    }
}
