<?php
include_once 'connect.php';

$res = array();

function sendOtp($otp, $phone){

 $api_url = 'http://sms.dataoxytech.com/index.php/smsapi/httpapi/?uname=sylvester007&password=forskmnit&sender=FORSKT&receiver='.$phone.'&route=TA&msgtype=1&sms=Your%20OTP%20is%20'.$otp;
 
 $response = file_get_contents($api_url);
 
 return $response;
 }

$otp = rand(1000, 9999);

if(isset($_POST['username']))
{
	$email = $_POST['username'];
	
	$query = "SELECT number FROM users WHERE email='$email'";
	$result = mysqli_query($con,$query);
	$count = mysqli_num_rows($result);
	$p = mysqli_fetch_array($result);
	$number = $p[0];
	
	if($count == 1)
	{       
		$q = "SELECT * FROM password WHERE email='$email'";
		$r = mysqli_query($con,$q);
		$n = mysqli_num_rows($r);
		if($n==0)
		{
		$a = mysqli_query($con,"INSERT INTO password(email,otp) VALUES('$email','$otp')");
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
		$a = mysqli_query($con,"UPDATE password SET otp='$otp' WHERE email='$email'");
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
	
}