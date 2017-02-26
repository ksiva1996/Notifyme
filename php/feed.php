<?php 

if(isset($_POST['feed']) && isset($_POST['channelid']) && isset($_POST['api_key']))
{
	$api = $_POST['api_key'];
	$feed = $_POST['feed'];
	$channel = $_POST['channelid'];
	$api_url = 'https://api.thingspeak.com/channels/'.$channel.'/fields/'.$feed.'.json?result=15&api_key='.$api;
 
 $response = file_get_contents( $api_url);
 
 echo $response;

}