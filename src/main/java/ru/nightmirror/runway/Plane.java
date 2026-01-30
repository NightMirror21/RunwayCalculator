package ru.nightmirror.runway;

public interface Plane {
    double getLandingSpeedInMeterPerSecond();

    void setSpeedInMeterPerSecond(double speed);

    double getSpeedInMeterPerSecond();

    double getMileage();

    void setMileage(double mileage);

    double getLiftCoefficient();

    double getDragCoefficient();

    double getMassInKilograms();

    double getWingArea();

    boolean hasBrakeChute();

    double getBrakeChuteDeploymentSpeedInMeterPerSecond();

    double getBrakeChuteDragArea();

    double getBrakeChuteCoefficient();

    double getBrakeChuteDeploymentDelayInSeconds();
}
