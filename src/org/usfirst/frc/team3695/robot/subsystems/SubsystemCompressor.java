package org.usfirst.frc.team3695.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SubsystemCompressor extends Subsystem{
	Compressor comp = new Compressor();
	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub	
	}
	
	/**
	 * @return whether or not compressor is running
	 */
	public boolean isEnabled(){
		return comp.enabled();
	}
	
	/**
	 * sets compressor state
	 * @param state Desired compressor state
	 */
	public void setState(boolean state){
		if (state)
			comp.start();
		else
			comp.stop();
	}
	
	/**
	 * toggles compressor's current state
	 */
	public void toggle(){
		setState(!isEnabled());
	}
	
	public void log(){
		SmartDashboard.putBoolean("Compressor Status", isEnabled());
	}
}
