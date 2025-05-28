import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.Scanner;

//일단 제 기준 돌아는 갑니다,,,
//TABLE은 별도 자바 파일 써서 넣었고 CSV 파일은 자바로 하는게 전 에러가 많아서,,, CMD로 넣었습니다
//하다보니까 INSTITUTION_ID 등 꽤 속성을 뭘로 맞출건지 안 정했더라고요, 애매한 감이 있어서 일단 INT로 했는데 회의 후 변경되면 수정하겠습니다
public class InstitutionReview {
    static final String DB_URL = "jdbc:mysql://localhost:3306/dbyte_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    static final String DB_USER = "testuser";
    static final String DB_PASSWORD = "testpw";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n==== 리뷰 관리 시스템 ====");
            System.out.println("1. 리뷰 전체 조회 (기관 평균 평점 및 순위 포함)");
            System.out.println("2. 리뷰 등록");
            System.out.println("3. 리뷰 수정");
            System.out.println("4. 리뷰 삭제");
            System.out.println("5. 기관별 리뷰 통계 조회 ");
            System.out.println("6. 사용자별 리뷰 통계 조회");
            System.out.println("7. 기관 평균 평점 조회");
            System.out.println("0. 종료");
            System.out.print("선택: ");
            int menuChoice = scanner.nextInt();
            scanner.nextLine();

            switch (menuChoice) {
                case 1: selectAllReviews(); break;
                case 2: createReview(scanner); break;
                case 3: updateReview(scanner); break;
                case 4: deleteReview(scanner); break;
                case 5: selectByInstitutionGroup(scanner); break;
                case 6: selectByUserGroup(scanner); break;
                case 7: showAverageRating(scanner); break;
                case 0:
                    System.out.println("종료합니다.");
                    scanner.close();
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }

    // 평점 검증
  //평점은 정수로만 받았습니다
    private static boolean validateRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    // 1. 전체 조회 랭크 넣는건 여기가 나을 거 같아서 윈도우 OLAP 사용했습니다.
    static void selectAllReviews() {
        String sql =
            "WITH review_with_avg AS (" +
            "    SELECT review_id, user_id, institution_id, content, rating, " +
            "           DATE_FORMAT(date, '%Y-%m-%d') AS formatted_date, " +
            "           AVG(rating) OVER (PARTITION BY institution_id) AS institution_avg " +
            "      FROM Reviews" +
            ") " +
            "SELECT review_id, user_id, institution_id, content, rating, formatted_date, " +
            "       institution_avg, " +
            "       RANK() OVER (ORDER BY institution_avg DESC) AS institution_rank " +
            "  FROM review_with_avg " +
            "ORDER BY institution_avg DESC, formatted_date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            printReviewResultsWithWindow(rs, "[전체 리뷰 + 기관평균평점/순위]");
        } catch (SQLException e) {
            handleSQLException("조회", e);
        }
    }

    // 2. 등록
    static void createReview(Scanner scanner) {
        try {
            System.out.print("유저ID: ");
            BigDecimal userId = scanner.nextBigDecimal();
            System.out.print("기관ID: ");
            int institutionId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("리뷰 내용: ");
            String content = scanner.nextLine().trim();
            System.out.print("평점(1~5): ");
            int rating = scanner.nextInt();

            if (!validateRating(rating)) {
                System.out.println("평점은 1~5 사이 정수로 골라주세요.");
                return;
            }
            if (content.isEmpty()) {
                System.out.println("리뷰 내용을 입력해주세요.");
                return;
            }

            String sql = "INSERT INTO Reviews (user_id, institution_id, content, rating) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBigDecimal(1, userId);
                pstmt.setInt(2, institutionId);
                pstmt.setString(3, content);
                pstmt.setInt(4, rating);
                int result = pstmt.executeUpdate();
                System.out.println(result + "건 등록 완료!");
            } catch (SQLException e) {
                handleSQLException("등록", e);
            }
        } catch (Exception e) {
            handleInputException(e);
        }
    }

    // 3. 수정(UPDATE)
    static void updateReview(Scanner scanner) {
        try {
            System.out.print("수정할 리뷰번호(review_id): ");
            BigDecimal reviewId = scanner.nextBigDecimal();
            scanner.nextLine();
            System.out.print("새 내용: ");
            String newContent = scanner.nextLine().trim();
            System.out.print("새 평점(1~5): ");
            int newRating = scanner.nextInt();
            if (!validateRating(newRating)) {
                System.out.println("평점은 1~5 사이여야 합니다.");
                return;
            }
            if (newContent.isEmpty()) {
                System.out.println("리뷰 내용을 입력해주세요.");
                return;
            }
            String sql = "UPDATE Reviews SET content=?, rating=? WHERE review_id=?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newContent);
                pstmt.setInt(2, newRating);
                pstmt.setBigDecimal(3, reviewId);
                int result = pstmt.executeUpdate();
                System.out.println(result > 0 ? "수정이 완료되었습니다!" : "해당 리뷰가 없습니다.");
            } catch (SQLException e) {
                handleSQLException("수정", e);
            }
        } catch (Exception e) {
            handleInputException(e);
        }
    }

    // 4. 삭제
    static void deleteReview(Scanner scanner) {
        try {
            System.out.print("삭제할 리뷰번호(review_id): ");
            BigDecimal reviewId = scanner.nextBigDecimal();

            String sql = "DELETE FROM Reviews WHERE review_id=?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBigDecimal(1, reviewId);
                int result = pstmt.executeUpdate();
                System.out.println(result > 0 ? "삭제가 정상적으로 처리되었습니다." : "해당 리뷰가 없습니다.");
            } catch (SQLException e) {
                handleSQLException("삭제", e);
            }
        } catch (Exception e) {
            handleInputException(e);
        }
    }

    // 5. 기관별 리뷰 통계 조회 (GROUP BY/HAVING)
    static void selectByInstitutionGroup(Scanner scanner) {
        try {
            System.out.print("기관ID: ");
            int institutionId = scanner.nextInt();

            String sql =
                "SELECT institution_id, user_id, COUNT(*) AS review_count, AVG(rating) AS avg_rating " +
                "  FROM Reviews " +
                " WHERE institution_id=? " +
                "GROUP BY institution_id, user_id " +
                "HAVING COUNT(*) >= 1 " +
                "ORDER BY avg_rating DESC";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, institutionId);
                ResultSet rs = pstmt.executeQuery();
                System.out.println("\n[기관별 사용자 리뷰 통계]");
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    System.out.printf("기관:%d | 사용자:%d | 리뷰수:%d | 평균평점:%.2f\n",
                        rs.getInt("institution_id"),
                        rs.getBigDecimal("user_id").intValue(),
                        rs.getInt("review_count"),
                        rs.getDouble("avg_rating"));
                }
                if (!hasData) System.out.println("조회 결과가 없습니다.");
            } catch (SQLException e) {
                handleSQLException("기관별 통계 조회", e);
            }
        } catch (Exception e) {
            handleInputException(e);
        }
    }

    // 6. 사용자별 리뷰 통계 조회 (GROUP BY/HAVING)
    static void selectByUserGroup(Scanner scanner) {
        try {
            System.out.print("사용자ID: ");
            BigDecimal userId = scanner.nextBigDecimal();
            String sql =
                "SELECT user_id, institution_id, COUNT(*) AS review_count, AVG(rating) AS avg_rating " +
                "  FROM Reviews " +
                " WHERE user_id=? " +
                "GROUP BY user_id, institution_id " +
                "HAVING COUNT(*) >= 1 " +
                "ORDER BY avg_rating DESC";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBigDecimal(1, userId);
                ResultSet rs = pstmt.executeQuery();
                System.out.println("\n[사용자별 기관 리뷰 통계]");
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    System.out.printf("사용자:%d | 기관:%d | 리뷰수:%d | 평균평점:%.2f\n",
                        rs.getBigDecimal("user_id").intValue(),
                        rs.getInt("institution_id"),
                        rs.getInt("review_count"),
                        rs.getDouble("avg_rating"));
                }
                if (!hasData) System.out.println("조회 결과가 없습니다.");
            } catch (SQLException e) {
                handleSQLException("사용자별 통계 조회", e);
            }
        } catch (Exception e) {
            handleInputException(e);
        }
    }

    // 7. 기관 평균 조회 
    //조인 사용해서 원하는 기관의 평균 평점만 조회했습니다
    static void showAverageRating(Scanner scanner) {
        try {
            System.out.print("기관ID: ");
            int institutionId = scanner.nextInt();

            String sql =
                "SELECT i.name, r.institution_id, AVG(r.rating) AS average_rating " +
                "  FROM Reviews r " +
                "  JOIN Institution i ON r.institution_id = i.institution_id " +
                " WHERE r.institution_id = ? " +
                " GROUP BY r.institution_id, i.name";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, institutionId);
                ResultSet rs = pstmt.executeQuery();

                System.out.println("\n[기관 평균 평점 조회]");
                if (rs.next()) {
                    System.out.printf(
                        " 기관명: %s | 기관ID: %d | 평균평점: %.2f\n",
                        rs.getString("name"),
                        rs.getInt("institution_id"),
                        rs.getDouble("average_rating")
                    );
                } else {
                    System.out.println("해당 기관이 존재하지 않거나 리뷰가 없습니다.");
                }
            } catch (SQLException e) {
                handleSQLException("평균 평점 조회", e);
            }
        } catch (Exception e) {
            handleInputException(e);
        }
    }



    // 공통 출력 부분
    private static void printReviewResultsWithWindow(ResultSet rs, String title) throws SQLException {
        System.out.println("\n" + title);
        boolean hasData = false;
        while (rs.next()) {
            hasData = true;
            System.out.printf("번호:%d | 유저:%d | 기관:%d | 평점:%d | 내용:%s | 작성일:%s | 기관평균:%.2f | 기관순위:%d\n",
                rs.getBigDecimal("review_id").intValue(),
                rs.getBigDecimal("user_id").intValue(),
                rs.getInt("institution_id"),
                rs.getInt("rating"),
                rs.getString("content"),
                rs.getString("formatted_date"),
                rs.getDouble("institution_avg"),
                rs.getInt("institution_rank"));
        }
        if (!hasData) {
            System.out.println("조회 결과가 없습니다.");
        }
    }

    // 예외 처리 메소드들
    private static void handleSQLException(String operation, SQLException e) {
        System.out.println(operation + " 작업 실패: " + e.getMessage());
    }

    private static void handleInputException(Exception e) {
        System.out.println("입력 오류: " + e.getMessage());
    }
}

