package gift.repository;

import gift.entity.Member;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    private final JdbcClient jdbcClient;

    public MemberRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // 회원 저장
    public Member save(Member member) {
        String sql = "INSERT INTO members (email, password) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient
            .sql(sql)
            .param(member.getEmail())
            .param(member.getPassword())
            .update(keyHolder, "id");

        Long id = keyHolder.getKey().longValue();
        return new Member(id, member.getEmail(), member.getPassword());
    }

    public boolean existByEmail(String email) {
        String sql = "SELECT EXISTS (SELECT 1 FROM members WHERE email = ?)";
        return jdbcClient
            .sql(sql)
            .param(email)
            .query(Boolean.class)
            .single();
    }

    private Member mapRowToMember(ResultSet rs, int rowNum) throws SQLException {
        return new Member(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password")
        );
    }

    public Optional<Member> findById(Long id) {
        String sql = "SELECT id, email, password FROM members WHERE id = ?";
        return jdbcClient
            .sql(sql)
            .param(id)
            .query(this::mapRowToMember)
            .optional();
    }

    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, email, password FROM members WHERE email = ?";
        return jdbcClient
            .sql(sql)
            .param(email)
            .query(this::mapRowToMember)
            .optional();
    }

    public boolean isAdminMember(Member member) {
        String sql = "SELECT is_admin FROM members WHERE email = ?";
        return jdbcClient
            .sql(sql)
            .param(member.getEmail())
            .query(Boolean.class)
            .single();
    }
}
