<?php
	if(!isset($_COOKIE['marks']))
	{
		print("Error");
		exit;
	}
	$marks = $_COOKIE['marks'];
	print("Your final marks are:");
	print($marks);
	print("\nThanks for playing! You may close the window now!");
	setcookie('qid','',time()-5);
	setcookie('marks','',time()-5);
?>