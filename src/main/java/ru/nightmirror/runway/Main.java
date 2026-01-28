package ru.nightmirror.runway;

public class Main {

    public static void main(String[] args) {
        Plane plane = new MiG29();

        NoWindEnvironment noWindEnvironment = new NoWindEnvironment();
        Ground dryGround = new DryConcreteGround();
        System.out.println(LandingRollCalculator.computeGroundRollInMeters(plane, dryGround, noWindEnvironment));
        Ground wetGround = new WetConcreteGround();
        System.out.println(LandingRollCalculator.computeGroundRollInMeters(plane, wetGround, noWindEnvironment));

        Environment windEnvironment = new WindEnvironment();
        System.out.println(LandingRollCalculator.computeGroundRollInMeters(plane, dryGround, windEnvironment));
        System.out.println(LandingRollCalculator.computeGroundRollInMeters(plane, wetGround, windEnvironment));
    }
}
