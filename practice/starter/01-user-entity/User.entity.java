// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/User.java
// 목표: 로그인 계정용 User 엔티티와 UserRole enum 을 채우세요.
//       TRD 3.3.1, 3.6.1 참고.

// ===== UserRole enum =====
public enum UserRole {
    // TODO 01: TRD 3.6.1 에서 정의한 세 가지 역할을 채우세요.
    ____, ____, ____
}

// ===== User entity =====
// TODO 02: 이 클래스가 JPA Entity 임을 알리는 어노테이션은?
@____
// TODO 03: 테이블명을 명시하세요. (예약어 충돌 방지)
@Table(name = "____")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    // TODO 04: PK 와 자동 증가 전략을 선언하세요.
    @____
    @GeneratedValue(strategy = GenerationType.____)
    private Long id;

    // TODO 05: 이메일은 unique + not null
    @Column(nullable = false, length = 100, unique = ____)
    private String email;

    // TODO 06: 비밀번호는 해시 결과를 담을 수 있는 충분한 길이로
    @Column(nullable = false, length = ____)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    // TODO 07: enum 을 DB 에 어떻게 저장하면 enum 순서 변경에도 안전할까요?
    @Enumerated(EnumType.____)
    @Column(nullable = false, length = 20)
    private UserRole role;

    // TODO 08: 자동으로 생성/수정 시각을 채워주는 Spring Data JPA Auditing 어노테이션을 적으세요.
    @____
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @____
    private LocalDateTime updatedAt;

    // ===== 정적 팩토리 =====
    public static User create(String email, String encodedPassword, String name, UserRole role) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.name = name;
        // TODO 09: role 이 null 이면 기본값을 USER 로 두는 식으로 안전망을 만들어 보세요.
        user.role = (role == null) ? UserRole.____ : role;
        return user;
    }

    public void changePassword(String encodedPassword) {
        // TODO 10: 평문이 들어올 가능성을 한 번 더 막을 방어 코드를 추가해도 좋습니다.
        this.password = ____;
    }
}

// 학습 질문 (면접 대비):
// Q1. 로그인 계정(User)과 인사 정보(Employee)를 한 테이블이 아니라 둘로 나눈 이유는? (면접 Q3)
//     A:
// Q2. @Enumerated(EnumType.STRING) 대신 ORDINAL 을 쓰면 운영 중 enum 에 값을 추가/재정렬할 때
//     어떤 사고가 날 수 있을까요?
//     A:
// Q3. password 컬럼 길이를 100 이 아니라 255 로 잡는 이유는? (BCrypt 결과 길이와 연결해서)
//     A:
// Q4. createdAt 을 updatable=false 로 둔 이유, 그리고 @CreatedDate 가 동작하려면
//     설정 어디에 무엇이 켜져 있어야 하나요? (33장 @EnableJpaAuditing 와 연결)
//     A:
