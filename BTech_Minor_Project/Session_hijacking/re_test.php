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
?>
<html>
<head>
<title>Qualifying Quiz</title>
<script src="check2.js" >
</script>
<link rel="stylesheet" href="fin_css.css" type="text/css" />
</head>
<body class="q_body">
<div class="q_div">
<form name="qualify" action="" method="post">
<table>
<tr><th colspan="4" style="text-align:left">1)Initials of Prof. Dumbeldore are:</th></tr>
<tr><td><input type="radio" name="q1" value="a1">A.P.W.B.D.</td>
<td><input type="radio" name="q1" value="b1">A.B.W.P.D.</td>
<td><input type="radio" name="q1" value="c1">A.W.P.B.D.</td>
<td><input type="radio" name="q1" value="d1">A.P.B.W.D.</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">2)What is the full name of Dolores Umbridge?</th></tr>
<tr><td><input type="radio" name="q2" value="a2">Dolores Jade Umbridge</td>
<td><input type="radio" name="q2" value="b2">Dolores June Umbridge</td>
<td><input type="radio" name="q2" value="c2">Dolores Jane Umbridge</td>
<td><input type="radio" name="q2" value="d2">Dolores Julie Umbridge</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">3)What was severus's mother's maternal surname?</th></tr>
<tr><td><input type="radio" name="q3" value="a3">Malfoy</td>
<td><input type="radio" name="q3" value="b3">Snape</td>
<td><input type="radio" name="q3" value="c3">Prince</td>
<td><input type="radio" name="q3" value="d3">Price</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">4)Spell used for defence against the dementors:</th></tr>
<tr><td><input type="radio" name="q4" value="a4">Stupefy</td>
<td><input type="radio" name="q4" value="b4">Alohomora</td>
<td><input type="radio" name="q4" value="c4">Finite</td>
<td><input type="radio" name="q4" value="d4">Expecto Patronum</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">5)Ted Lupin's(Rimus Lupin's son) god father was:</th></tr>
<tr><td><input type="radio" name="q5" value="a5">Sirius Black</td>
<td><input type="radio" name="q5" value="b5">Harry Potter</td>
<td><input type="radio" name="q5" value="c5">Ronald Weasley</td>
<td><input type="radio" name="q5" value="d5">Nevil Longbottom</td></tr><tr></tr><br/><br/>
<tr><td></td><td><center><input type="submit" class="button" value="Alohomora(Submit)" onclick="marks()"></center></td><td><center><input type="reset" class="button" value="Finite(Reset)"></center></td></tr>
</table>
</form>
</div>
</body>
</html>