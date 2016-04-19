<?php

$userId = $_POST['userId'];
$originatorId = $_POST['originatorId'];

$fields_string = "";
$url = 'http://localhost:9090/circle/user/' . $userId . '/friend/' . $originatorId;
 
$session = curl_init($url);

curl_setopt($session, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($session, CURLOPT_USERPWD, "acxiom:dmp_success");
curl_setopt($session, CURLOPT_POST, 1);
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);  

$response = curl_exec($session);

curl_close($session);

$json = json_decode($response);

$user = $json->result;

$redirecturl = "/friends.php?userId=" . $userId;
header("Location: " . $redirecturl);

?>

