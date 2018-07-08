<?php
	$name = $_POST['uname1'];
	$pass = $_POST['pass1'];
	$query = "SELECT * FROM `t1` WHERE `UserName` = '$name' AND `Password` = '$pass'";
	
	//echo $query;
	$conn = mysql_connect("localhost","root","");
	mysql_select_db("sql_injection_login_db",$conn);
	$result = mysql_query($query,$conn);
	echo mysql_error();
	$count = mysql_num_rows($result);
	
	if($count)
	{
		while(($cols = mysql_fetch_array($result)))
		{
			echo $cols['UserName'];
			echo " ";
			echo $cols['Password'];
			echo "<br/>";
		}
	}
	else
	{
		echo "Failure";
	}
	//echo $result;
	
	
	/*while($row = mysqli_fetch_array($result)) {
	  print( "$row['UserName'] $row['Password']" );
	}*/
	
	
	/*echo "Name:"." ".$name;
	echo "<br/>"."Pass: ".$pass;*/
?>