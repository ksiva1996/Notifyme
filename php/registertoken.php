<?php

include_once('connect.php');
$res = array();
if(isset($_POST['username']) && isset($_POST['token']))
{
	$email = $_POST['username'];
	$token = $_POST['token'];
	
	$query = "UPDATE users SET token='$token' WHERE email='$email'";
	$result = mysqli_query($con,$query);
}
