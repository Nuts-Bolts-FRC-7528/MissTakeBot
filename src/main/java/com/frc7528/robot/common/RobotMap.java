package com.frc7528.robot.common;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class RobotMap {
    //Drivetrain
    public static WPI_TalonFX m_leftAft = new WPI_TalonFX(1);
    public static WPI_TalonFX m_leftFront = new WPI_TalonFX(2);
    public static WPI_TalonFX m_rightAft = new WPI_TalonFX(3);
    public static WPI_TalonFX m_rightFront = new WPI_TalonFX(4);
    public static DifferentialDrive m_drive;

    //Operator interface
    public static Joystick m_joy = new Joystick(0);
}
