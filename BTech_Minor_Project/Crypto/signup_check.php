<?php
	$unsafe_rnum = $_POST['rnum1'];
	$unsafe_pass = $_POST['pass1'];
	$unsafe_name = $_POST['act_name'];
	$mail = $_POST['mail'];
	$rnum=mysql_real_escape_string($unsafe_rnum);
	$pass=mysql_real_escape_string($unsafe_pass);
	$name=mysql_real_escape_string($unsafe_name);
	$query = "SELECT * FROM `main_table` WHERE `RollNumber` = '$rnum'";
	
	//echo $query;
	$conn = mysql_connect("localhost","root","");
	mysql_select_db("crypto_login_db",$conn);
	$result = mysql_query($query,$conn);
	echo mysql_error();
	$count = mysql_num_rows($result);
	
	if($count)
	{
		echo "Username already registered, try with another user name after pressing back!";
		header('Location: index.html');
	}
	else
	{
		$insert_query = "INSERT INTO `main_table` (RollNumber, Password, Name, Mail, Points) VALUES ('$rnum', '$pass', '$name', '$mail', '0')";
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