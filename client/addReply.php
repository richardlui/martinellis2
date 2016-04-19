<?php
$userId = $_POST['userId'];
$topicId = $_POST['projectId'];
$comment = $_POST['comment'];

$fields = array(
	'userId' => $userId,
	'comment' => urlencode($comment)
);

$fields_string = "";
$url = 'http://localhost:9090/circle/topic/' . $topicId . '/reply';
foreach($fields as $key=>$value) { 
    $fields_string .= $key.'='.$value.'&'; 
}
rtrim($fields_string, '&');
 
$session = curl_init($url);

curl_setopt($session, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($session, CURLOPT_USERPWD, "acxiom:dmp_success");
curl_setopt($session, CURLOPT_POST, 1);
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);  
curl_setopt($session, CURLOPT_POSTFIELDS, $fields_string);  

$response = curl_exec($session);

curl_close($session);

$json = json_decode($response);

$reply = $json->result;

echo 'Successfully added reply id: ' . $reply->id . '<br/>';

?>

Click <a href="/home.php?userId=<?php echo $userId ?>">here</a> to go back to home page.

