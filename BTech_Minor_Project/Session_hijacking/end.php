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
	if(isset($_COOKIE['marks']))
	{
		$marks = $_COOKIE['marks'];
		print("Your final marks are:");
		print($marks);
	}
	print("\nThanks for playing! You may close the window now!");
	setcookie('qid','',time()-5);
	setcookie('marks','',time()-5);
	setcookie('session','',time()-5);
?>