function marks()
{
	var mark=0;
	if(qualify.q1[3].checked==true)
		mark++;
	if(qualify.q2[1].checked==true)
		mark++;
	if(qualify.q3[2].checked==true)
		mark++;
	if(qualify.q4[0].checked==true)
		mark++;
	if(qualify.q5[1].checked==true)
		mark++;
	if(qualify.q6[3].checked==true)
		mark++;
	if(qualify.q7[2].checked==true)
		mark++;
	if(qualify.q8[0].checked==true)
		mark++;
	if(qualify.q9[1].checked==true)
		mark++;
	if(qualify.q10[2].checked==true)
		mark++;
	if(mark>5)
	{
		alert("Congo! Your marks are: "+mark);
	}
	if(mark<=5)
	{
		alert("Sorry, You lost, your marks are: "+mark);
	}
}		