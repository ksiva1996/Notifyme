<?php

include_once('connect.php');
$res = array();

if(isset($_POST['username']))
{
	$email = $_POST['username'];
	$query = "SELECT channel_id FROM users_channels WHERE email = '$email'";
	$result = mysqli_query($con,$query);
	while ($row = mysqli_fetch_array($result))
		{
		        
			$id = $row[0];
			$channel = "SELECT name,read_api FROM channels WHERE id = '$id'";
			$array = mysqli_query($con,$channel);
			while($r = mysqli_fetch_array($array))
			{
			   $name = $r[0];
			   $api = $r[1];
			}
			array_push($res, array('id' => $id, 'name' => $name, 'api' => $api));
		}

		echo json_encode(array("channels" => $res));
		
}

?>
