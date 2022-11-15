package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.exception.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제 해결
 * 체크 예외 -> 런타임 예외로 변경 (throw new MyDbException(e);)
 * MemberRepository 사용
 * throws SQLException 제거
 */
@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 생성
    @Override
    public Member save(Member member){

        String sql = "insert into member(member_id, money) values(?, ?)"; // 파라미터 바인딩
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // DB 연결 -> 파라미터 바인딩 SQL 전달 -> DB 쿼리 적용
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }

    }
    
    // 조회
    @Override
    public Member findById(String memberId) {

        String sql = "select * from member where member_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery(); // 테이블에서 조회된 row 반환

            if (rs.next()) { // 커서를 한 번은 이동해야 한다.
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;

            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    // 수정
    @Override
    public void update(String memberId, int money) {

        String sql = "update member set money=? where member_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); // 쿼리의 영향을 받은 row 수
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    // 삭제
    @Override
    public void delete(String memberId) {

        String sql = "delete from member where member_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    private Connection getConnection() throws SQLException {
        Connection conn = DataSourceUtils.getConnection(dataSource); // 트랜잭션 동기화
        log.info("get connection={}, class={}", conn, conn.getClass());
        return conn;
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(conn, dataSource); // 트랜잭션 동기화
    }
}
