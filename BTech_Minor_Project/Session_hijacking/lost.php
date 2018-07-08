<?php
if(!isset($_COOKIE['session']))
{
	print("Error, session not started!");
	die();
}
else if($_COOKIE['session']!="the_key")
{
	print("Invalid session");
	die();
}
?>
<html>
<head>
<title>Loser</title>
<link rel="stylesheet" type="text/css" href="fin_css.css" />
<script type="text/JavaScript">
</script>
</head>
<body class="l1_body">
<div class="l1_div"><center>
<h1>Better luck next time</h1><br/>
<h2>You have failed to prove yourself a die hard Harry Potter fan</h2>
<br/><h2>But still you have got a chance...<br/>Read the questions more carefully this time<br/>Click here for a retest!</h2><br/>
<a class="sub_but" href="re_test.php">Alohomora</a></center>
</div>
</body>
</html>