
//mySQL JDBC GUI connection
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;

class Retrieve{
public static void main(String[] args){
	JFrame f=new JFrame();
	JLabel label1=new JLabel("Name: ");
	JLabel label2=new JLabel("Address: ");
	JTextField text1=new JTextField(20);
	JTextField text2=new JTextField(20);
	try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test",
			"root", "root");
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery("select * from data where id=1");
			String name="",address="";
			if(rs.next()){
			name=rs.getString("name");
			address=rs.getString("address");
		}
	text1.setText(name);
	text2.setText(address);
	}
catch(Exception e){
		}
	JPanel p=new JPanel(new GridLayout(2,2));
	p.add(label1);
	p.add(text1);
	p.add(label2);
	p.add(text2);
	f.add(p);
	f.setVisible(true);
	f.pack();
	}
}