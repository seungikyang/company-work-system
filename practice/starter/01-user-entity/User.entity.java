// 중학생 눈높이: 로그인 계정
// User는 로그인에 쓰는 계정 카드입니다. 이메일·이름·비밀번호의 해시·역할을 담습니다.
// Entity는 DB의 표와 연결되는 객체이고, enum은 선택할 수 있는 값을 미리 정한 목록입니다.
// 계정 만들기 → 필요한 값 넣기 → DB에 저장할 규칙 읽기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/User.java
// 목표: 로그인 계정용 User 엔티티와 UserRole enum 을 채우세요.
//       TRD 3.3.1, 3.6.1 참고.

// 초보자 안내 (01장): Entity와 enum
// 개념: Entity는 DB에 저장할 대상을 표현하는 Java 객체이고, enum은 허용할 값을 이름으로 정한 타입입니다.
// 사용 이유: 로그인 계정의 저장 구조와 USER·MANAGER·ADMIN 같은 역할을 일관되게 표현합니다.
// 동작 흐름: User.create가 계정을 만들고 JPA 매핑이 users 테이블의 컬럼과 필드를 연결합니다. 비밀번호 필드에는 별도 인코더로 만든 해시를 전달합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
    // static 팩토리는 객체를 만드는 이름 있는 입구입니다. new User()로 만든 메모리 객체에 값을 넣으며,
    // 이 메서드 자체는 DB에 INSERT하지 않습니다. 이후 Repository 저장과 트랜잭션을 따라가세요.
    public static User create(String email, String encodedPassword, String name, UserRole role) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.name = name;
        // TODO 09: role 이 null 이면 기본값을 USER 로 두는 식으로 안전망을 만들어 보세요.
        user.role = (role == null) ? UserRole.____ : role;
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return user;
    }

    // this는 현재 User 객체입니다. 인자는 이미 해시한 비밀번호여야 하며, 단순 대입이 해시를 만들지는 않습니다.
    public void changePassword(String encodedPassword) {
        // TODO 10: 평문이 들어올 가능성을 한 번 더 막을 방어 코드를 추가해도 좋습니다.
        this.password = ____;
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-01
//
// Q1. 로그인 계정과 인사 정보를 분리하는 설계의 이점은? [chapter-v1-01-05]
// ① 계정과 직원의 관계를 검증하지 않아도 된다.
// ② 모든 직원이 같은 비밀번호를 공유하게 한다.
// ③ 인증·권한과 인사 정보의 책임·변경 주기를 나누고 명시적인 관계로 연결한다.
// ④ 테이블이 둘이면 개인정보 노출이 자동으로 불가능해진다.
// 정답: ③
// 해설: User는 인증 정보를, Employee는 인사 정보를 맡는다. 분리 뒤에도 관계 무결성과 공개 필드 정책을 검증한다.
//
// Q2. enum을 ORDINAL로 저장한 뒤 선언 순서를 바꾸면 생길 수 있는 문제는? [chapter-v1-01-06]
// ① 숫자 저장값이 항상 이름으로 자동 이관된다.
// ② ORDINAL이 모든 순서 변경을 거부한다.
// ③ 역할을 재정렬하면 사용자 행이 모두 자동 삭제된다.
// ④ 기존 숫자가 다른 enum 상수를 가리켜 역할의 의미가 바뀔 수 있다.
// 정답: ④
// 해설: ORDINAL은 순서에 의존한다. STRING은 순서 변경에 안전하지만 상수 이름 변경에는 데이터 이관을 검토한다.
//
// Q3. 비밀번호 해시 컬럼 길이를 255로 잡는 판단으로 알맞은 것은? [chapter-v1-01-07]
// ① BCrypt 결과를 수용하고 인코더 식별자·향후 해시 형식 변경도 고려한 여유를 둔다.
// ② BCrypt 결과는 반드시 255자여야 한다.
// ③ 해시 컬럼이 길수록 BCrypt 계산 강도가 자동 증가한다.
// ④ 255자 컬럼이면 평문 비밀번호 저장도 안전하다.
// 정답: ①
// 해설: 일반적인 BCrypt 문자열은 60자이므로 100자도 수용한다. 255자는 저장 형식의 확장을 고려한 선택이며 필수 BCrypt 길이는 아니다.
//
// Q4. createdAt의 updatable=false와 JPA Auditing 설정의 역할은? [chapter-v1-01-08]
// ① updatable=false 하나만으로 현재 시각을 생성한다.
// ② 생성 시각의 일반 UPDATE 반영을 막고 리스너·활성화 설정으로 생성 시각을 채운다.
// ③ @CreatedDate를 붙이면 리스너와 활성화 설정은 항상 필요 없다.
// ④ createdAt은 조회마다 현재 시각으로 덮어써야 한다.
// 정답: ②
// 해설: updatable=false는 갱신 매핑 설정이고 Auditing은 값 입력 기능이다. 필드·리스너·@EnableJpaAuditing을 연결한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
