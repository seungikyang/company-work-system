// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/User.java
// 목표: 로그인 계정용 User 엔티티와 UserRole enum 을 "거의 백지" 에서 작성. TRD 3.3.1, 3.6.1.
// 막히면 starter/01-user-entity, answers.md 1장, 루트 PRD/TRD 3.3.1·3.6.1 참고.

// 초보자 안내 (01장): Entity와 enum
// 개념: Entity는 DB에 저장할 대상을 표현하는 Java 객체이고, enum은 허용할 값을 이름으로 정한 타입입니다.
// 사용 이유: 로그인 계정의 저장 구조와 USER·MANAGER·ADMIN 같은 역할을 일관되게 표현합니다.
// 동작 흐름: User.create가 계정을 만들고 JPA 매핑이 users 테이블의 컬럼과 필드를 연결합니다. 비밀번호 필드에는 별도 인코더로 만든 해시를 전달합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// ===== UserRole enum =====
// TODO 01: 세 가지 역할.
public enum UserRole {
    ____, ____, ____
}

// ===== User entity =====
// TODO 02: 엔티티 선언 + 테이블명(예약어 충돌 회피) + Auditing 리스너.
@____
@____(name = "____")
@Getter
@NoArgsConstructor(access = AccessLevel.____)
@____(AuditingEntityListener.class)
public class User {

    // TODO 03: PK + 자동 증가 전략.
    @____
    @____(strategy = GenerationType.____)
    private ____ id;

    // TODO 04: 이메일 = unique + not null + 길이.
    @____(nullable = ____, length = ____, unique = ____)
    private ____ email;

    // TODO 05: 비밀번호 = 해시 결과를 담을 길이. 평문 길이가 아니라 무엇 기준으로 잡나?
    @Column(nullable = false, length = ____)
    private ____ password;

    @Column(nullable = false, length = 50)
    private String name;

    // TODO 06: enum 을 순서 변경에 안전하게 저장하는 방식.
    @____(EnumType.____)
    @Column(nullable = false, length = 20)
    private ____ role;

    // TODO 07: 생성/수정 시각 자동 채움. 생성시각은 수정 시 덮어쓰이면 안 된다.
    @____
    @Column(nullable = false, updatable = ____)
    private ____ createdAt;

    @____
    private LocalDateTime updatedAt;

    // ===== 정적 팩토리 =====
    // TODO 08: 비밀번호는 "이미 해시된 값" 을 받는다는 점에 주의(해시는 호출부 Service 책임).
    public static User create(String email, String encodedPassword, String name, UserRole role) {
        ____ user = new ____();
        user.email = ____;
        user.password = ____;
        user.name = ____;
        // role 이 null 이면 기본값.
        user.role = (role == null) ? UserRole.____ : role;
        return user;
    }

    public void changePassword(String encodedPassword) {
        this.____ = ____;
    }
}

// 학습 질문 (직접 답):
// Q1. User 와 Employee 를 왜 분리했나?
//     A:
// Q2. EnumType.STRING 대신 ORDINAL 을 쓰면 운영 중 무슨 사고가 나나?
//     A:
// Q3. createdAt 에 updatable=false 를 둔 이유 + @CreatedDate 가 동작하려면 어디에 무엇이 필요한가?
//     A:

// 자가 채점:
// □ UserRole 3값  □ @Entity+@Table(users)  □ @Id+IDENTITY  □ email unique
// □ password 255  □ @Enumerated(STRING)  □ @CreatedDate/@LastModifiedDate  □ create 팩토리
