import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.awt.*;
import javax.swing.*;



public class MultiThreadedServerA2 extends JFrame {


	//Database stuff

	/** The name of the MySQL account to use (or empty for anonymous) */
	private final String userName = "root";

	/** The password for the MySQL account (or empty for anonymous) */
	private final String password = "";

	/** The name of the computer running MySQL */
	private final String serverName = "localhost";

	/** The port of the MySQL server (default is 3306) */
	private final int portNumber = 3306;

	/**
	 * The name of the database we are testing with
	 */
	private final String dbName = "areadatabase";

	/** The name of the table we are testing with */
	private final String tableName = "registeredapplicants";

	Statement s = null;
	ResultSet rs = null;

	private String sql_stmt;

	// Text area for displaying contents
	private JTextArea jta = new JTextArea();

	public static void main(String[] args) {
		new MultiThreadedServerA2();
	}

	public MultiThreadedServerA2() {
		// Place text area on the frame
		setLayout(new BorderLayout());
		add(new JScrollPane(jta), BorderLayout.CENTER);

		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		try {
			// Create a server socket
			ServerSocket serverSocket = new ServerSocket(8000);
			jta.append("Server started at " + new Date() + '\n');

			while (true) {
				Socket s1=serverSocket.accept();
				s1.getInetAddress();
				myClient c = new myClient(s1);
				c.address = s1.getInetAddress();
				c.accountValidated = false;		//variable to hold whether client has been validated in sql server
				c.start();
				// Listen for a connection request

			}


		}
		catch(IOException ex) {
			System.err.println(ex);
		}
	}
	private class myClient extends Thread {
		//The socket the client is connected through
		private Socket socket;
		//The ip address of the client
		private InetAddress address;
		//The input and output streams to the client
		private DataInputStream inputFromClient;
		private DataOutputStream outputToClient;
		private boolean accountValidated; 
		private int accountNumber;
		// The Constructor for the client
		public myClient(Socket socket) throws IOException {
			// Create data input and output streams
			inputFromClient = new DataInputStream(
					socket.getInputStream());
			outputToClient = new DataOutputStream(
					socket.getOutputStream());
		}

		/**
		 * Get a new database connection
		 * 
		 * @return
		 * @throws SQLException
		 */
		public Connection getConnection() throws SQLException {
			Connection conn = null;
			Properties connectionProps = new Properties();
			connectionProps.put("user", userName);
			connectionProps.put("password", password);

			conn = DriverManager.getConnection(
					"jdbc:mysql://" + serverName + ":" + portNumber + "/" + dbName, connectionProps);

			return conn;
		}

		/*
		 * The method that runs when the thread starts
		 */
		@Override
		public void run() {
			// Connect to MySQL DB
			Connection conn = null;
			try {
				conn = this.getConnection();
				System.out.println("Connected to database");
			} catch (SQLException e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
				return;
			}
			while (true) {
				

				// radius from the client
				double radius;

				try {
					if (accountValidated){
						try{
							radius = inputFromClient.readDouble();

							// Compute area
							double area = radius * radius * Math.PI;

							// Send area back to the client
							outputToClient.writeDouble(area);
							
							//address & getHostName() & getHostAddress() will return same values as it is run on local machine(127.0.0.1)
							jta.append("Message from client at address: " + address + '\n');
							jta.append("Client Account Number: " + accountNumber + '\n');
							jta.append("Radius received from client: " + radius + '\n');
							jta.append("Area found: " + area + '\n');
						}catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}			
					}
					else{
						accountNumber = inputFromClient.readInt();
						sql_stmt = "SELECT * FROM "+tableName+" WHERE AccountNum =" + accountNumber;

						try {
							System.out.println("SQL Statement is " + sql_stmt);
							Statement s = conn.createStatement();
							s.executeQuery( "SELECT * FROM registeredapplicants WHERE AccountNum =" + accountNumber);
							rs = s.getResultSet();
							if (!rs.isBeforeFirst() ) {    
								System.out.println("Account Number received invalid"); 
								System.out.println("Account Number = " + accountNumber + " . Account number not in system. Access Denied");
								outputToClient.writeUTF("Message From Server:  Sorry the account number "+ accountNumber + " is not in the system" + '\n');
								jta.append("Account Number received invalid "  + '\n');
								jta.append("Account Number received from client: " + accountNumber  + '\n');
								jta.append("Client Address: " + address + '\n');
								accountValidated = false;
							}
							else{
								while (rs.next()) {
									int accountNumVal = rs.getInt("AccountNum");
									String firstNameVal = rs.getString("FirstName");
									String lastNameVal = rs.getString("LastName");

									System.out.println("Account Number = " + accountNumVal + ", First Name = " + firstNameVal + "," + " Last Name = " + lastNameVal);
									outputToClient.writeUTF("Message From Server: "+ '\n'+ "Welcome " + firstNameVal + " " + lastNameVal  + '\n'+ "Your address is " + address.toString() + '\n');
									System.out.println("Client Address :" + address.toString());
									jta.append("Client Account Accepted: Account Number = " + accountNumVal + ", First Name = " + firstNameVal + "," + " Last Name = " + lastNameVal+ '\n'); 
									jta.append("Client Address: " + address + '\n');
									accountValidated = true;
								}
							}

						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}


				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			} 
		}
	}
}