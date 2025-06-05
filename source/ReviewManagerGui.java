import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;


public class ReviewManagerGui extends JFrame {
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ReviewManagerGui frame = new ReviewManagerGui();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ReviewManagerGui() {
        setTitle("리뷰 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 900);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 240));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel titleLabel = new JLabel("리뷰 관리 시스템");
        titleLabel.setFont(new Font("한컴 말랑말랑 Regular", Font.BOLD, 32));
        titleLabel.setBounds(400, 20, 400, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(titleLabel);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(50, 90, 1100, 50);
        buttonPanel.setLayout(new GridLayout(1, 6, 10, 0));
        buttonPanel.setOpaque(false);
        contentPane.add(buttonPanel);

        // 1. 전체 리뷰 조회
        JButton btnAll = createButton("전체 조회", e -> {
            setTableColumns(new String[]{"리뷰ID", "유저ID", "기관ID", "내용", "평점", "작성일", "기관평균", "기관순위"});
            Object[][] data = ReviewGUI.getAllReviews();
            updateTable(data);
        });
        buttonPanel.add(btnAll);

        // 2. 리뷰 등록
        JButton btnCreate = createButton("리뷰 등록", e -> {
            JTextField userIdField = new JTextField();
            JTextField institutionIdField = new JTextField();
            JTextField contentField = new JTextField();
            JTextField ratingField = new JTextField();
            Object[] message = {
                "유저ID:", userIdField,
                "기관ID:", institutionIdField,
                "리뷰 내용:", contentField,
                "평점(1~5):", ratingField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "리뷰 등록", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int userId = Integer.parseInt(userIdField.getText());
                    int institutionId = Integer.parseInt(institutionIdField.getText());
                    String content = contentField.getText();
                    int rating = Integer.parseInt(ratingField.getText());
                    boolean result = ReviewGUI.createReview(userId, institutionId, content, rating);
                    JOptionPane.showMessageDialog(this, result ? "등록 완료!" : "등록 실패!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
                }
            }
        });
        buttonPanel.add(btnCreate);

        // 3. 리뷰 수정
        JButton btnUpdate = createButton("리뷰 수정", e -> {
            JTextField reviewIdField = new JTextField();
            JTextField contentField = new JTextField();
            JTextField ratingField = new JTextField();
            Object[] message = {
                "리뷰ID:", reviewIdField,
                "새 내용:", contentField,
                "새 평점(1~5):", ratingField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "리뷰 수정", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    BigDecimal reviewId = new BigDecimal(reviewIdField.getText());
                    String content = contentField.getText();
                    int rating = Integer.parseInt(ratingField.getText());
                    boolean result = ReviewGUI.updateReview(reviewId, content, rating);
                    JOptionPane.showMessageDialog(this, result ? "수정 완료!" : "수정 실패!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
                }
            }
        });
        buttonPanel.add(btnUpdate);

        // 4. 리뷰 삭제
        JButton btnDelete = createButton("리뷰 삭제", e -> {
            String reviewIdStr = JOptionPane.showInputDialog(this, "삭제할 리뷰ID:");
            if (reviewIdStr != null) {
                try {
                    BigDecimal reviewId = new BigDecimal(reviewIdStr);
                    boolean result = ReviewGUI.deleteReview(reviewId);
                    JOptionPane.showMessageDialog(this, result ? "삭제 완료!" : "삭제 실패!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
                }
            }
        });
        buttonPanel.add(btnDelete);

        // 5. 기관별 리뷰 통계 조회
        JButton btnInstStats = createButton("기관별 통계", e -> {
            String instIdStr = JOptionPane.showInputDialog(this, "기관ID 입력:");
            if (instIdStr != null) {
                try {
                    int institutionId = Integer.parseInt(instIdStr);
                    setTableColumns(new String[]{"기관명", "기관평균", "리뷰수", "기관ID", "유저ID", "리뷰ID", "평점", "작성일", "내용"});
                    Object[][] data = ReviewGUI.getInstitutionStats(institutionId);
                    updateTable(data);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
                }
            }
        });
        buttonPanel.add(btnInstStats);

        // 6. 사용자별 리뷰 통계 조회
        JButton btnUserStats = createButton("사용자별 통계", e -> {
            String userIdStr = JOptionPane.showInputDialog(this, "사용자ID 입력:");
            if (userIdStr != null) {
                try {
                    BigDecimal userId = new BigDecimal(userIdStr);
                    setTableColumns(new String[]{"유저ID", "기관ID", "리뷰수", "평균평점", "사용자평균평점"});
                    Object[][] data = ReviewGUI.getUserStats(userId);
                    updateTable(data);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
                }
            }
        });
        buttonPanel.add(btnUserStats);

        // 결과 테이블
        JScrollPane resultScroll = new JScrollPane();
        resultScroll.setBounds(50, 170, 1100, 650);
        contentPane.add(resultScroll);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(50);
        
        resultScroll.setViewportView(table);
    }

    // 버튼 생성 유틸
    private JButton createButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("한컴 말랑말랑 Regular", Font.BOLD, 18));
        btn.setBackground(new Color(0, 128, 0));
        btn.setForeground(Color.WHITE);
        btn.setFocusable(false);
        btn.addActionListener(listener);
        return btn;
    }

    // 테이블 컬럼 세팅
    private void setTableColumns(String[] columns) {
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
    }

    // 테이블 데이터 업데이트
    private void updateTable(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
}
