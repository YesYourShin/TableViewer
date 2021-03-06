import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class TableViewer extends JFrame implements ActionListener {

	private JTextField idField, titleField, publisherField, yearField, priceField, authorField;
	private JButton previousBtn, nextBtn, insertBtn, finishBtn;
	private ResultSet rs;
	private PreparedStatement pstmt;
	private Connection con = null;
	
	
	public TableViewer() {
		super("Database Viewer");
		
		con = null;
		try {
			con = makeConnection();
		} catch(Exception e) {
			System.out.println("데이터베이스에 연결할 수 없어 프로그램을 종료합니다.");
			System.out.println(e.getMessage());
			return;
		}
		try {
			String sql = "select * from books";
			pstmt = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			rs = pstmt.executeQuery();
		} catch(Exception e) {
			System.out.println("SQL 문을 실행에 문제 발생");
			System.out.println(e.getMessage());
			System.out.println("프로그램을 종료합니다.");
			return;
		}

		this.add(new JLabel("Title", JLabel.CENTER));
		titleField = new JTextField();
		this.add(titleField);
		
		this.add(new JLabel("Publisher", JLabel.CENTER));
		publisherField = new JTextField();
		this.add(publisherField);
		
		this.add(new JLabel("Year", JLabel.CENTER));
		yearField = new JTextField();
		this.add(yearField);
		
		this.add(new JLabel("Price", JLabel.CENTER));
		priceField = new JTextField();
		this.add(priceField);
		
		previousBtn = new JButton("Previous");
		previousBtn.addActionListener(this);
		
		nextBtn = new JButton("Next");
		nextBtn.addActionListener(this);
		
		this.add(nextBtn);
		this.add(previousBtn);
		
		insertBtn = new JButton("삽입");
		this.add(insertBtn);
		finishBtn.addActionListener(this);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setSize(350, 200);
		this.setResizable(false);
		
		this.setVisible(true);
		
	}
	
	private static Connection makeConnection() throws Exception {
		String url = "jdbc:mariadb://localhost:3306/oop";
		String id = "root";
		String password = "root";
		
		Connection con = null;
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("드라이버 적재 성공");
		
		con = DriverManager.getConnection(url, id, password);
		System.out.println("데이터베이스 연결 성공");
		
		return con;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == nextBtn || e.getSource() == previousBtn) {
				if (e.getSource() == nextBtn) {
					rs.next();
//					setCurrentBookInfo2TextField();
				} else if(e.getSource() == previousBtn) {
					rs.previous();

//					setCurrentBookInfo2TextField();
				}
				
				int bookId = rs.getInt("book_id");
				idField.setText(String.valueOf(bookId));
				
				String title = rs.getString("title");
				titleField.setText(title);
				
				String publisher = rs.getString("publisher");
				publisherField.setText(publisher);
				
				Date date = rs.getDate("year");
				yearField.setText(String.valueOf(date));
				
				int price = rs.getInt("price");
				priceField.setText(String.valueOf(price));
				
			} else if (e.getSource() == insertBtn) {
				
				String title = titleField.getText();
				String publisher = publisherField.getText();
				String year = yearField.getText();
				String price = priceField.getText();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date(sdf.parse(year).getTime());
				
				String sql = "insert into books(title, publisher, year, price) values(?, ?, ?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				
				pstmt.setString(1,  title);
				pstmt.setString(2,  publisher);
				pstmt.setDate(3,  date);
				pstmt.setInt(4,  Integer.parseInt(price));
				
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(this, "책 등록 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
				
			} else {
				System.out.println("프로그램을 종료합니다.");
				rs.close();
				con.close();
				this.dispose();
				System.exit(0);;
			}
		} catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		new TableViewer();
	}
	
}
