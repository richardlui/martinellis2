<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<title>Splash</title>
 
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
    <p><a href="/profile.php?userId=<?php echo $persons[0]->userId?>"><?php echo $persons[0]->firstName?></a>
      &nbsp;&nbsp;<a href="/register.php">Sign up</a>
    </p>
    <nav><ul>
      <li class="active"><a href="/home.php?userId=<?php echo $userId?>">home</a></li>
      <li><a href="/friends.php?userId=<?php echo $userId?>">friends</a></li>
      <li><a href="/messages.php?userId=<?php echo $userId?>">messages</a></li>
    </ul></nav>
  </header><!-- /#banner -->	
 
  <aside id="featured" class="body">
    <article>
      <form action="/searchProject.php" method="post">
        <input type="text" size="60" name="keywords">
        <input type="hidden" name='userId' value="<?php echo $userId ?>">
        <input type="submit" value="Search">
      </form>
    </article>
  </aside>

  <aside id="featured" class="body">
    <article>
      <hgroup>
        <h4>Post your projects</h4>
      </hgroup>
      <form action="/addProject.php" method="post">
      Category:
        <select name='categoryId'>
          <option value='200'>Plumbing</option>
          <option value='201'>Cooking</option>
          <option value='202'>Computer</option>
        </select>
        Summary: <input type="text" size="80" name="summary">
        <input type="hidden" name='userId' value="<?php echo $userId ?>">
        <input type="submit" value="Post">
      </form>
    </article>
  </aside><!-- /#featured -->
 

<?php
function getProjectImg($userId) {
    if ($userId == 100) {
        return "project.jpg";
    } else if ($userId == 101) {
        return "project1.jpg";
    } else if ($userId == 102) {
        return "project2.jpg";
    } else if ($userId == 103) {
        return "project3.jpg";
    } else {
        return "project4.jpg";
    }
}

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

$url = 'http://localhost:9090/circle/user/' . $userId . '/friends/projects';

$session = curl_init($url);

curl_setopt($session, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($session, CURLOPT_USERPWD, "acxiom:dmp_success");
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($session);
curl_close($session);

$json = json_decode($response);
$userprojects = $json->result;

?>

<section id="content" class="body">
<?php
foreach ($userprojects as $userproject) {
?>
  <ol id="posts-list" class="hfeed">
    <li><article class="hentry">	
<!--
      <header>
        <h2 class="entry-title"><a href="#" rel="bookmark" title="Permalink to this POST TITLE">This be the title</a></h2>
 
      </header>
-->
 
      <footer class="post-info">
        <abbr class="published" title="2005-10-10T14:07:00-07:00">
          <?php echo 'Updated ' . getAgoString($userproject->project->time);?>
        </abbr>
 
        <address class="vcard author">
          By <a class="url fn" href="/home.php?userId=<?php echo $userproject->user->userId?>"><?php echo $userproject->user->firstName . ' ' . $userproject->user->lastName; ?></a>
          <p><?php echo  $userproject->project->summary;?></p>


<?php
$url = 'http://localhost:9090/circle/topic/' . $userproject->project->projectId . '/reply';

$session = curl_init($url);

curl_setopt($session, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($session, CURLOPT_USERPWD, "acxiom:dmp_success");
curl_setopt($session, CURLOPT_RETURNTRANSFER, true);

$replyResponse = curl_exec($session);
curl_close($session);

$replyJson = json_decode($replyResponse);
$replies = $replyJson->result;

foreach ($replies as $reply) {
echo "-- " . $reply->comment . "<br/>";
}
?>

      <form action="/addReply.php" method="post">
        <input type="text" size="50" name="comment">
        <input type="hidden" name='userId' value="<?php echo $userId ?>">
        <input type="hidden" name='projectId' value="<?php echo $userproject->project->projectId; ?>">
        <input type="submit" value="Comment">
      </form>

        </address>
      </footer>
 
      <div class="entry-content">
        <img src="/image/<?php echo getProjectImg($userproject->user->userId);?>"/>
      </div><!-- /.entry-content -->
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
