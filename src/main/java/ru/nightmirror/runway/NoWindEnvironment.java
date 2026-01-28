package ru.nightmirror.runway;

public class NoWindEnvironment implements Environment {
    @Override
    public double getHeadwindSpeedInMeterPerSecond() {
        return 0.0D;
    }

    @Override
    public double getCrosswindSpeedInMeterPerSecond() {
        return 0.0D;
    }
}
