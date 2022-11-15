package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 커넥션 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource; // 커넥션 획득을 위해 dataSource 필요
    private final MemberRepositoryV2 memberRepository;

    // 계좌 이체
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection conn = dataSource.getConnection();
        try {
            // 트랜잭션 시작
            conn.setAutoCommit(false);
            // 비즈니스 로직
            bizLogic(conn, fromId, toId, money);
            // 로직 정상 실행시, 커밋 수행
            conn.commit();
        } catch (Exception e) {
            conn.rollback(); // 예외 발생시, 롤백 수행
            throw new IllegalStateException(e);
        } finally {
            releaseConnection(conn);
        }

    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(conn, fromId);
        Member toMember = memberRepository.findById(conn, toId);
        memberRepository.update(conn, fromId, fromMember.getMoney() - money);
        validate(toMember); // 멤버 검증
        memberRepository.update(conn, toId, toMember.getMoney() + money);
    }

    private void validate(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true); // 커넥션 풀에 반납하기 전에 오토커밋 모드를 기본값으로 설정
                conn.close(); // 커넥션 풀에 반납
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
}
