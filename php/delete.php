<?php 
session_start();
include_once('connect.php');

$response = array();

if(isset($_POST['username']) && isset($_POST['channelid']))
{
	$email = $_POST['username'];
	$channel_id = $_POST['channelid'];
	$a = mysqli_query($con,"DELETE FROM users_channels WHERE email='$email' AND channel_id='$channel_id'");
 	if($a)
 	{
 		$response['success'] = "1";
 		$response['message'] = "Channel deleted.";

 		echo json_encode($response);
 	}
 	else
 	{
 		$response['success'] = "0";
 		$response['message'] = "Something went wrong.";

 		echo json_encode($response);
 	}
}