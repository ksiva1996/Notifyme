<?php

function send_notification ($token,$message)
	{
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			 'registration_ids' => $token,
			 'data' => $message
			);
		$headers = array(
			'Authorization : key = AIzaSyA5V3oK6SzLw3nAhWeipJGXC6MWePZgdMs',
			'Content-Type : application/json'
			);
	   $ch = curl_init();
       curl_setopt($ch, CURLOPT_URL, $url);
       curl_setopt($ch, CURLOPT_POST, true);
       curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
       curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
       curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);  
       curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
       curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
       $result = curl_exec($ch);           
       if ($result === FALSE) {
           die('Curl failed: ' . curl_error($ch));
       }
       curl_close($ch);
       return $result;
	}

include_once('connect.php');
$res = array();

	$api = 'VB4W4DQGAOQPRJVP';
	$message = "Your fields exceeded the preset value.";
	$tokens = array();
	$row = mysqli_query($con,"SELECT id,field1,field2 FROM channels");
	while(true)
	{
	while($channel = mysqli_fetch_array($row))
	{
	$id = $channel['0'];
	$api_url_one = 'https://api.thingspeak.com/channels/'.$id.'/fields/1/last.txt?api_key='.$api;
	$api_url_two = 'https://api.thingspeak.com/channels/'.$id.'/fields/2/last.txt?api_key='.$api;
		$response_one = file_get_contents($api_url_one);
 		$response_two = file_get_contents($api_url_two);
 		if($response_one > $channel['1'] || $response_two > $channel['2'])
 		{
 			$user = mysqli_query($con,"SELECT email from users_channels WHERE channel_id='$id'")
 			while($s = mysqli_fetch_array($user))
 			{
 				$tokens[] = $s['token'];
 			}
 			send_notification($tokens, $message);
 		}

 	}sleep(540);
 		
 	
 	}sleep(60);
