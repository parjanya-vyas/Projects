<?php
	$rnum = $_POST['rnum'];
	$pass = $_POST['pass1'];
	$query = "SELECT * FROM `main_table`";
	
	//echo $query;
	$conn = mysql_connect("localhost","root","");
	mysql_select_db("crypto_login_db",$conn);
	$result = mysql_query($query,$conn);
	echo mysql_error();
	$count = mysql_num_rows($result);
	
	if($count==1)
	{
		
	}
	else
	{
		print("Failure!");
	}
	//echo $result;
	
	
	/*while($row = mysqli_fetch_array($result)) {
	  print( "$row['UserName'] $row['Password']" );
	}*/
	
	
	/*echo "Name:"." ".$name;
	echo "<br/>"."Pass: ".$pass;*/
?>