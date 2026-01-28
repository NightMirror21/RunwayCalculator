package ru.nightmirror.runway;

public interface Plane {
    double getLandingSpeedInMeterPerSecond();

    void setSpeedInMeterPerSecond(double speed);

    double getSpeedInMeterPerSecond();

    double getMileage();

    void setMileage(double mileage);

    double getLiftCoefficient();

    double getDragCoefficient();

    double getWeight();

    double getWingArea();

    double getReverseMaxThrustInNewtons();

    double getReverseCutoffSpeedInMeterPerSecond();

    boolean hasBrakeChute();

    double getBrakeChuteDeploymentSpeedInMeterPerSecond();

    double getBrakeChuteDragArea();

    double getBrakeChuteDeploymentDelayInSeconds();
}
