package hello.jdbc.repository;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

// JDBC - DriverManger 사용
@Slf4j
public class MemberRepositoryV0 {

    // 생성
    public Member save(Member member) throws SQLException {

        String sql = "insert into member(member_id, money) values(?, ?)"; // 파라미터 바인딩
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // DB 연결 -> 파라미터 바인딩 SQL 전달 -> DB 쿼리 적용
            connection = DBConnectionUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate();
            return member;

        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }

    }
    
    // 조회
    public Member findById(String memberId) throws SQLException {

        String sql = "select * from member where member_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnectionUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            resultSet = preparedStatement.executeQuery(); // 테이블에서 조회된 row 반환

            if (resultSet.next()) { // 커서를 한 번은 이동해야 한다.
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));
                return member;

            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    // 수정
    public void update(String memberId, int money) throws SQLException {

        String sql = "update member set money=? where member_id=?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnectionUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int resultSize = preparedStatement.executeUpdate(); // 쿼리의 영향을 받은 row 수
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    // 삭제
    public void delete(String memberId) throws SQLException {

        String sql = "delete from member where member_id=?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnectionUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            log.error("DB Error", e);
            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }

    }


    // 리소스 해제 (역순)
    private void close(Connection connection, Statement statement, ResultSet resultSet) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (connection != null ) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }


}
