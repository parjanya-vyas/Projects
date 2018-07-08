<html>
<head>
<link rel="shortcut icon" href="images/icon.ico">
<title>Packet Wars Login Page</title>
<script type="text/javaScript">
function signup_redirect()
{
	window.location = "signup_form.html";
}
</script>
<style type="text/css">
body			{background-color: black;
				opacity: 0.6;}

.main_div_class	{position: absolute;
				top: 40%;
				left: 35%;
				height: 30%;
				width: 30%;
				box-shadow: -10px -10px 5px #888888;
				background-color: white}
				
.tab_div_class	{position: absolute;
				top: 30%;
				left: 30%;
				height: 20%;
				width: 20%}

.button			{background-color: red;
				color: white}
				
.login_but_class{position: absolute;
				left: 20%;
				top: 160%;}

.signup_but_class{position: absolute;
				left: 90%;
				top: 160%;}
</style>
</head>
<body>
<div id="main_div" class="main_div_class">
<form action="main_login.php" id="login1" method="post">
<center><div id="tab_div" class="tab_div_class">
<table>
<tr><td><input type="text" name="uname1" placeholder="Username"/></td></tr>
<tr><td><input type="password" name="pass1" placeholder="Password"/></td></tr>
<tr><td><center><div id="login_but" class="login_but_class"><input type="submit" value="Log In" id="submit1" class="button"/></div>
<div id="signup_but" class="signup_but_class"><input type="button" class="button" value="Sign Up" onclick="return signup_redirect()"></div></center></td></tr>
</table>
</center></div>
</form>
</div>
</body>
</html>