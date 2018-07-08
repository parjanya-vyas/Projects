<?php
if(!isset($_COOKIE['session']))
{
	print("Error, session not started!");
}
else if($_COOKIE['session']!="the_key")
{
	print("Invalid session");
}
else
{
$ans = $_GET['q'];
if(($ans == 'd1')||($ans == 'b2')||($ans == 'c3')||($ans == 'a4')||($ans == 'b5')||($ans == 'd6')||($ans == 'c7')||($ans == 'a8')||($ans == 'b9')||($ans == 'c10'))
{
	print("True");
	if(isset($_COOKIE['marks']))
	{
		$marks = $_COOKIE['marks'];
		$marks = $marks + 1;
		setcookie("marks",$marks);
	}
	else
	{
		$marks = 1;
		setcookie("marks",$marks);
	}
}
else
{
	print("False");
	if(isset($_COOKIE['marks']))
	{
		$marks = $_COOKIE['marks'];
		$marks = $marks - 1;
		setcookie("marks",$marks);
	}
	else
	{
		$marks = -1;
		setcookie("marks",$marks);
	}
}
if(isset($_COOKIE['qid']))
{
	$qid = $_COOKIE['qid'];
	$qid = $qid + 1;
	setcookie("qid",$qid);
}
else
{
	print("Error");
}
if($qid >= 10)
	header("location: end.php");
else
	header("location: more_q.php");
}
?>