<?php
$userId = $_POST['userId'];
$keywords = $_POST['keywords'];

$url = 'http://localhost:9090/circle/user/' . $userId . '/friends/projects?keywords=';
$url = $url . urlencode($keywords);
 
$session = curl_init($url);

curl_setopt($session, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($session, CURLOPT_USERPWD, "acxiom:dmp_success");
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);  

$response = curl_exec($session);

curl_close($session);

$json = json_decode($response);
$project = $json->result;

$userprojects = $json->result;

foreach ($userprojects as $userproject) {
    echo '<li>';
    echo $userproject->user->firstName;
    echo ' recently ';
    echo $userproject->project->summary . ',&nbsp;<br/>';
    echo '</li>';
}

?>

Click <a href="/home.php?userId=<?php echo $userId ?>">here</a> to go back to home page.

