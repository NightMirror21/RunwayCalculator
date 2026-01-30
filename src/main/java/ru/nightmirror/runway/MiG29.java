package ru.nightmirror.runway;

public class MiG29 implements Plane {

    private double speed;
    private double mileage = 0.0D;

    @Override
    public double getLandingSpeedInMeterPerSecond() {
        return 72D;
    }

    @Override
    public double getMassInKilograms() {
        return 12900D;
    }

    @Override
    public double getWingArea() {
        return 38.06D;
    }

    @Override
    public double getLiftCoefficient() {
        return 0.5D;
    }

    @Override
    public double getDragCoefficient() {
        return 0.05D;
    }

    @Override
    public double getSpeedInMeterPerSecond() {
        return speed;
    }

    @Override
    public void setSpeedInMeterPerSecond(double speed) {
        this.speed = speed;
    }

    @Override
    public double getMileage() {
        return mileage;
    }

    @Override
    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    @Override
    public boolean hasBrakeChute() {
        return true;
    }

    @Override
    public double getBrakeChuteDeploymentSpeedInMeterPerSecond() {
        return 55.0D;
    }

    @Override
    public double getBrakeChuteDragArea() {
        return 18.0D;
    }

    @Override
    public double getBrakeChuteCoefficient() {
        return 0.52D;
    }

    @Override
    public double getBrakeChuteDeploymentDelayInSeconds() {
        return 1.5D;
    }
}

