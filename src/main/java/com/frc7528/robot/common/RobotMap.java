package com.frc7528.robot.common;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class RobotMap {
    //Drivetrain
    public static WPI_VictorSPX m_leftAft = new WPI_VictorSPX(1);
    public static WPI_VictorSPX m_leftFront = new WPI_VictorSPX(2);
    public static WPI_VictorSPX m_rightAft = new WPI_VictorSPX(3);
    public static WPI_VictorSPX m_rightFront = new WPI_VictorSPX(4);
    public static DifferentialDrive m_drive;

    //Operator interface
    public static Joystick m_joy = new Joystick(0);
}
