<?php
include_once 'connect.php';

$res = array();

function sendOtp($otp, $phone){

 $api_url = 'http://sms.dataoxytech.com/index.php/smsapi/httpapi/?uname=sylvester007&password=forskmnit&sender=FORSKT&receiver='.$phone.'&route=TA&msgtype=1&sms=Your%20OTP%20is%20'.$otp;
 
 $response = file_get_contents($api_url);
 
 return $response;
 }

$otp = rand(1000, 9999);

if(isset($_POST['username']) && isset($_POST['password']) && isset($_POST['number']))
{
	$email = $_POST['username'];
	$password = $_POST['password'];
	$number = $_POST['number'];
	
	$query = "SELECT email FROM users WHERE email='$email'";
	$result = mysqli_query($con,$query);
	$count = mysqli_num_rows($result);
	
	if($count == 0){
	
	
	       $q = "SELECT * FROM temp_user WHERE email='$email'";
		$r = mysqli_query($con,$q);
		$n = mysqli_num_rows($r);
		if($n==0)
	{
		$a = mysqli_query($con,"INSERT INTO temp_user(otp,email,number,password) VALUES('$otp','$email','$number','$password')");
		if($a)
		{
		
		$res['success'] = "1";
		$res['message'] = "OTP Generated";

		sendOtp($otp, $number);	

		echo json_encode($res);
		
		}
		else
		{
		$res['success'] = "0";
		$res['message'] = "Something Went wrong";
		
		 echo json_encode($res);
		}
	}
		
		else
		{
		$a = mysqli_query($con,"UPDATE temp_user SET otp='$otp' WHERE email='$email'");
		if($a)
		{
		
		$res['success'] = "1";
		$res['message'] = "OTP Generated";

		sendOtp($otp, $number);	

		echo json_encode($res);
		
		}
		else
		{
		$res['success'] = "0";
		$res['message'] = "Something Went wrong";
		
		 echo json_encode($res);
		}
	

		
			
	}
	}
	else
	{
			
		$res['success'] = "0";
		$res['message'] = "Email already exists!";
		echo json_encode($res);
	}
	

}
