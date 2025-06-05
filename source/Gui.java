package view;

import search.SearchGUI;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;



public class Gui extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel searchLabel;
	private JButton total;
	private JButton type;
	private JButton district;
	private JButton rating;
	private JScrollPane resultScroll;
	private JTable table;
	private DefaultTableModel tableModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {	
		//기본 요소 디자인
		setBackground(new Color(240, 240, 240));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 900);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(240, 240, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//검색 타이틀
		searchLabel = new JLabel("기관 검색");
		searchLabel.setFont(new Font("한컴 말랑말랑 Regular", Font.BOLD, 32));
		searchLabel.setBounds(600, 45, 237, 84);
		searchLabel.setVerticalAlignment(JButton.CENTER);
		searchLabel.setHorizontalAlignment(JButton.CENTER);
		searchLabel.setLocation(600, ABORT);
		contentPane.add(searchLabel);
		
		JScrollPane typeScroll = new JScrollPane();
		typeScroll.setBounds(593, 273, 100, 25);
		typeScroll.setVisible(false);
		contentPane.add(typeScroll);
		
		JList typeList = new JList();
		typeScroll.setViewportView(typeList);
		typeList.setVisibleRowCount(3);
		typeList.setModel(new AbstractListModel() {
			String[] values = new String[] {"(select)", "공공", "민간"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		JScrollPane districtScroll = new JScrollPane();
		districtScroll.setBounds(746, 273, 100, 25);
		districtScroll.setVisible(false);
		contentPane.add(districtScroll);
		
		JList districtList = new JList();
		districtScroll.setViewportView(districtList);
		districtList.setVisibleRowCount(5);
		districtList.setModel(new AbstractListModel() {
			String[] values = new String[] {"(select)", "마포구", "서대문구", "은평구", "종로구"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		resultScroll = new JScrollPane();
		resultScroll.setBounds(432, 326, 600, 365);
		contentPane.add(resultScroll);
		
		table = new JTable();
		tableModel = new DefaultTableModel(
		            new Object[][] {},
		            new String[] {"기관명", "주소", "전화번호"}
		        );
		table.setModel(tableModel);
		resultScroll.setViewportView(table);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		
		//전체 기관 검색
		total = new JButton("전체");
		total.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typeScroll.setVisible(false);
				districtScroll.setVisible(false);
				tableModel.setColumnIdentifiers(new String[] {"기관명", "주소", "전화번호"});
				tableModel.setRowCount(0);
				table.getColumnModel().getColumn(0).setPreferredWidth(100);
				table.getColumnModel().getColumn(1).setPreferredWidth(250);
				table.getColumnModel().getColumn(2).setPreferredWidth(150);
				Object[][] data = SearchGUI.getAllInstitution();
                for (Object[] row : data) {
                    tableModel.addRow(row);
                }
			}
		});
		total.setLocation(423, 212);
		contentPane.add(total);
		total.setBackground(new Color(0, 128, 0));
		total.setFont(new Font("한컴 말랑말랑 Regular", Font.BOLD, 18));
		total.setSize(130, 50);
		total.setFocusable(false);
		
		//타입별 기관 검색
		type = new JButton("기관 타입");
		type.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				districtScroll.setVisible(false);
				typeScroll.setVisible(true);
			}
		});
		type.setFont(new Font("한컴 말랑말랑 Regular", Font.BOLD, 18));
		type.setBackground(new Color(0, 128, 0));
		type.setBounds(578, 212, 130, 50);
		type.setFocusable(false);
		contentPane.add(type);
		
		//리스트에서 기관 타입을 선택하면 타입별로 검색됨
		typeList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				String selected = (String) typeList.getSelectedValue();
				if (!selected.equals("(select)") && selected != null) {
					tableModel.setColumnIdentifiers(new String[] {"기관명", "주소", "전화번호"});
					tableModel.setRowCount(0);
					table.getColumnModel().getColumn(0).setPreferredWidth(100);
					table.getColumnModel().getColumn(1).setPreferredWidth(250);
					table.getColumnModel().getColumn(2).setPreferredWidth(150);
					Object[][] data = SearchGUI.getInstitutionByType(selected);
	                for (Object[] row : data) {
	                    tableModel.addRow(row);
	                }
				} 
			}
		});
		
		//지역별 기관 검색
		district = new JButton("지역");
		district.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typeScroll.setVisible(false);
				districtScroll.setVisible(true);
			}
		});
		district.setFont(new Font("한컴 말랑말랑 Regular", Font.BOLD, 18));
		district.setFocusable(false);
		district.setBackground(new Color(0, 128, 0));
		district.setBounds(732, 213, 130, 50);
		contentPane.add(district);
		
		//리스트에서 지역을 선택하면 지역별로 검색됨
		districtList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				String selected = (String) districtList.getSelectedValue();
				if (!selected.equals("(select)") && selected != null) {
					tableModel.setColumnIdentifiers(new String[] {"기관명", "주소", "전화번호"});
					tableModel.setRowCount(0);
					table.getColumnModel().getColumn(0).setPreferredWidth(100);
					table.getColumnModel().getColumn(1).setPreferredWidth(250);
					table.getColumnModel().getColumn(2).setPreferredWidth(150);
					Object[][] data = SearchGUI.getInstitutionByDistrict(selected);
	                for (Object[] row : data) {
	                    tableModel.addRow(row);
	                }
				} 
			}
		});
		
		//평균 평점보다 기관의 평점이 높은 기관 조회
		rating = new JButton("평균 평점 이상");
		rating.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				typeScroll.setVisible(false);
				districtScroll.setVisible(false);
				
				resultScroll.setBounds(432, 326, 600, 365);
				tableModel.setColumnIdentifiers(new String[] {"기관명", "주소", "전화번호", "평점"});
				tableModel.setRowCount(0);
				table.getColumnModel().getColumn(0).setPreferredWidth(100);
				table.getColumnModel().getColumn(1).setPreferredWidth(250);
				table.getColumnModel().getColumn(2).setPreferredWidth(150);
				table.getColumnModel().getColumn(3).setPreferredWidth(50);
				Object[][] data = SearchGUI.getInstitutionByRating();
                for (Object[] row : data) {
                    tableModel.addRow(row);
                }
			}
		});
		rating.setFont(new Font("한컴 말랑말랑 Regular", Font.BOLD, 18));
		rating.setFocusable(false);
		rating.setBackground(new Color(0, 128, 0));
		rating.setBounds(885, 214, 162, 50);
		contentPane.add(rating);
		
		
		
	}
	
	
}
