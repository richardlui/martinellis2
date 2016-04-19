<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<title>LinkHelp</title>
 
<link rel="stylesheet" href="css/main.css" type="text/css" />
 
<!--[if IE]>
	<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
<!--[if lte IE 7]>
	<script src="js/IE8.js" type="text/javascript"></script><![endif]-->
 
<!--[if lt IE 7]>
	<link rel="stylesheet" type="text/css" media="all" href="css/ie6.css"/><![endif]-->
</head>
 
<?php

$userId = $_GET['userId'];

if ($userId == "") {
    $userId = 100;
}

$url = 'http://localhost:9090/circle/user/' . $userId;

$session = curl_init($url);

curl_setopt($session, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($session, CURLOPT_USERPWD, "acxiom:dmp_success");
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($session);

curl_close($session);

$json = json_decode($response);

$persons = $json->result;
?>

<body id="index" class="home">
 
  <header id="banner" class="body">
    <h1><a href="#">Splash <strong>Share ideas. Share talents. Share knowledge.</strong></a></h1>
    <p><a href="/profile.php?userId=<?php echo $persons[0]->userId?>"><?php echo $persons[0]->firstName?></a></p>
    <nav><ul>
      <li><a href="/home.php?userId=<?php echo $userId?>">home</a></li>
      <li><a href="/friends.php?userId=<?php echo $userId?>">friends</a></li>
      <li><a href="/messages.php?userId=<?php echo $userId?>">messages</a></li>
    </ul></nav>
  </header>
 
<?php
function getAgoString($timestamp) {
    $date = DateTime::createFromFormat('U', $timestamp, new DateTimeZone('America/Los_Angeles'));
    $date2 = new DateTime('now', new DateTimeZone('America/Los_Angeles'));
    $interval=$date2->diff($date, true);

    if ($interval->y > 0) {
        return $interval->y . ' years ago';
    } else if ($interval->m > 0) {
        return $interval->m . ' months ago';
    } else if ($interval->d > 0) {
        return $interval->d . ' days ago';
    } else if ($interval->h > 0) {
        return $interval->h . ' hours ago';
    } else if ($interval->i > 1) {
        return $interval->i . ' minutes ago';
    } else {
        return 'Just now';
    }
}


$url = 'http://localhost:9090/circle/user/' . $userId . '/projects';

$session = curl_init($url);
curl_setopt($session, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($session, CURLOPT_USERPWD, "acxiom:dmp_success");
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($session);
curl_close($session);

$json = json_decode($response);
$projects = $json->result;

?>

<section id="content" class="body">
<h4>My projects</h4>
<?php
foreach ($projects as $project) {
?>
  <ol id="posts-list" class="hfeed">
    <li><article class="hentry">	
      <footer class="post-info">
        <abbr class="published" title="2005-10-10T14:07:00-07:00">
          <?php echo 'Updated ' . getAgoString($project->time);?>
        </abbr>
 
<!--
        <address class="vcard author">
          By <a class="url fn" href="/home.php?userId=<?php echo $userproject->user->userId?>"><?php echo $userproject->user->name; ?></a>
        </address>
-->
      </footer>
 
      <div class="entry-content">
        <p><?php echo  $project->summary;?></p>
      </div>
    </article></li>
  </ol><!-- /#posts-list -->
<?php
}
?>
</section><!-- /#content -->
	
<footer id="contentinfo" class="body">
<!--
	<address id="about" class="vcard body">
		<span class="primary">
			<strong><a href="#" class="fn url">Smashing Magazine</a></strong>
 
			<span class="role">Amazing Magazine</span>
		</span>
		
		<img src="images/avatar.gif" alt="Smashing Magazine Logo" class="photo" />
		<span class="bio">Smashing Magazine is a website and blog that offers resources and advice to web developers and web designers. It was founded by Sven Lennartz and Vitaly Friedman.</span>
 
	</address>
-->
	<p>2014 Copyright LinkHelp LLC</p>
</footer>
 
</body>
</html>
