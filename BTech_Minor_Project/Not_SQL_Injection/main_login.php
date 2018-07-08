<?php
	$unsafe_name = $_POST['uname1'];
	$unsafe_pass = $_POST['pass1'];
	$name=mysql_real_escape_string($unsafe_name);
	$pass=mysql_real_escape_string($unsafe_pass);
	$query = "SELECT * FROM `t1` WHERE `UserName` = '$name' AND `Password` = '$pass'";
	
	//echo $query;
	$conn = mysql_connect("localhost","root","");
	mysql_select_db("main_login_db",$conn);
	$result = mysql_query($query,$conn);
	echo mysql_error();
	$count = mysql_num_rows($result);
	
	if($count)
	{
		print("Successful login");
	}
	else
	{
		print("Login Failure!");
	}
?>