<html>
<head>
<title>Qualifying Quiz</title>
<script src="check.js" >
</script>
<link rel="stylesheet" type="text/css" href="fin_css.css" />
</head>
<body class="q_body">
<div class="q_div"><center>
<form name="qualify" action="check_qtest.php" method="post">
<input type="hidden" name="hidden1" id="hidden1" value="true">
<input type="hidden" name="hidden2" id="hidden2" value="nothing">
<input type="hidden" name="hidden3" id="hidden3" value=<?php echo $_COOKIE['session']?>>
<table>
<tr><th colspan="4" style="text-align:left">1)Name the elf who was a key to find the locket of slytherin:</th></tr>
<tr><td><input type="radio" name="q1" value="a1">Kreacher</td>
<td><input type="radio" name="q1" value="b1">Dobby</td>
<td><input type="radio" name="q1" value="c1">Winky</td>
<td><input type="radio" name="q1" value="d1">Trelawny</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">2)What is full form of R.A.B.?</th></tr>
<tr><td><input type="radio" name="q2" value="a2">Rufes A. Black</td>
<td><input type="radio" name="q2" value="b2">Regulas A. Bond</td>
<td><input type="radio" name="q2" value="c2">Regulas A. Black</td>
<td><input type="radio" name="q2" value="d2">Ronald A. Blue</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">3)Nick name of James Potter was?</th></tr>
<tr><td><input type="radio" name="q3" value="a3">Mooney</td>
<td><input type="radio" name="q3" value="b3">Wormtail</td>
<td><input type="radio" name="q3" value="c3">Prongs</td>
<td><input type="radio" name="q3" value="d3">Padfoot</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">4)Spell which became identity of Harry Potter</th></tr>
<tr><td><input type="radio" name="q4" value="a4">Stupefy</td>
<td><input type="radio" name="q4" value="b4">Alohomora</td>
<td><input type="radio" name="q4" value="c4">Sectum Sempra</td>
<td><input type="radio" name="q4" value="d4">Expelliarmus</td></tr><tr></tr>
<tr><th colspan="4" style="text-align:left">5)After Fudge who became the minister of magic?</th></tr>
<tr><td><input type="radio" name="q5" value="a5">P. Thikness</td>
<td><input type="radio" name="q5" value="b5">Scrimguer</td>
<td><input type="radio" name="q5" value="c5">Shackelbolt</td>
<td><input type="radio" name="q5" value="d5">Harry Potter</td></tr><tr></tr><br/><br/>
<tr><td></td><td><center><input type="submit" class="button" value="Alohomora(Submit)"></center></td><td><center><input type="reset" class="button" value="Finite(Reset)"></center></td></tr>
</table>
</form></center>
</div>
</body>
</html>