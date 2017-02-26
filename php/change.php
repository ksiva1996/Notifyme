<?php

include_once('connect.php');
$res = array();
if(isset($_POST['username']) && isset($_POST['password_new']) && isset($_POST['password']))
{
	$email = $_POST['username'];
	$password = $_POST['password_new'];
	$old = $_POST['password'];
	
	$pass = mysqli_query($con,"SELECT * FROM users WHERE email='$email'");
	$r = mysqli_fetch_array($pass);
	if($r['password'] == $old)
	{
	$query = "UPDATE users SET password='$password' WHERE email='$email'";
	$result = mysqli_query($con,$query);
	if($result)
	{
		$res['success'] = "1";
		$res['message'] = "Password Changed";
		
		echo json_encode($res);
	
	}
	else
	{
		$res['success'] = "-1";
		$res['message'] = "Something went wrong.";
		
		echo json_encode($res);
	
	}
	}
	else
	{
	$res['success'] = "0";
	$res['message'] = "Wrong Password";
	
	echo json_encode($res);
	
	}

}
