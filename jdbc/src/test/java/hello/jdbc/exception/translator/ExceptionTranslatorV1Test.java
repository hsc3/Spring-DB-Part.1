package hello.jdbc.exception.translator;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.exception.MyDbException;
import hello.jdbc.repository.exception.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExceptionTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave() {
        service.createMember("myId");
        service.createMember("myId"); // 중복 ID 저장 시도
    }


    @RequiredArgsConstructor
    static class Service {

        private final Repository repository;

        public void createMember(String memberId) {
            try { // 회원 가입 시도
                repository.save(new Member(memberId, 0)); 
                log.info("회원 가입 ID={}", memberId); 
            } catch (MyDuplicateKeyException e) { // 중복 ID 문제 발생 시, 새로운 ID 생성 후 가입 시도
                log.info("중복 ID 존재, 복구 시도");
                String newId = memberId + new Random().nextInt(10000);
                log.info("newID={}", newId);
                repository.save(new Member(newId, 0));
            }
        }
    }


    /**
     * 회원 저장
     * DB에 존재하는 회원을 가입하고자 하면, MyDuplicateKeyException을 던진다.
     */
    @RequiredArgsConstructor
    static class Repository {

        private final DataSource dataSource;

        public Member save(Member member) {

            String sql = "insert into member(member_id, money) values (?, ?)";

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = dataSource.getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                if (e.getErrorCode() == 23505) // 키 중복 문제인 경우
                    throw new MyDuplicateKeyException(e);
                throw new MyDbException(e); // 그 외의 문제
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(conn);
            }
        }
    }
}
