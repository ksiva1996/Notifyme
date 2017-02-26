<?php 


if(isset($_POST['api_key']) && isset($_POST['channelid']))
{
	$api = $_POST['api_key'];
	$channel = $_POST['channelid'];
	$api_url = 'https://api.thingspeak.com/channels/'.$channel.'/feeds.json?results=0&api_key='.$api;
 
 $response = file_get_contents( $api_url);
 
 echo $response;

}