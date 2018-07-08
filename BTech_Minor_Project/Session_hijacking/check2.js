function marks()
{
	var mark=0;
	if(qualify.q1[0].checked==true)
		mark++;
	if(qualify.q2[2].checked==true)
		mark++;
	if(qualify.q3[2].checked==true)
		mark++;
	if(qualify.q4[3].checked==true)
		mark++;
	if(qualify.q5[1].checked==true)
		mark++;
	if(mark>=3)
	{
		alert("Congo! Your marks are: "+mark);
		window.open("won.php","","");
	}
	if(mark<3)
	{
		alert("Sorry, You lost, your marks are: "+mark);
		window.open("lost2.php","","");
	}
}