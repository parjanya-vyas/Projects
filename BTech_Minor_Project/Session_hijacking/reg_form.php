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
<title>Registration Page</title>
<link rel="stylesheet" href="fin_css.css" type="text/css" />
<script src="reg.js">
</script>
</head>
<body class="r_body">
<div class="r_div"><center>
<h1 style = "text-align: center">Fill up the form to have more fun!</h1>
		<form name="main_form" method="post" action="take.php" onsubmit="return more_quiz()">
		<h3>All fields are mendatory</h3><hr/>
		<table class="r_table">
			<tr>
				<th>Name* </th>
				<td>:<input type="text" name="st_first_name" value="First name" style="color:#888" size="10" maxlength="20" onfocus="vanish(this)" onblur="reappear(this)">
				<input type="text" name="Last_name" value="Last name" size="10" maxlength="40" style="color:#888" onfocus="vanish(this)" onblur="reappear(this)"></td>
			</tr>
			<tr>
				<th>Gender*</th>
				<td>:<input type="radio" name="gender" value="male" checked="checked">Male
				<input type="radio" name="gender" value="female">Female</td>
			</tr>
			<tr>
				<th>Mobile Number*</th>
				<td>:<input type="number" name="number1" value="91" min="0" max="99" style="color:#888" onfocus="vanish(this)" onblur="reappear(this)" style="width:10px">
				<input type="number" name="number2" value="" min="0" max="9999999999" style="color:#888" onfocus="vanish(this)" onblur="reappear(this)"></td>
			</tr>
			<tr>
				<th>Email ID*</th>
				<td>:<input type="email" name="mail" value="Email Address" size="30" maxlength="50" style="color:#888" onfocus="vanish(this)" onblur="reappear(this)"></td>
			</tr>
			<tr>
				<th>Password*</th>
				<td>:<input type="password" name="pass" size="30" placeholder="Type Password here"></td>
			</tr>
			<tr>
				<th>Confirm Password*</th>
				<td>:<input type="password" name="conf_pass" size="30" placeholder="Type Password here"></td>
			</tr>
		</table>
		<input class="button" type="submit" value="Alohomora(Submit)">
		<input class="button" type="reset" value="Finite(Clear)">
		</form>
</center></div>
</body>
</html>