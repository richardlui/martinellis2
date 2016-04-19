<?php
  $firstName = $_POST['firstName'];
  $lastName = $_POST['lastName'];
  $email = $_POST['email'];
  $fields = array(
      'firstName' => $firstName,
      'lastName' => $lastName,
      'email' => urlencode($email)
  );

  $fields_string = "";
  $url = 'http://localhost:9090/circle/user/' . $userId;
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

  $user = $json->result;
  $redirecturl = "home.php?userId=" . $user->userId;
  header("Location: " . $redirecturl);
?>
