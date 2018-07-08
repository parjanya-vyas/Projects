<?php
	$unsafe_uname = $_POST['uname1'];
	$unsafe_pass = $_POST['pass1'];
	$unsafe_name = $_POST['act_name'];
	$mail = $_POST['mail'];
	$uname=mysql_real_escape_string($unsafe_uname);
	$pass=mysql_real_escape_string($unsafe_pass);
	$name=mysql_real_escape_string($unsafe_name);
	$query = "SELECT * FROM `t1` WHERE `UserName` = '$uname'";
	
	//echo $query;
	$conn = mysql_connect("localhost","root","");
	mysql_select_db("sql_injection_login_db",$conn);
	$result = mysql_query($query,$conn);
	echo mysql_error();
	$count = mysql_num_rows($result);
	
	if($count)
	{
		printf("Username already registered, try with another user name after pressing back!");
	}
	else
	{
		$insert_query = "INSERT INTO `t1` (UserName, Password, Name, Mail) VALUES ('$uname', '$pass', '$name', '$mail')";
		$result2 = mysql_query($insert_query,$conn);
		echo mysql_error();
		if($result2)
		{
			print("Success!");
		}
		else
		{
			print("Error!");
		}
	}
?>