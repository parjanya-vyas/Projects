<?php
if(!isset($_COOKIE['qid']))
{
	$qid = 0;
	$question = "Whose pet was a cat?";
	$ans_val1 = "a1";
	$ans_val2 = "b1";
	$ans_val3 = "c1";
	$ans_val4 = "d1";
	$ans1 = "Sirius Black";
	$ans2 = "Peter Petigrew";
	$ans3 = "Mundungus Fletcher";
	$ans4 = "Argus Filtch";
	setcookie("qid",$qid);
}
else
{
	$qid = $_COOKIE['qid'];
	if($qid == 0)
	{
		$question = "Whose pet was a cat?";
		$ans_val1 = "a1";
		$ans_val2 = "b1";
		$ans_val3 = "c1";
		$ans_val4 = "d1";
		$ans1 = "Sirius Black";
		$ans2 = "Peter Petigrew";
		$ans3 = "Mundungus Fletcher";
		$ans4 = "Argus Filtch";
		setcookie("qid",$qid);
	}
	if($qid == 1)
	{
		$question = "Where is the tomb of Ignotus Peverell situated?";
		$ans_val1 = "a2";
		$ans_val2 = "b2";
		$ans_val3 = "c2";
		$ans_val4 = "d2";
		$ans1 = "Little Hangleton";
		$ans2 = "Godrics Hollow";
		$ans3 = "Little Whinging";
		$ans4 = "Greate Hangleton";
		setcookie("qid",$qid);
	}
	if($qid == 2)
	{
		$question = "Which animal is considered cursed?";
		$ans_val1 = "a3";
		$ans_val2 = "b3";
		$ans_val3 = "c3";
		$ans_val4 = "d3";
		$ans1 = "Unicorn";
		$ans2 = "Dragon";
		$ans3 = "Thestral";
		$ans4 = "Phoenix";
		setcookie("qid",$qid);
	}
	if($qid == 3)
	{
		$question = "Maternal surname of voldemoert's mother:";
		$ans_val1 = "a4";
		$ans_val2 = "b4";
		$ans_val3 = "c4";
		$ans_val4 = "d4";
		$ans1 = "Gaunt";
		$ans2 = "Riddle";
		$ans3 = "Marvolo";
		$ans4 = "Slytherin";
		setcookie("qid",$qid);
	}
	if($qid == 4)
	{
		$question = "Name of Fluer's daughter was:";
		$ans_val1 = "a5";
		$ans_val2 = "b5";
		$ans_val3 = "c5";
		$ans_val4 = "d5";
		$ans1 = "Rose";
		$ans2 = "Victoire";
		$ans3 = "Lily";
		$ans4 = "Fluer";
		setcookie("qid",$qid);
	}
	if($qid == 5)
	{
		$question = "Who killed belatrix lestrange?";
		$ans_val1 = "a6";
		$ans_val2 = "b6";
		$ans_val3 = "c6";
		$ans_val4 = "d6";
		$ans1 = "Harry Potter";
		$ans2 = "Ronald Weasely";
		$ans3 = "Hermoine Granger";
		$ans4 = "Molly Weasely";
		setcookie("qid",$qid);
	}
	if($qid == 6)
	{
		$question = "Who was the monster of the chamber of the secrets?";
		$ans_val1 = "a7";
		$ans_val2 = "b7";
		$ans_val3 = "c7";
		$ans_val4 = "d7";
		$ans1 = "Norbert";
		$ans2 = "Aragog";
		$ans3 = "Basilisk";
		$ans4 = "Fluffy";
		setcookie("qid",$qid);
	}
	if($qid == 7)
	{
		$question = "What type of creature was Fluffy?";
		$ans_val1 = "a8";
		$ans_val2 = "b8";
		$ans_val3 = "c8";
		$ans_val4 = "d8";
		$ans1 = "Dog";
		$ans2 = "Spider";
		$ans3 = "Snake";
		$ans4 = "Dragon";
		setcookie("qid",$qid);
	}
	if($qid == 8)
	{
		$question = "Harry was descendant of?";
		$ans_val1 = "a9";
		$ans_val2 = "b9";
		$ans_val3 = "c9";
		$ans_val4 = "d9";
		$ans1 = "Slytherin";
		$ans2 = "Peverell";
		$ans3 = "Gryfindore";
		$ans4 = "Gaunt";
		setcookie("qid",$qid);
	}
	if($qid == 9)
	{
		$question = "Who was cursed by Hermoine and hence lost the trials for quidditch keeper of Gryffindor?";
		$ans_val1 = "a10";
		$ans_val2 = "b10";
		$ans_val3 = "c10";
		$ans_val4 = "d10";
		$ans1 = "Harry Potter";
		$ans2 = "Dean Thomas";
		$ans3 = "Cormac McLaggen";
		$ans4 = "Cedric Diggory";
		setcookie("qid",$qid);
	}
}
?>
<html>
<head>
<link rel="shortcut icon" href="images/icon.ico">
<title>Qualifying Quiz</title>
<link rel="stylesheet" href="fin_css.css" type="text/css" />
</head>
<body class="q_body">
<div class="m_div"><center>
<form name="qualify" action="check.php" method="get">
<table>
<tr><th colspan="4" style="text-align:left"><?php echo $question?></th></tr>
<tr><td><input type="radio" name="q" value=<?php echo $ans_val1?>><?php echo $ans1?></td>
<td><input type="radio" name="q" value=<?php echo $ans_val2?>><?php echo $ans2?></td>
<td><input type="radio" name="q" value=<?php echo $ans_val3?>><?php echo $ans3?></td>
<td><input type="radio" name="q" value=<?php echo $ans_val4?>><?php echo $ans4?></td></tr><tr></tr>
<tr></tr><br/><br/>
<tr><td></td><td><center><input type="submit" class="button" value="Alohomora(Submit)"></center></td><td><center><input type="reset" class="button" value="Finite(Reset)"></center></td></tr>
</table>
</form></center>
</div>
</body>
</html>