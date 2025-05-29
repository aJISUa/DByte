// 트래킹 기능 구현을 위한 클래스 파일입니다. 

import java.sql.Timestamp;
import java.util.Scanner;
import java.sql.*;

public class Tracking {
	static final String DB_URL = "jdbc:mysql://localhost:3306/dbyte?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    static final String DB_USER = "testuser";
    static final String DB_PASSWORD = "testpw";
    
	public Scanner input = new Scanner(System.in);
	public int userId;
	public Timestamp date;
	private int institutionId;
	private int feeling;
	private int sleeping;
	private String exerciseName;
	private double exerciseTime;
	private String comment;
	
	public Tracking() {}
	
	public Tracking(int userId, int institutionId) {
		this.userId = userId;
		this.institutionId = institutionId; 
		this.feeling = 0;
		this.sleeping = 0;
		this.setExerciseName(null);
		this.setComment(null);
	}
	
	public Tracking(int userId, int institutionId, Timestamp date, int feeling, int sleeping, String exerciseName, double exerciseTime, String comment) {
		this.userId = userId;
		this.institutionId = institutionId; 
		this.date = date;
		this.feeling = feeling;
		this.sleeping = sleeping;
		this.exerciseName = exerciseName;
		this.exerciseTime = exerciseTime;
		this.comment = comment;
	}
	
	public void setDate() {
		this.date = new Timestamp(System.currentTimeMillis());
	}
	
	public int getInstitutionId() {
		return institutionId;
	}
	public void setInstitutionId(int institutionId) {
		this.institutionId = institutionId;
	}
	public int getFeeling() {
		return feeling;
	}
	public void setFeeling(int feeling) {
		this.feeling = feeling;
	}
	public int getSleeping() {
		return sleeping;
	}
	public void setSleeping(int sleeping) {
		this.sleeping = sleeping;
	}
	public String getExerciseName() {
		return exerciseName;
	}
	public void setExerciseName(String exerciseName) {
		this.exerciseName = exerciseName;
	}
	public double getExerciseTime() {
		return exerciseTime;
	}
	public void setExerciseTime(double exerciseTime) {
		this.exerciseTime = exerciseTime;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void registerTracking() {
		setDate();
		System.out.print("오늘의 수면시간을 적어주세요: ");
		setSleeping(input.nextInt());
		System.out.print("오늘의 감정은 몇 점인가요 (1-100): ");
		setFeeling(input.nextInt());
		System.out.print("오늘 운동을 하셨나요? (y/n): ");
		input.nextLine();
		String check = input.nextLine();
		if (check.equals("y")) {
			System.out.print("하신 운동을 적어주세요: ");
			setExerciseName(input.nextLine());
			System.out.print("운동 시간을 적어주세요: ");
			setExerciseTime(input.nextDouble());
			input.nextLine(); // flush
		} else {
			System.out.println("운동 시간을 기록하지 않습니다.");
		}
		System.out.print("등록하고 싶은 한 문장이 있으신가요? (y/n): ");
		check = input.nextLine();
		if (check.equals("y")) {
			System.out.print("오늘의 한 문장을 적어주세요: ");
			this.comment = input.nextLine();
		} else {
			System.out.println("별도의 코멘트를 기록하지 않습니다.");
		}
	}

	public void checkTracking() {
		System.out.println("\n=======< Tracking을 불러옵니다 >=======");
		System.out.println("기록 일자: " + this.date);
		System.out.println("수면 시간: " + this.sleeping);
		System.out.println("오늘의 감정 점수: " + this.feeling);
		System.out.print("오늘의 운동: ");
		if (this.getExerciseName() == null) {
			System.out.println("-");
		} else {
			System.out.println(this.getExerciseName());
			System.out.println("운동시간: " + this.getExerciseTime());
		}
		System.out.print("오늘의 한 문장: ");
		if (this.getComment() == null) {
			System.out.println("-");
		} else {
			System.out.println(this.getComment());
		}
	}

	public void insertTracking() {
	    setDate(); // 현재 시간 설정
	    String sql = "INSERT INTO tracking (user_id, institution_id, date, feeling, sleeping, exercise_name, exercise_time, comment) "
	               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	    
	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, userId);
	        pstmt.setInt(2, institutionId);
	        pstmt.setTimestamp(3, date);
	        pstmt.setInt(4, feeling);
	        pstmt.setInt(5, sleeping);
	        pstmt.setString(6, exerciseName);
	        pstmt.setDouble(7, exerciseTime);
	        pstmt.setString(8, comment);
	        
	        pstmt.executeUpdate();
	        System.out.println("트래킹 정보가 성공적으로 저장되었습니다.");
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public void updateTracking() {
	    String sql = "UPDATE tracking SET feeling = ?, sleeping = ?, exercise_name = ?, exercise_time = ?, comment = ? "
	               + "WHERE user_id = ? AND institution_id = ? AND date = ?";
	    
	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, feeling);
	        pstmt.setInt(2, sleeping);
	        pstmt.setString(3, exerciseName);
	        pstmt.setDouble(4, exerciseTime);
	        pstmt.setString(5, comment);
	        pstmt.setInt(6, userId);
	        pstmt.setInt(7, institutionId);
	        pstmt.setTimestamp(8, date);

	        int rows = pstmt.executeUpdate();
	        if (rows > 0) {
	            System.out.println("트래킹 정보가 성공적으로 수정되었습니다.");
	        } else {
	            System.out.println("수정할 트래킹 정보가 없습니다.");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public void deleteTracking() {
	    String sql = "DELETE FROM tracking WHERE user_id = ? AND institution_id = ? AND date = ?";
	    
	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, userId);
	        pstmt.setInt(2, institutionId);
	        pstmt.setTimestamp(3, date);

	        int rows = pstmt.executeUpdate();
	        if (rows > 0) {
	            System.out.println("트래킹 정보가 성공적으로 삭제되었습니다.");
	        } else {
	            System.out.println("삭제할 트래킹 정보가 없습니다.");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void analyzeSleepWithRollup() {
	    String sql = "SELECT user_id, AVG(sleeping) AS avg_sleeping " +
	                 "FROM tracking " +
	                 "GROUP BY user_id WITH ROLLUP";

	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        System.out.println("\n===== 사용자별 평균 수면 시간 (ROLLUP 포함) =====");
	        System.out.printf("%-12s | %-15s\n", "user_id", "avg_sleeping");
	        System.out.println("---------------------------------------");

	        while (rs.next()) {
	            int userId = rs.getInt("user_id");
	            double avgSleep = rs.getDouble("avg_sleeping");

	            if (rs.wasNull()) {
	                System.out.printf("%-12s | %-15.2f\n", "전체 평균", avgSleep);
	            } else {
	                System.out.printf("%-12d | %-15.2f\n", userId, avgSleep);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}
