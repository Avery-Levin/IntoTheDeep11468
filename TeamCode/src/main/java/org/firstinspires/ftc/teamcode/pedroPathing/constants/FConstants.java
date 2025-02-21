package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import com.pedropathing.localization.Localizers;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.util.CustomFilteredPIDFCoefficients;
import com.pedropathing.util.CustomPIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.baylorschool.intothedeep.Global;

public class FConstants {
    static {
        FollowerConstants.localizers = Localizers.PINPOINT;

        FollowerConstants.leftFrontMotorName = Global.flMotorName;
        FollowerConstants.leftRearMotorName = Global.blMotorName;
        FollowerConstants.rightFrontMotorName = Global.frMotorName;
        FollowerConstants.rightRearMotorName = Global.brMotorName;

        FollowerConstants.leftFrontMotorDirection = DcMotorSimple.Direction.REVERSE;//reversed
        FollowerConstants.leftRearMotorDirection = DcMotorSimple.Direction.REVERSE;//55.505
        FollowerConstants.rightFrontMotorDirection = DcMotorSimple.Direction.FORWARD;
        FollowerConstants.rightRearMotorDirection = DcMotorSimple.Direction.FORWARD;

        FollowerConstants.mass = Global.mass;

        FollowerConstants.xMovement = 55.50497018227666;
        FollowerConstants.yMovement = 44.34952889352113;

        FollowerConstants.forwardZeroPowerAcceleration = -43.09741075760215;
        FollowerConstants.lateralZeroPowerAcceleration = -73.89135519963428;

        FollowerConstants.translationalPIDFCoefficients.setCoefficients(0.14,0,0.01,0);
        FollowerConstants.useSecondaryTranslationalPID = false;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.1,0,0.01,0); // Not being used, @see useSecondaryTranslationalPID

        FollowerConstants.headingPIDFCoefficients.setCoefficients(2,0,0.1,0);
        FollowerConstants.useSecondaryHeadingPID = false;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(2,0,0.1,0); // Not being used, @see useSecondaryHeadingPID

        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.008, 0.0, 0.0011, 0.6, 0.0);
        FollowerConstants.useSecondaryDrivePID = false;
        FollowerConstants.secondaryDrivePIDFCoefficients = new CustomFilteredPIDFCoefficients(0.1,0,0,0.6,0); // Not being used, @see useSecondaryDrivePID

        FollowerConstants.zeroPowerAccelerationMultiplier = 5;
        FollowerConstants.centripetalScaling = 0.0005;

        FollowerConstants.pathEndTimeoutConstraint = 100;
        FollowerConstants.pathEndTValueConstraint = 0.995;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;

        FollowerConstants.useVoltageCompensationInAuto = true;
        FollowerConstants.useVoltageCompensationInTeleOp = false;
        FollowerConstants.nominalVoltage = 13.0;
        FollowerConstants.cacheInvalidateSeconds = 0.5;
    }
    public static void init() {/*load static block!*/}
}
