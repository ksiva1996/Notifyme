<?php 
include_once('connect.php');

$response = array();

if(isset($_POST['otp']) && isset($_POST['username']))
{
	$otp = $_POST['otp'];
	$email = $_POST['username'];
	
	$query = "SELECT * FROM temp_user WHERE email='$email'";
	$result = mysqli_query($con,$query);
	$data = mysqli_fetch_array($result);
	$main_otp = $data['otp'];
	$password = $data['password'];
	$number = $data['number'];
 
	if($main_otp == $otp)
	{
	$v = 1;
		$a = mysqli_query($con,"INSERT INTO users(email,number,password) VALUES('$email','$number','$password')");
		if($a)
		{
			$response['success'] = "1";
			$response['message'] = "Successfully Registered";

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
		$response['message'] = "wrong otp";

		echo json_encode($response);
	}

}