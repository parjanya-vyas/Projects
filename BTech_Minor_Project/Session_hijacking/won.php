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
<title>Winner</title>
<link rel="stylesheet" href="fin_css.css" type="text/css" />
<script type="text/JavaScript">
</script>
</head>
<body class="w_body">
<div class="l1_div"><center>
<h1>Congratulations!!!</h1><br/>
<h2>You have proved yourself a die hard Harry Potter fan</h2>
<br/><h2>You are now eligible to become a respected member of this Harry Potter fan club!!!<br/>Click here to fill up the details and test your level of Harry Potter Addiction!</h2><br/>
<a class="sub_but" href="reg_form.php">Alohomora</a></center>
</div>
</body>
</html>