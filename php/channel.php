<?php 
include_once('connect.php');

$response = array();

if(isset($_POST['username']) && isset($_POST['channelid']))
{
	$email = $_POST['username'];
	$channel_id = $_POST['channelid'];
	$query = "SELECT * FROM channels where id='$channel_id'";
	$channel = mysqli_query($con,$query);
	$result = mysqli_fetch_array($channel);
	$count = mysqli_num_rows($channel);
	
	if($count == 1)
	{
	   	$a = mysqli_query($con,"INSERT INTO users_channels VALUES('$email','$channel_id')");
 		if($a)
 		{
	 		$response['success'] = "1";
	 		$response['message'] = "Channel added.";
	 		$response['channelname'] = $result['name'];
	 		$response['apikey'] = $result['read_api'];
	
	 		echo json_encode($response);
 		}
 		else
 		{
	 		$response['success'] = "0";
	 		$response['message'] = "Something went wrong.";
	
	 		echo json_encode($response);
	 	}
 	}
 	else
 	{
 	   $response['success'] = "-1";
 	   $response['message'] = "Channel doesn't exists.";
 	   
 	   echo json_encode($response);
 	}
}