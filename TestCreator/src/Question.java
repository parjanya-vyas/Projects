
public class Question {
	private String questionText;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private int correctAnswer;
	
	public Question(String questionText, 
			String op1, String op2, String op3, String op4,
			int correctAns) {
		this.questionText = questionText;
		this.option1 = op1;
		this.option2 = op2;
		this.option3 = op3;
		this.option4 = op4;
		this.correctAnswer = correctAns;
	}
	
	public Question(String serializedQuestion) {
		String[] questionFields = serializedQuestion.split(";");
		this.questionText = questionFields[0];
		this.option1 = questionFields[1];
		this.option2 = questionFields[2];
		this.option3 = questionFields[3];
		this.option4 = questionFields[4];
		this.correctAnswer = Integer.parseInt(questionFields[5]);
	}
	
	public String getQuestionText() {
		return questionText;
	}
	
	public String getOption1() {
		return option1;
	}

	public String getOption2() {
		return option2;
	}
	
	public String getOption3() {
		return option3;
	}
	
	public String getOption4() {
		return option4;
	}
	
	public int getCorrectAnswer() {
		return correctAnswer;
	}
	
	public String toString() {
		return questionText + ";"
				+ option1 + ";"
				+ option2 + ";"
				+ option3 + ";"
				+ option4 + ";"
				+ correctAnswer;
	}
}
