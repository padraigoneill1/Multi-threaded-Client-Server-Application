import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class ClientA2 extends JFrame implements ActionListener  {


	private JTextArea jta = new JTextArea();
	private JScrollPane scroll = new JScrollPane(jta);

	private JLabel accountNumberLabel = new JLabel("Enter Account Number");
	private JTextField accountNumberTextField = new JTextField();
	private JButton loginButton = new JButton("Login");

	private JLabel radiusLabel = new JLabel("Enter Radius");
	private JTextField radiusTextField = new JTextField();
	private JButton radiusButton = new JButton("Submit");

	private JLabel outputLabel = new JLabel("Output");



	// IO streams
	private DataOutputStream toServer;
	private DataInputStream fromServer;

	public static void main(String[] args) {
		new ClientA2();
	}

	public ClientA2() {

		JPanel p = new JPanel(new GridLayout(3, 3));

		p.add(accountNumberLabel);
		p.add(accountNumberTextField);
		p.add(loginButton);
		p.add(radiusLabel);
		p.add(radiusTextField);
		p.add(radiusButton );
		p.add(outputLabel);
		p.add(scroll);

		add(p);

		// Button action listeners
		loginButton.addActionListener(this);
		radiusButton.addActionListener(this);
		


		setTitle("Client");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		try {
			// Create a socket to connect to the server
			Socket socket = new Socket("localhost", 8000);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException ex) {
			jta.append(ex.toString() + '\n');
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loginButton) {
			try {
				// Get the account Number from the text field
				System.out.println(accountNumberTextField.getText());
				int accountNum = Integer.parseInt(accountNumberTextField.getText().trim());

				// Send the account number to the server
				toServer.writeInt(accountNum);
				toServer.flush();

				// Get area from the server
				String response = fromServer.readUTF();

				// Display to the text area
				jta.append(response);
				
				//Once logged in make account detail fields uneditable.
				if (response.contains("Welcome")){
					System.out.println("Valid Login");
					loginButton.setEnabled(false);
					accountNumberTextField.setEditable(false);
				}


			}
			catch (NumberFormatException e1) {
				
				jta.append("Account Number must be a number" + "\n");
			}
			catch (IOException e1) {
				jta.append("Invalid login details" + "\n");
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == radiusButton) {
			try {
				// Get the radius from the text field
				double radius = Double.parseDouble(radiusTextField.getText().trim());

				// Send the radius to the server
				toServer.writeDouble(radius);
				toServer.flush();

				// Get area from the server
				double area = fromServer.readDouble();

				// Display to the text area
				jta.append("Radius is " + radius + "\n");
				jta.append("Area received from the server is "
						+ area + '\n');
			}
			catch (NumberFormatException e1) {
				jta.append("Radius must be a number" + "\n");
			}
			catch (IOException ex) {
				jta.append("Invalid Radius" + "\n");
				System.err.println(ex);
			}
		}	
	}
}
