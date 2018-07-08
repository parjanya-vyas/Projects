import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class BobUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel bob;
	JLabel label, myMessage;
	JTextArea message, chatHistory;
	JButton sendMessage;
	CountDownLatch msgSentLatch;
	
	public void createFrame() {
		bob = new JPanel();
		label = new JLabel();
		message = new JTextArea();
		sendMessage = new JButton("SEND");
		chatHistory = new JTextArea("History:\n");
		myMessage = new JLabel("Enter your message: ");
		this.setSize(500, 600);
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		bob.setLayout(null);
		this.add(bob);
		
		label.setBounds(50, 50, 400, 40);
		bob.add(label);		
		myMessage.setBounds(50, 100, 400, 40);
		bob.add(myMessage);
		message.setBounds(50, 150, 400, 40);
		bob.add(message);
		sendMessage.setBounds(150, 200, 200, 20);
		bob.add(sendMessage);
		chatHistory.setBounds(50, 250, 400, 300);
		bob.add(chatHistory);
		this.setTitle("Bob");
		sendMessage.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendMessage) {
			chatHistory.setText(chatHistory.getText() + "Me: " + message.getText() + "\n");
			msgSentLatch.countDown();
		}
	}
		
	public String getMessageFromTextarea() {
		msgSentLatch = new CountDownLatch(1);
		try {
			msgSentLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String text = message.getText();
//		System.out.println("text after while== " + text);
		return text;
	}
	
	public void addToHistory(String receivedMessage) {
		chatHistory.setText(chatHistory.getText() + "Alice: " + receivedMessage + "\n");
	}
	
}
