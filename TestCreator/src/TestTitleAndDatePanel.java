import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TestTitleAndDatePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	JLabel titleLabel = new JLabel("Test Title: ");
	JLabel dateLabel = new JLabel("Test Date: ");
	JLabel minusMarksLabel = new JLabel("Minus Marks: ");
	JTextField titleTextField = new JTextField();
	JTextField dateTextField = new JTextField();
	JTextField minusMarksTextField = new JTextField();
	
	public float getMinusMarks() {
		return Float.parseFloat(minusMarksTextField.getText());
	}
	
	public TestTitleAndDatePanel() {
		GroupLayout masterLayout = new GroupLayout(this);
		titleTextField.setPreferredSize(new Dimension(100, 1));
		this.setLayout(masterLayout);
		masterLayout.setAutoCreateGaps(true);
		masterLayout.setAutoCreateContainerGaps(true);
		masterLayout.setHorizontalGroup(masterLayout.createParallelGroup()
				.addGroup(masterLayout.createSequentialGroup()
						.addComponent(titleLabel)
						.addComponent(titleTextField))
				.addGroup(masterLayout.createSequentialGroup()
						.addComponent(dateLabel)
						.addComponent(dateTextField))
				.addGroup(masterLayout.createSequentialGroup()
						.addComponent(minusMarksLabel)
						.addComponent(minusMarksTextField)));
		masterLayout.setVerticalGroup(masterLayout.createSequentialGroup()
				.addGroup(masterLayout.createParallelGroup()
						.addComponent(titleLabel)
						.addComponent(titleTextField))
				.addGroup(masterLayout.createParallelGroup()
						.addComponent(dateLabel)
						.addComponent(dateTextField))
				.addGroup(masterLayout.createParallelGroup()
						.addComponent(minusMarksLabel)
						.addComponent(minusMarksTextField)));
	}
}
