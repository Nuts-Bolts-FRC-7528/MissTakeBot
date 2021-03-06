package com.frc7528.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.frc7528.robot.common.RobotMap.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Robot extends TimedRobot {

    private SendableChooser<Double> fineControlSpeed = new SendableChooser<>();
    private SendableChooser<Double> deadBandOptions = new SendableChooser<>();
    private double fineControlSpeedDouble;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private NetworkTable limelight_table = NetworkTableInstance.getDefault().getTable("limelight");
    private NetworkTableEntry ledStatusEntry = Shuffleboard.getTab("DRIVETRAIN").add("LED Status", "OFF").getEntry();
    private NetworkTableEntry ll3dEntry = Shuffleboard.getTab("DRIVETRAIN").add("Limelight 3D stuff", new Number[0]).getEntry();
    private NetworkTableEntry inversionEntry = Shuffleboard.getTab("DEBUG").add("Inversion", false).getEntry();

    public static double d; // The distance to the target
    private static double a2; // The angle from the limelight
    private static final double a1 = 42; // The angle the limelight is mounted at
    private static final double h1 = 11.25; // The height the limelight is mounted at
    private static final double h2 = 98.25; // The height of the target

    private boolean isInverted = false;

    /**
     * Configure Victors, SendableChoosers, and initial debug statistics
     */
    @Override
    public void robotInit() {
        File file = new File(Robot.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        Shuffleboard.getTab("DEBUG").add("Left Aft Drivetrain Firm",m_leftAft.getFirmwareVersion());
        Shuffleboard.getTab("DEBUG").add("Left Front Drivetrain Firm",m_leftFront.getFirmwareVersion());
        Shuffleboard.getTab("DEBUG").add("Right Aft Drivetrain Firm",m_rightAft.getFirmwareVersion());
        Shuffleboard.getTab("DEBUG").add("Right Front Drivetrain Firm",m_rightFront.getFirmwareVersion());
        Shuffleboard.getTab("DEBUG").add("Last code deploy",sdf.format(file.lastModified()));

        //Format all motor controllers
        m_leftAft.configFactoryDefault();
        m_leftFront.configFactoryDefault();
        m_rightAft.configFactoryDefault();
        m_rightFront.configFactoryDefault();

        //Config followers
        m_leftAft.follow(m_leftFront);
        m_rightAft.follow(m_rightFront);

        //Config inversion
        m_leftFront.setInverted(false);
        m_rightFront.setInverted(false);

        //Instantiate DifferentialDrive and put it on Shuffleboard
        m_drive = new DifferentialDrive(m_leftFront,m_rightFront);
        Shuffleboard.getTab("DRIVETRAIN").add(m_drive);

        //Put Limelight LED Status to Shuffleboard
        ledStatusEntry.setString("OFF");

        //Fine Control Speed chooser
        fineControlSpeed.addOption("35% Speed", 0.35);
        fineControlSpeed.addOption("40% Speed", 0.40);
        fineControlSpeed.setDefaultOption("45% Speed", 0.45);
        fineControlSpeed.addOption("50% Speed", 0.50);
        fineControlSpeed.addOption("55% Speed", 0.55);
        fineControlSpeed.addOption("60% Speed", 0.60);
        Shuffleboard.getTab("SETUP").add("Fine Control Speed", fineControlSpeed);

        //Deadband chooser
        deadBandOptions.setDefaultOption("5%", 0.05);
        deadBandOptions.addOption("10%", 0.10);
        deadBandOptions.addOption("15%", 0.15);
        Shuffleboard.getTab("SETUP").add("Dead Band", deadBandOptions);

        //Transmits video through cameras
        CameraServer.getInstance().startAutomaticCapture();
    }

    @Override
    public void robotPeriodic() {
        ll3dEntry.setNumberArray(limelight_table.getEntry("camtran").getNumberArray(new Number[0]));
        inversionEntry.setBoolean(isInverted);

        a2 = limelight_table.getEntry("ty").getDouble(0); // Sets a2, the y position of the target
        d = Math.round((h2-h1) * 12 / Math.tan(Math.toRadians(a1+a2))); // Finds the distance
        SmartDashboard.putNumber("distance",d);
    }

    /**
     * Sets fine control speed and deadband
     */
    @Override
    public void teleopInit() {
        fineControlSpeedDouble = -fineControlSpeed.getSelected(); //Set fine control speed
        m_drive.setDeadband(deadBandOptions.getSelected()); //Set deadband
    }

    /**
     * Teleop driving (Fine control and joystick control)
     */
    @Override
    public void teleopPeriodic(){

        //Fine control
        if (m_joy.getPOV() == 0) { //Forward
            m_drive.arcadeDrive(-fineControlSpeedDouble,0);
        } else if (m_joy.getPOV() == 90) { //Right
            m_drive.arcadeDrive(0,-fineControlSpeedDouble);
        } else if (m_joy.getPOV() == 180) { //Backward
            m_drive.arcadeDrive(fineControlSpeedDouble,0);
        } else if (m_joy.getPOV() == 270) { //Left
            m_drive.arcadeDrive(0,fineControlSpeedDouble);
        } else {

            //Arcade Drive
            if (isInverted) {
                m_drive.arcadeDrive(m_joy.getY(), m_joy.getX());
            } else {
                m_drive.arcadeDrive(-m_joy.getY(), m_joy.getX());
            }
        }

        if (m_joy.getRawButton(2)) {
            isInverted = !isInverted;
        }
        //Limelight LED Control
        if(m_joy.getRawButtonPressed(5)) {
            limelight_table.getEntry("ledMode").setNumber(1); //Off
            ledStatusEntry.setString("OFF");
        }

        if(m_joy.getRawButtonPressed(6)) {
            limelight_table.getEntry("ledMode").setNumber(2); //Blink
            ledStatusEntry.setString("BLINK");
        }

        if(m_joy.getRawButtonPressed(3)) {
            limelight_table.getEntry("ledMode").setNumber(3); //On
            ledStatusEntry.setString("ON");
        }

        //Limelight Driver Cam Mode
        if(m_joy.getRawButtonPressed(12)) {
            limelight_table.getEntry("camMode").setNumber(0); //Vision Processor
        }

        if(m_joy.getRawButtonPressed(11)) {
            limelight_table.getEntry("camMode").setNumber(1); //Driver Cam
        }
    }

    @Override
    public void disabledInit() {
        limelight_table.getEntry("ledMode").setNumber(0); //Reset limelight LEDs after match end
//        ledStatusEntry.setString("OFF");
    }
}
