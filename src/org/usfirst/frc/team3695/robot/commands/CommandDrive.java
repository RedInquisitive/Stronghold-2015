
package org.usfirst.frc.team3695.robot.commands;

import org.usfirst.frc.team3695.robot.Controller;
import org.usfirst.frc.team3695.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This command controls the drive subsystem with joysticks.
 */
public class CommandDrive extends Command {

    public CommandDrive() {
        requires(Robot.driveSubsystem);
    }

    protected void initialize() {
    }

    protected void execute() {
    	Robot.driveSubsystem.drive(Controller.DRIVE_JOY());
    }

    protected boolean isFinished() {
        return false; //Driving with the Joysticks is NEVER finished.
    }

    protected void end() {
    	Robot.driveSubsystem.drive(0, 0);
    }

    protected void interrupted() {
    	end();
    }
}
