import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TestCreatorUI {

	private JFrame frame;
	private int numOfQuestions = 0;
	
	private JLabel headerLabel = new JLabel("Creating New Test");
	private TestTitleAndDatePanel titleAndDatePanel = new TestTitleAndDatePanel();
	private JButton addQuestionButton = new JButton("Add New Question");
	private JButton createTestButton = new JButton("Create Test");
	private JButton saveTestButton = new JButton("Save Test");
	private JButton loadTestButton = new JButton("Load Test");
	private JPanel masterQuestionPanel;
	private ArrayList<QuestionPanel> questionPanels = new ArrayList<>();
	
	Dimension maxDimension = new Dimension(1500, 200);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestCreatorUI window = new TestCreatorUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestCreatorUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel masterPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(masterPanel);
		masterQuestionPanel = new JPanel();
		masterQuestionPanel.setLayout(new BoxLayout(masterQuestionPanel, BoxLayout.Y_AXIS));
		masterQuestionPanel.add(addNewQuestion());
		headerLabel.setMaximumSize(maxDimension);
		headerLabel.setFont(new Font(headerLabel.getFont().getFontName(),
				headerLabel.getFont().getStyle(), 30));
		titleAndDatePanel.setMaximumSize(maxDimension);
		createTestButton.setMaximumSize(new Dimension(addQuestionButton.getMaximumSize()));
		saveTestButton.setMaximumSize(new Dimension(addQuestionButton.getMaximumSize()));
		loadTestButton.setMaximumSize(new Dimension(addQuestionButton.getMaximumSize()));
		
		initClickListeners();
		
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
		masterPanel.add(headerLabel);
		masterPanel.add(titleAndDatePanel);
		masterPanel.add(masterQuestionPanel);
		masterPanel.add(addQuestionButton);
		masterPanel.add(Box.createVerticalStrut(10));
		masterPanel.add(createTestButton);
		masterPanel.add(Box.createVerticalStrut(10));
		masterPanel.add(saveTestButton);
		masterPanel.add(Box.createVerticalStrut(10));
		masterPanel.add(loadTestButton);
		masterPanel.add(Box.createVerticalGlue());
		
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		frame.getContentPane().add(scrollPane);
	}

	private void initClickListeners() {
		addQuestionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				masterQuestionPanel.add(addNewQuestion());
				masterQuestionPanel.revalidate();
				masterQuestionPanel.repaint();
			}
		});
		saveTestButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Question> questions = createQuestionArrayList();
				Test newTest = new Test(titleAndDatePanel.titleTextField.getText(), 
						titleAndDatePanel.dateTextField.getText(), 
						questions.size(), titleAndDatePanel.getMinusMarks(),questions);
				newTest.saveTest(titleAndDatePanel.titleTextField.getText()+"_partial");
				JOptionPane.showMessageDialog(null, "Test :"+titleAndDatePanel.titleTextField.getText()+" saved successfully!");
			}
		});
		loadTestButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Test curTest = new Test(titleAndDatePanel.titleTextField.getText()+"_partial");
				titleAndDatePanel.dateTextField.setText(curTest.getTestDate());
				titleAndDatePanel.minusMarksTextField.setText(""+curTest.getMinusMarks());
				for(int i=0;i<curTest.getNumberOfQuestions();i++) {
					QuestionPanel questionPanel = questionPanels.get(i);
					Question question = curTest.getQuestionList().get(i);
					questionPanel.qTextArea.setText(question.getQuestionText());
					questionPanel.op1TextField.setText(question.getOption1());
					questionPanel.op2TextField.setText(question.getOption2());
					questionPanel.op3TextField.setText(question.getOption3());
					questionPanel.op4TextField.setText(question.getOption4());
					switch(question.getCorrectAnswer()) {
						case 0: questionPanel.op1RadioButton.setSelected(true);break;
						case 1: questionPanel.op2RadioButton.setSelected(true);break;
						case 2: questionPanel.op3RadioButton.setSelected(true);break;
						case 3: questionPanel.op4RadioButton.setSelected(true);
					}
					masterQuestionPanel.add(addNewQuestion());
				}
				masterQuestionPanel.revalidate();
				masterQuestionPanel.repaint();
			}
		});
		createTestButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Question> questions = createQuestionArrayList();
				Test newTest = new Test(titleAndDatePanel.titleTextField.getText(), 
						titleAndDatePanel.dateTextField.getText(), 
						questions.size(), titleAndDatePanel.getMinusMarks(),questions);
				HTMLCreator testCreator = new HTMLCreator(newTest, titleAndDatePanel.titleTextField.getText());
				try {
					testCreator.createHTML();
					JOptionPane.showMessageDialog(null, "Test :"+titleAndDatePanel.titleTextField.getText()+".html created successfully!");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	private ArrayList<Question> createQuestionArrayList() {
		ArrayList<Question> questions = new ArrayList<>();
		for(QuestionPanel qPanel : questionPanels) {
			int correctAns=-1;
			if(qPanel.op1RadioButton.isSelected())
				correctAns=0;
			if(qPanel.op2RadioButton.isSelected())
				correctAns=1;
			if(qPanel.op3RadioButton.isSelected())
				correctAns=2;
			if(qPanel.op4RadioButton.isSelected())
				correctAns=3;
			questions.add(new Question(qPanel.qTextArea.getText(), 
					qPanel.op1TextField.getText(), 
					qPanel.op2TextField.getText(), 
					qPanel.op3TextField.getText(), 
					qPanel.op4TextField.getText(), 
					correctAns));
		}
		return questions;
	}

	private Component addNewQuestion() {
		QuestionPanel newQuestion = new QuestionPanel(++numOfQuestions);
		newQuestion.setMaximumSize(maxDimension);
		newQuestion.op1RadioButton.setSelected(true);
		newQuestion.clearQuestionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i=newQuestion.qId+1;i<=numOfQuestions;i++) {
					QuestionPanel q = getQuestionPanelByLabel(masterQuestionPanel, i+") ");
					q.qLabel.setText((i-1)+") ");
				}
				masterQuestionPanel.remove(newQuestion);
				masterQuestionPanel.revalidate();
				masterQuestionPanel.repaint();
				questionPanels.remove(newQuestion);
				numOfQuestions--;
			}
		});
		questionPanels.add(newQuestion);
		return newQuestion;
	}
	
	private static QuestionPanel getQuestionPanelByLabel(Container container, String componentId){
        if(container.getComponents().length > 0)
        {
            for(Component c : container.getComponents())
            {
                if(componentId.equals(((QuestionPanel)c).qLabel.getText()))
                    return ((QuestionPanel)c);
            }
        }

        return null;

    }
}
