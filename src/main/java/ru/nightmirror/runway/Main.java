package ru.nightmirror.runway;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));

        Plane plane = new MiG29();

        // NoWindEnvironment noWindEnvironment = new NoWindEnvironment();
        // Ground dryGround = new DryConcreteGround();
        // System.out.println(LandingRollCalculator.computeGroundRollInMetersWithLog(plane, dryGround, noWindEnvironment));
         Ground wetGround = new WetConcreteGround();
        // System.out.println(LandingRollCalculator.computeGroundRollInMeters(plane, wetGround, noWindEnvironment));

         Environment headwindEnvironment = new HeadwindEnvironment();
        // System.out.println(LandingRollCalculator.computeGroundRollInMeters(plane, dryGround, headwindEnvironment));
         System.out.println(LandingRollCalculator.computeGroundRollInMetersWithLog(plane, wetGround, headwindEnvironment));
    }
}