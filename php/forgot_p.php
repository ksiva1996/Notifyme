<?php 
include_once('connect.php');

$response = array();

if(isset($_POST['otp']) && isset($_POST['password']) && isset($_POST['username']))
{
	$otp = $_POST['otp'];
	$password = $_POST['password'];
	$email = $_POST['username'];
	
	$query = "SELECT * FROM password WHERE email='$email'";
	$result = mysqli_query($con,$query);
	$data = mysqli_fetch_array($result);
	$main_otp = $data['otp'];
 
	if($main_otp == $otp)
	{
		$a = mysqli_query($con,"UPDATE users SET password='$password' WHERE email='$email'");
		if($a)
		{
			$response['success'] = "1";
			$response['message'] = "Password Changed";

			echo json_encode($response);
		}
		else
		{
			$response['success'] = "0";
			$response['message'] = "Something went Wrong";

			echo json_encode($response);
		}
	}
	else
	{
		$response['success'] = "-1";
		$response['message'] = "Wrong otp";

		echo json_encode($response);
	}

}