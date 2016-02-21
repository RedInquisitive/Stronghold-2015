package org.usfirst.frc.team3695.robot.subsystems;

import org.usfirst.frc.team3695.robot.Constants;
import org.usfirst.frc.team3695.robot.Controller;
import org.usfirst.frc.team3695.robot.Robot;
import org.usfirst.frc.team3695.robot.commands.CommandDrive;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This subsystem controls the driving motors and drive train variables. There are also
 * some unique sensors (like the accelerometer and stuff) that are included.
 */
public class SubsystemDrive extends Subsystem {
	private Talon frontLeft;
	private Talon frontRight;
	private Talon rearLeft;
	private Talon rearRight;
	
	private double[] x_g_buffer = new double[10];
	private double[] y_g_buffer = new double[x_g_buffer.length];
	private double[] z_g_buffer = new double[x_g_buffer.length];
	
	//TODO: Uncomment for encoders: private Encoder leftEncoder, rightEncoder;
	
	private RobotDrive driveTrain;
	
	private BuiltInAccelerometer builtInAccelerometer;
	
	private long timeStartRumble = 0;
	
	public SubsystemDrive() {
		super();
		
		frontLeft = new Talon(Constants.FRONT_LEFT_MOTOR_PORT);
		frontRight = new Talon(Constants.FRONT_RIGHT_MOTOR_PORT);
		rearLeft = new Talon(Constants.REAR_LEFT_MOTOR_PORT);
		rearRight = new Talon(Constants.REAR_RIGHT_MOTOR_PORT);
		driveTrain = new RobotDrive(frontLeft,rearLeft,frontRight,rearRight);
		
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, Constants.FRONT_LEFT_MOTOR_INVERT);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontRight, Constants.FRONT_RIGHT_MOTOR_INVERT);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearLeft, Constants.REAR_LEFT_MOTOR_INVERT);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearRight, Constants.REAR_RIGHT_MOTOR_INVERT);
		
		//TODO: Uncomment for encoders: leftEncoder = new Encoder(Constants.FRONT_LEFT_MOTOR_PORT, Constants.REAR_LEFT_MOTOR_PORT);
		//TODO: Uncomment for encoders: rightEncoder = new Encoder(Constants.FRONT_RIGHT_MOTOR_PORT, Constants.REAR_RIGHT_MOTOR_PORT);
		//TODO: Uncomment for encoders: leftEncoder.setDistancePerPulse(Constants.DISTANCE_PER_PULSE);
		//TODO: Uncomment for encoders: rightEncoder.setDistancePerPulse(Constants.DISTANCE_PER_PULSE);
		
		builtInAccelerometer = new BuiltInAccelerometer();
	}
	
    public void initDefaultCommand() {
		setDefaultCommand(new CommandDrive());
    }
    
	/**
	 * The log method puts interesting information to the SmartDashboard.
	 */
	public void log() {
		//TODO: Uncomment for encoders: SmartDashboard.putNumber("Left Distance", leftEncoder.getDistance());
		//TODO: Uncomment for encoders: SmartDashboard.putNumber("Right Distance", rightEncoder.getDistance());
		//TODO: Uncomment for encoders: SmartDashboard.putNumber("Left Speed", leftEncoder.getRate());
		//TODO: Uncomment for encoders: SmartDashboard.putNumber("Right Speed", rightEncoder.getRate());
		for(int i = 0; i < x_g_buffer.length - 1; i++) {
			x_g_buffer[i] = x_g_buffer[i + 1];
			y_g_buffer[i] = y_g_buffer[i + 1];
			z_g_buffer[i] = z_g_buffer[i + 1];
		}
		
		x_g_buffer[x_g_buffer.length - 1] = builtInAccelerometer.getX();
		y_g_buffer[y_g_buffer.length - 1] = builtInAccelerometer.getY();
		z_g_buffer[z_g_buffer.length - 1] = builtInAccelerometer.getZ();
		
		double x = average(x_g_buffer);
		double y = average(y_g_buffer);
		double z = average(z_g_buffer);
		
		SmartDashboard.putNumber("Speed X m.s:", Math.abs(x * 9.8));
		SmartDashboard.putNumber("Speed Y m.s:", Math.abs(y * 9.8));
		SmartDashboard.putNumber("Speed Z m.s:", Math.abs(z * 9.8));
		rumble(x, y, z);
	}
	
	private void rumble(double x, double y, double z) {
		double downGForce;
		switch(Constants.DOWN_AXIS) {
			case "X":
				downGForce = x * (Constants.DOWN_IS_NEGATIVE ? -1.0f : 1.0f);
				break;
			case "Y":
				downGForce = y * (Constants.DOWN_IS_NEGATIVE ? -1.0f : 1.0f);
				break;
			default:
				downGForce = z * (Constants.DOWN_IS_NEGATIVE ? -1.0f : 1.0f);
				break;
		}
		if(downGForce > 1.0f + Constants.RUMBLE_BOUND_G_FORCE || downGForce < 1.0f - Constants.RUMBLE_BOUND_G_FORCE) {
			timeStartRumble = System.currentTimeMillis();
		}
	}

	/**
	 * Arcade style driving for the DriveTrain.
	 * @param x Speed in range [-1,1]
	 * @param y Speed in range [-1,1]
	 */
	public void drive(double x, double y) {
		driveTrain.arcadeDrive(x, y);
	}
	
	/**
	 * @param joy This should move the robot and rumble the controller.
	 * Passing the joy to this method is simply for rumble.
	 */
	public void drive(Joystick joy) {
		drive(Controller.DRIVE_X_AXIS(),Controller.DRIVE_Y_AXIS());
		if(Robot.isRumbleEnabled()) {
			joy.setRumble(RumbleType.kLeftRumble, (System.currentTimeMillis() < timeStartRumble + Constants.RUMBLE_TIME_MS ? 1.0f : 0.0f));
			joy.setRumble(RumbleType.kRightRumble, (System.currentTimeMillis() < timeStartRumble + Constants.RUMBLE_TIME_MS ? 1.0f : 0.0f));
		}
	}
	
	/**
	 * Reset the robots sensors to the zero states.
	 */
	public void reset() {
		//Zero units (such as a gyro) here
		//TODO: Uncomment for encoders: leftEncoder.reset();
		//TODO: Uncomment for encoders: rightEncoder.reset();
	}
	
	/**
	 * @return The distance driven (average of left and right encoders).
	 */
	public double getDistance() {
		//TODO: Uncomment for encoders: return (leftEncoder.getDistance() + rightEncoder.getDistance())/2;
		return -1.0;
	}
	
	private double average(double[] list) {
		double sum = 0.0;
		for(double d : list) {
			sum += d;
		}
		return sum / (double)list.length;
	}
}

