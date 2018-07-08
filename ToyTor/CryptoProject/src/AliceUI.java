import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AliceUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	JPanel alice;
	JLabel label, myMessage, temp;
	JTextArea input, aliceText, chatHistory;
	JButton createCktButton, sendMessage;
	ArrayList<String> onionRouterAddresses;
	String destinationAddress;
	
	CountDownLatch createdLatch;
	
	OnionProxy onionProxy;

	public void createFrame() {
		alice = new JPanel();
		label = new JLabel("Enter IP:PORT");
		input = new JTextArea();
		createCktButton = new JButton("Create Network");
		
		this.setSize(500, 600);
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		alice.setLayout(null);
		this.add(alice);
		
		label.setBounds(50, 50, 400, 40);
		alice.add(label);
		input.setBounds(50, 80, 400, 100);
		alice.add(input);
		createCktButton.setBounds(150, 200, 200, 20);
		alice.add(createCktButton);
		this.setTitle("Alice");
		createCktButton.addActionListener(this);
	}

	public void changeUI() {
		label.setText("Circuit created");
		input.setVisible(false);
		createCktButton.setVisible(false);
		alice.remove(input);
		alice.remove(createCktButton);
		myMessage = new JLabel("Enter your message: ");
		aliceText = new JTextArea();
		sendMessage = new JButton("SEND");
		chatHistory = new JTextArea("History:\n");
		myMessage.setBounds(50, 100, 400, 40);
		aliceText.setBounds(50, 150, 400, 40);
		sendMessage.setBounds(50, 200, 200, 20);
		sendMessage.addActionListener(this);
		chatHistory.setBounds(50, 250, 400, 300);
		alice.add(myMessage);
		alice.add(aliceText);
		alice.add(sendMessage);
		alice.add(chatHistory);
		alice.revalidate();
		alice.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == createCktButton) {
			// TODO Auto-generated method stub
			String data = input.getText();
			System.out.println(data);
			onionRouterAddresses = new ArrayList<>();
			String[] dataArray = data.split("\n");
			int limit = dataArray.length-1;
			for (int i=0; i<limit; i++) {
				onionRouterAddresses.add(dataArray[i]);
			}
			System.out.println(onionRouterAddresses);
			destinationAddress = dataArray[limit];
			System.out.println(destinationAddress);
			
			createdLatch = new CountDownLatch(1);
			(new Thread(new AliceUIListener())).start();
			
			try {
				createdLatch.await();
				changeUI();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		} else if(e.getSource() == sendMessage) {
			String text = aliceText.getText();
			chatHistory.setText(chatHistory.getText() + "ME: " + text + "\n");
			onionProxy.send(text, chatHistory);
		}
	}
	
	public void addToHistory(String receivedMessage) {
		chatHistory.setText(chatHistory.getText() + "Bob: " + receivedMessage + "\n");
	}
	
	private class AliceUIListener implements Runnable {
		@Override
		public void run() {
			onionProxy = new OnionProxy();
			onionProxy.startServer();
			
			try {
				onionProxy.createNewCircuit(onionRouterAddresses, 
						Utils.getIpFromIpPortString(destinationAddress), 
						Utils.getPortFromIpPortString(destinationAddress));
				createdLatch.countDown();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		AliceUI aliceUI = new AliceUI();
		aliceUI.createFrame();
	}
}
