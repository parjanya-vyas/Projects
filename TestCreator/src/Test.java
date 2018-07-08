import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Test {
	private String testTitle;
	private String testDate;
	private int numberOfQuestions;
	private float minusMarks;
	private ArrayList<Question> questionList;
	
	public Test(String title, String date,
			int numQuestions, float minusMarks,
			ArrayList<Question> questionList) {
		this.testTitle = title;
		this.testDate = date;
		this.numberOfQuestions = numQuestions;
		this.minusMarks = minusMarks;
		this.questionList = questionList;
	}
	
	public Test(String serializedTestFilePath) {
		try {
			File savedTestFile = new File(serializedTestFilePath);
			FileInputStream fileInputStream = new FileInputStream(savedTestFile);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
			this.testTitle = bufferedReader.readLine();
			this.testDate = bufferedReader.readLine();
			this.minusMarks = Float.parseFloat(bufferedReader.readLine());
			this.numberOfQuestions = Integer.parseInt(bufferedReader.readLine());
			this.questionList = new ArrayList<>();
			String line;
			while((line = bufferedReader.readLine()) != null)
				questionList.add(new Question(line));
			bufferedReader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Question> getQuestionList() {
		return questionList;
	}
	
	public float getMinusMarks() {
		return minusMarks;
	}
	
	public String getTestTitle() {
		return testTitle;
	}
	
	public String getTestDate() {
		return testDate;
	}
	
	public int getNumberOfQuestions() {
		return numberOfQuestions;
	}
	
	public void saveTest(String filePath) {
		try {
			File saveFile = new File(filePath);
			if(!saveFile.exists())
				saveFile.createNewFile();
			FileOutputStream outStream = new FileOutputStream(saveFile);
			PrintWriter outputFile = new PrintWriter(new OutputStreamWriter(outStream, StandardCharsets.UTF_8));
			outputFile.println(testTitle);
			outputFile.println(testDate);
			outputFile.println(minusMarks);
			outputFile.println(numberOfQuestions);
			for(Question question : questionList)
				outputFile.println(question.toString());
			outputFile.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
