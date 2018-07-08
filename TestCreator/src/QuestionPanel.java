import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class QuestionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	int qId;
	
	JLabel qLabel = new JLabel();
	JTextArea qTextArea = new JTextArea(5,0);
	JTextField op1TextField = new JTextField();
	JTextField op2TextField = new JTextField();
	JTextField op3TextField = new JTextField();
	JTextField op4TextField = new JTextField();
	JRadioButton op1RadioButton = new JRadioButton();
	JRadioButton op2RadioButton = new JRadioButton();
	JRadioButton op3RadioButton = new JRadioButton();
	JRadioButton op4RadioButton = new JRadioButton();
	ButtonGroup radioGroup = new ButtonGroup();
	JButton clearQuestionButton = new JButton("Remove This Question");
	
	public QuestionPanel(int qNo) {
		qId = qNo;
		qLabel.setText(qNo+") ");
		radioGroup.add(op1RadioButton);
		radioGroup.add(op2RadioButton);
		radioGroup.add(op3RadioButton);
		radioGroup.add(op4RadioButton);
		GroupLayout masterLayout = new GroupLayout(this);
		this.setLayout(masterLayout);
		masterLayout.setAutoCreateGaps(true);
		masterLayout.setAutoCreateContainerGaps(true);
		masterLayout.setHorizontalGroup(masterLayout.createSequentialGroup()
				.addComponent(qLabel)
				.addGroup(masterLayout.createParallelGroup()
					.addComponent(qTextArea)
					.addGroup(masterLayout.createSequentialGroup()
							.addComponent(op1RadioButton)
							.addComponent(op1TextField)
							.addComponent(op2RadioButton)
							.addComponent(op2TextField)
							.addComponent(op3RadioButton)
							.addComponent(op3TextField)
							.addComponent(op4RadioButton)
							.addComponent(op4TextField))
					.addComponent(clearQuestionButton)));
		masterLayout.setVerticalGroup(masterLayout.createParallelGroup()
				.addComponent(qLabel)
				.addGroup(masterLayout.createSequentialGroup()
					.addComponent(qTextArea)
					.addGroup(masterLayout.createParallelGroup()
							.addComponent(op1RadioButton)
							.addComponent(op1TextField)
							.addComponent(op2RadioButton)
							.addComponent(op2TextField)
							.addComponent(op3RadioButton)
							.addComponent(op3TextField)
							.addComponent(op4RadioButton)
							.addComponent(op4TextField))
					.addComponent(clearQuestionButton)));
	}
}
