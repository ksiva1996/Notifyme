<?php
$con = mysqli_connect('localhost','mst_mnit','Maachanel@1','mstmnit');
if (!$con) {
    die('Could not connect: ' . mysqli_error($con));
}

mysqli_select_db($con,"mstmnit");

?>