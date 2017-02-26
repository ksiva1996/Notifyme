<?php
include_once ('connect.php');

$response = array();

if(isset($_POST['username']) && isset($_POST['password']))
{
	$email = $_POST['username'];
	$password = $_POST['password'];
	$query = "SELECT password FROM users WHERE email='$email'";
	$result = mysqli_query($con,$query);
	$data = mysqli_fetch_array($result);
	$count = mysqli_num_rows($result);
	$check = $data['password'];
	
	if($count == 1 && $check == $password)
	{
		$response['success'] = "1";
		$response['message'] = "Login Successful"; 

		echo json_encode($response);
	}
	else
	{
		$response['success'] = "0";
		$response['message'] = "Wrong Username/Password"; 

		echo json_encode($response);
	}
}