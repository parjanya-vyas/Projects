<?php
$marks = 0;
if(isset($_POST['hidden3']))
{
if($_POST['hidden3']=="the_key")
{
	if($_POST['q1']=="a1")
		$marks++;
	if($_POST['q2']=="c2")
		$marks++;
	if($_POST['q3']=="c3")
		$marks++;
	if($_POST['q4']=="d4")
		$marks++;
	if($_POST['q5']=="b5")
		$marks++;
	if($marks>=3)
	{
		header("location: won.php");
	}
	else
	{
		header("location: lost.php");
	}
}
else
{
	printf("Invalid session");
}
}
else
	printf("Invalid session");
die();
?>