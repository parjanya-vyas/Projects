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
<link rel="stylesheet" href="fin_css.css" type="text/css" />
<script type="text/JavaScript">
</script>
</head>
<body class="l2_body">
<div class="l2_div"><center>
<h1>Better luck next time</h1><br/>
<h2>You have failed to prove yourself a die hard Harry Potter fan</h2>
<br/><h2>Sorry, but you blew the chance given to you...!<br/>Click the below Icon to continue</h2>
<a href="end.php"><img src="d_h.jpg" height="80px" width="90px" border="2"></a></center>
</div>
</body>
</html>