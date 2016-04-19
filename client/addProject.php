<?php
$userId = $_POST['userId'];
$categoryId = $_POST['categoryId'];
$summary = $_POST['summary'];

$fields = array(
	'userId' => $userId,
	'categoryId' => $categoryId,
	'summary' => urlencode($summary)
);

$fields_string = "";
$url = 'http://localhost:9090/circle/user/' . $userId . '/project';
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

$project = $json->result;

echo 'Successfully added ' . $project->name . ' project. <br/>';

?>

Click <a href="/home.php?userId=<?php echo $userId ?>">here</a> to go back to home page.

