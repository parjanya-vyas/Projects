import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class HTMLCreator {
	private Test test;
	private String outputFileName;
	private String htmlString = "";
	
	public HTMLCreator(Test test, String outputFileName) {
		this.test = test;
		this.outputFileName = outputFileName;
	}

	private void createTitle() {
		htmlString += ("<title>" + test.getTestTitle() + "</title>\n");
	}
	
	private void createScript() {
		htmlString += "<script>\n"
					+"var x = setInterval(function() {\n"
					+"\tvar cur_time = document.getElementById(\"timer\").innerHTML;\n"
					+"\tvar cur_time_arr = cur_time.split(\":\");\n"
					+"\tvar now_sec = parseInt(cur_time_arr[1]);\n"
					+"\tvar now_min = parseInt(cur_time_arr[0]);\n"
					+"\tif(now_sec > 0)\n"
					+"\t\tdocument.getElementById(\"timer\").innerHTML = cur_time_arr[0] + \":\" + (now_sec-1);\n"
					+"\telse if(now_min > 0) {\n"
					+"\t\tdocument.getElementById(\"timer\").innerHTML = (now_min-1) + \":\" + 59;\n"
					+"\t\tif(now_min == 10)\n"
					+"\t\t\talert(\"10 minutes remaining!\");\n"
					+"\t\telse if(now_min == 5)\n"
					+"\t\t\talert(\"5 minutes remaining!\");\n"
					+"\t}\n"
					+"\telse {\n"
					+"\t\tclearInterval(x);\n"
					+"\t\tdocument.getElementById(\"timer\").innerHTML = \"EXPIRED\";\n"
					+"\t\talert(\"Time up!\");\n"
					+"\t\treturn calculateMarks();\n"
					+"\t}\n"
					+"}, 1000);\n\n"
					
					+ "function calculateMarks() {\n"
					+ "\tvar marks=0;\n"
					+ "\tvar correct=0;\n"
					+ "\tvar incorrect=0;\n"
					+ "\tvar unattempted=0;\n"
					+ "\tfor(i=0;i<" + test.getNumberOfQuestions() + ";i++) {\n"
					+ "\t\tdocument.getElementById(\"tbody_q\"+i).style.display = '';"
					+ "\t\tvar currentCorrect = document.getElementById(\"ans\"+i).value;\n"
        			+ "\t\tvar corAns = \"Correct Answer: \" + document.getElementById(\"txt\"+i+currentCorrect).innerText;\n"
        			+ "\t\tvar youAns = \"Your Answer: \";\n"
        			+ "\t\tif(document.getElementById(i+\"_\"+0).checked)\n"
        			+ "\t\t\tyouAns += document.getElementById(\"txt\"+i+0).innerText;\n"
        			+ "\t\tif(document.getElementById(i+\"_\"+1).checked)\n"
        			+ "\t\t\tyouAns += document.getElementById(\"txt\"+i+1).innerText;\n"
        			+ "\t\tif(document.getElementById(i+\"_\"+2).checked)\n"
        			+ "\t\t\tyouAns += document.getElementById(\"txt\"+i+2).innerText;\n"
        			+ "\t\tif(document.getElementById(i+\"_\"+3).checked)\n"
        			+ "\t\t\tyouAns += document.getElementById(\"txt\"+i+3).innerText;\n"
        			+ "\t\tdocument.getElementById(\"you\"+i).innerHTML = youAns;\n"
        			+ "\t\tdocument.getElementById(\"cor\"+i).innerHTML = corAns;\n"
        			+ "\t\tif(document.getElementById(i+\"_\"+currentCorrect).checked) {\n"
        			+ "\t\t\tmarks++;\n"
        			+ "\t\t\tcorrect++;\n"
        			+ "\t\t}\n"
        			+ "\t\telse if(!document.getElementById(i+\"_\"+0).checked&&!document.getElementById(i+\"_\"+1).checked&&!document.getElementById(i+\"_\"+2).checked&&!document.getElementById(i+\"_\"+3).checked) {\n"
        			+ "\t\t\tunattempted++;\n"
        			+ "\t\t\tcontinue;\n"
        			+ "\t\t}\n"
        			+ "\t\telse {\n"
        			+ "\t\t\tmarks-=" + test.getMinusMarks() + ";\n"
        			+ "\t\t\tincorrect++;\n"
        			+ "\t\t}\n"
    				+ "\t}\n"
    				+ "\tdocument.getElementById(\"finalMarks\").innerHTML = \"Final Marks:\" + marks;\n"
    				+ "\tdocument.getElementById(\"totCorrect\").innerHTML = \"Correct:\" + correct;\n"
    				+ "\tdocument.getElementById(\"totIncorrect\").innerHTML = \"Incorrect:\" + incorrect;\n"
    				+ "\tdocument.getElementById(\"totUnattempted\").innerHTML = \"Unattempted:\" + unattempted;\n"
    				+ "\n\treturn false;\n"
					+ "}\n\n"
    				
    				+ "function askConfirm() {\n"
    				+ "\tvar ret = confirm(\"Are you sure you want to submit? Once you submit you cannot change your answers!\");\n"
    				+ "\tif(ret == true) {\n"
    				+ "\t\tclearInterval(x);\n"
    				+ "\t\tdocument.getElementById(\"timer\").innerHTML = \"EXPIRED\";\n"
    				+ "\t\treturn calculateMarks();\n"
    				+ "\t}\n"
    				+ "\telse\n"
    				+ "\t\treturn false;\n"
    				+ "}\n\n"
    				
    				+ "function changeColor(queNo, code) {\n"
    				+ "\tvar tabQue = document.getElementById(\"tab_q_\"+queNo);\n"
    				+ "\tswitch(code) {\n"
    				+ "\t\tcase 0: tabQue.style.backgroundColor = \"#FCFCFC\";break;\n"
    				+ "\t\tcase 1: tabQue.style.backgroundColor = \"#00FF00\";break;\n"
    				+ "\t\tcase 2: tabQue.style.backgroundColor = \"#FF0000\";break;\n"
    				+ "\t\tcase 3: tabQue.style.backgroundColor = \"#0000FF\";\n"
    				+ "\t}\n"
    				+ "}\n\n"
    				
    				+ "function changeStatus(qNo) {\n"
    				+ "\tvar flagButton = document.getElementById(\"flag_\"+qNo);\n"
    				+ "\tvar opGrp = document.getElementsByName(\"q\"+qNo);\n"
    				+ "\tvar isAttempted = 0;\n"
    				+ "\tfor(var i=0;i<opGrp.length;i++) {\n"
    				+ "\t\tif(opGrp[i].checked)\n"
    				+ "\t\t\tisAttempted++;\n"
    				+ "\t}\n"
    				+ "\tif(flagButton.value === \"Flag Question\") {\n"
    				+ "\t\tif(isAttempted == 0)\n"
    				+ "\t\t\tchangeColor(qNo, 0);\n"
    				+ "\t\telse\n"
    				+ "\t\t\tchangeColor(qNo, 1);\n"
    				+ "\t}\n"
    				+ "\telse {\n"
    				+ "\t\tif(isAttempted == 0)\n"
    				+ "\t\t\tchangeColor(qNo, 2);\n"
    				+ "\t\telse\n"
    				+ "\t\t\tchangeColor(qNo, 3);\n"
    				+ "\t}\n"
    				+ "}\n\n"
					
					+ "function clearAns(val) {\n"
					+ "\tvar ele = document.getElementsByName(\"q\"+val);\n"
					+ "\tfor(var i=0;i<ele.length;i++)\n"
					+ "\t\tele[i].checked = false;\n"
					+ "\tchangeStatus(val);\n"
					+ "}\n\n"
					
					+ "function displayQue(val) {\n"
					+ "\tif(val>=0 && val<" + test.getNumberOfQuestions() + ") {\n"
					+ "\t\tfor(i=0;i<" + test.getNumberOfQuestions() + ";i++) {\n"
					+ "\t\t\tvar ele = document.getElementById(\"tbody_q\"+i);\n"
					+ "\t\t\tif(i==val) {\n"
					+ "\t\t\t\tele.style.display = '';\n"
					+ "\t\t\t} else {\n"
					+ "\t\t\t\tele.style.display = 'none';\n"
					+ "\t\t\t}\n"
					+ "\t\t}\n"
					+ "\t}\n"
					+ "}\n\n"

					+ "function flagQue(qNo) {\n"
					+ "\tvar flagButton = document.getElementById(\"flag_\"+qNo);\n"
					+ "\tif(flagButton.value === \"Flag Question\")\n"
					+ "\t\tflagButton.value = \"Clear Flag\";\n"
					+ "\telse\n"
					+ "\t\tflagButton.value = \"Flag Question\";\n"
					+ "\tchangeStatus(qNo);\n"
					+ "}\n\n"

					+ "function init() {\n"
					+ "\tdisplayQue(0);\n"
					+ "}\n"
					
					+ "</script>\n";
	}
	
	private void createHead() {
		htmlString += "<head>\n";
		createTitle();
		createScript();
		htmlString += "</head>\n";
	}
	
	private void createTestHeading() {
		htmlString += "<h1>" + test.getTestTitle() + "</h1>\n";
		htmlString += "<h2>" + test.getTestDate() + "</h2>\n<hr/>\n";
		htmlString += "<h3 id=\"timer\">90:00</h3>\n"
					+ "<h3 id=\"finalMarks\"></h3>\n"
					+ "<h3 id=\"totCorrect\"></h3>\n"
					+ "<h3 id=\"totIncorrect\"></h3>\n"
					+ "<h3 id=\"totUnattempted\"></h3>\n<hr/>\n";
	}
	
	private void createQuestionRow(int id) {
		Question curQuestion = test.getQuestionList().get(id);
		int curQueNo = id + 1;
		htmlString += "<tbody id=\"tbody_q" + id + "\">"
				+ "<tr><th colspan=\"4\" style=\"text-align:left\">" + curQueNo + ") " + curQuestion.getQuestionText() + "</th></tr>\n"
				+ "<input type=\"hidden\" name=\"ans" + id + "\" id=\"ans" + id + "\" value=\"" + curQuestion.getCorrectAnswer() + "\"/>\n"
				+ "<tr><td id=\"txt"+ id +"0\"><input type=\"radio\" name=\"q" + id + "\" id=\"" + id + "_0\" onclick=\"changeStatus(" + id + ")\">" + curQuestion.getOption1() + "</td>\n"
				+ "<td id=\"txt"+ id +"1\"><input type=\"radio\" name=\"q" + id + "\" id=\"" + id + "_1\" onclick=\"changeStatus(" + id + ")\">" + curQuestion.getOption2() + "</td>\n"
				+ "<td id=\"txt"+ id +"2\"><input type=\"radio\" name=\"q" + id + "\" id=\"" + id + "_2\" onclick=\"changeStatus(" + id + ")\">" + curQuestion.getOption3() + "</td>\n"
				+ "<td id=\"txt"+ id +"3\"><input type=\"radio\" name=\"q" + id + "\" id=\"" + id + "_3\" onclick=\"changeStatus(" + id + ")\">" + curQuestion.getOption4() + "</td></tr>\n"
				+ "<tr><td colspan=\"3\" style=\"text-align:left\"><input type=\"button\" value=\"Clear Answer\" onclick=\"clearAns(" + id + ")\"/></td><td><input id=\"flag_" + id + "\" type=\"button\" value=\"Flag Question\" onclick=\"flagQue(" + id + ")\"/></td></tr>\n"
				+ "<tr><td colspan=\"3\"><input type=\"button\" value=\"<<Previous Question\" onclick=\"displayQue(" + id + "-1)\"/></td><td><input type=\"button\" value=\"Next Question>>\" onclick=\"displayQue(" + id + "+1)\"/></td></tr>"
				+ "<tr><td colspan=\"4\" style=\"text-align:left\" id=\"you" + id + "\"></td></tr>\n"
				+ "<tr><td colspan=\"4\" style=\"text-align:left\" id=\"cor" + id + "\"></td></tr>\n"
				+ "</tbody>\n";
	}
	
	private void createQuestionAccessGrid() {
		htmlString += "<table cellspacing=\"7\">\n";
		for(int i=0;i<10;i++) {
			htmlString += "<tr>\n";
			for(int j=0;j<10;j++) {
				htmlString += "<td><input id=\"tab_q_" + (i*10+j) + "\" type=\"button\" value=\"" + (i*10+j+1) +"\" onclick=\"displayQue(" + (i*10+j) + ")\" style=\"background-color:#FCFCFC\"></td>\n";
			}
			if(i==1)
				htmlString += "<td rowspan=\"2\" style=\"background-color:#FCFCFC;border-style:solid;text-align:center\">Unattempted</td>\n";
			else if(i==3)
				htmlString += "<td rowspan=\"2\" style=\"background-color:#00FF00;border-style:solid;text-align:center\">Attempted</td>\n";
			else if(i==5)
				htmlString += "<td rowspan=\"2\" style=\"background-color:#FF0000;border-style:solid;text-align:center\">Flagged-Unattempted</td>\n";
			else if(i==7)
				htmlString += "<td rowspan=\"2\" style=\"background-color:#0000FF;border-style:solid;text-align:center\">Flagged-Attempted</td>\n";
			htmlString += "</tr>\n";
		}
		htmlString += "</table>\n";
	}
	
	private void createBody() {
		htmlString += "<body onload=\"init()\">\n";
		htmlString += "<center>\n";
		createTestHeading();
		htmlString += "<table>\n";
		htmlString += "<form name=\"test_body\" method=\"post\">\n";
		for (int i=0; i<test.getNumberOfQuestions(); i++)
			createQuestionRow(i);
		htmlString += "<tr><td colspan=\"4\"><center><input type=\"submit\" value=\"Submit\" onclick=\"return askConfirm()\"></center></td></tr>\n";
		htmlString += "</form>\n";
		htmlString += "</table>\n<br/><br/>\n";
		createQuestionAccessGrid();
		htmlString += "</center>\n";
		htmlString += "</body>\n";
	}

	private void createHTMLString() {
		htmlString += "<html>\n";
		createHead();
		createBody();
		htmlString += "</html>\n";
	}
	
	public void createHTML() throws FileNotFoundException {
		createHTMLString();
		File outFile = new File(outputFileName + ".html");
		FileOutputStream outStream = new FileOutputStream(outFile);
		PrintWriter outputFile = new PrintWriter(new OutputStreamWriter(outStream, StandardCharsets.UTF_8));
		outputFile.print(htmlString);
		outputFile.close();
	}
}
