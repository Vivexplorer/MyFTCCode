package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp(group = "drive")

public class TeleOPTest extends OpMode {
    private PIDController controller;

    public static double p = 0.0144, i = 0, d=0.0032;
    public static double f = 0.3;

    public static int target = 0;

    public static double MOTOR_POWERS;

    private final double ticks_in_degrees = 22.4;

    private DcMotorEx lift_motor_left;
    private DcMotorEx lift_motor_right;

    private Servo left_intake;
    private Servo right_intake;

    private DcMotorEx intake;

    public static ElapsedTime elapsedTime;

    SampleMecanumDrive drive ;
    @Override
    public void init() {
        drive = new SampleMecanumDrive(hardwareMap);

        lift_motor_left = hardwareMap.get(DcMotorEx.class, "lift_left");
        lift_motor_right = hardwareMap.get(DcMotorEx.class, "lift_right");

        left_intake = hardwareMap.get(Servo.class, "left_intake");
        right_intake = hardwareMap.get(Servo.class, "right_intake");

        right_intake.setDirection(Servo.Direction.REVERSE);

        controller = new PIDController(p, i, d);
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());


        lift_motor_left.setDirection(DcMotorSimple.Direction.REVERSE);

        intake = hardwareMap.get(DcMotorEx.class, "intake");





    }
    @Override
    public void loop() {
        if (gamepad2.b) {
            MOTOR_POWERS = 0.7;
        }else {
            MOTOR_POWERS = 0.5;
        }
        controller.setPID(p, i, d);

        int armPos = lift_motor_left.getCurrentPosition();
        double pid = controller.calculate(armPos, target);
        double ff = Math.cos(Math.toRadians(target / ticks_in_degrees)) * f;

        double power = pid + ff;

        lift_motor_right.setPower(power);
        lift_motor_left.setPower(power);

        telemetry.addData("pos", armPos);
        telemetry.addData("target", target);
        telemetry.update();

        if (gamepad2.a) {
            lift_up();
        }else if (gamepad2.y) {
            lift_neutral();
        }

        if (gamepad2.x) {
            intake.setPower(-1.0);
        }else{
            intake.setPower(0);
        }

        if (gamepad2.dpad_left) {
            intake_up();
        }else if (gamepad2.dpad_right) {
            intake_down();
        }

        if (gamepad2.dpad_up) {
            target += (Math.floor(elapsedTime.seconds())  * 10);
        }else if(gamepad2.dpad_down) {
            target -= (Math.floor(elapsedTime.seconds())  * 10);//precision functions
        }
        elapsedTime.reset();




        if (gamepad1.left_stick_y>0) {
            drive.setMotorPowers(MOTOR_POWERS,MOTOR_POWERS,MOTOR_POWERS,MOTOR_POWERS);//forward
        }else if (gamepad1.left_stick_y<0) {
            drive.setMotorPowers(-MOTOR_POWERS,-MOTOR_POWERS,-MOTOR_POWERS,-MOTOR_POWERS);//backward
        }else if (gamepad1.left_stick_x>0) {
            drive.setMotorPowers(-MOTOR_POWERS, -MOTOR_POWERS, MOTOR_POWERS, MOTOR_POWERS);
        }else if(gamepad1.left_stick_x<0) {
            drive.setMotorPowers(MOTOR_POWERS, MOTOR_POWERS, -MOTOR_POWERS, -MOTOR_POWERS);
        }else if (gamepad1.right_stick_x>0) {
            drive.setMotorPowers(-MOTOR_POWERS,MOTOR_POWERS,-MOTOR_POWERS,MOTOR_POWERS);
        }else if (gamepad1.right_stick_x<0) {
            drive.setMotorPowers(MOTOR_POWERS,-MOTOR_POWERS,MOTOR_POWERS,-MOTOR_POWERS);
        }
        else {
            drive.setMotorPowers(0,0,0,0);
        }


    }
    public void lift_up() {
        target = 210;
    }
    public void lift_neutral() {
        target = 10;
    }

    public void intake_up() {
        left_intake.setPosition(0.6);
        right_intake.setPosition(0.6);
    }
    public void intake_down() {
        left_intake.setPosition(1);
        right_intake.setPosition(1);
    }
}
