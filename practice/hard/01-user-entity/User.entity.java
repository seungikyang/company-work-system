// 중학생 눈높이: 로그인 계정
// User는 로그인에 쓰는 계정 카드입니다. 이메일·이름·비밀번호의 해시·역할을 담습니다.
// Entity는 DB의 표와 연결되는 객체이고, enum은 선택할 수 있는 값을 미리 정한 목록입니다.
// 계정 만들기 → 필요한 값 넣기 → DB에 저장할 규칙 읽기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
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
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return user;
    }

    public void changePassword(String encodedPassword) {
        this.____ = ____;
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
// Q3. createdAt의 updatable=false와 JPA Auditing 설정의 역할은? [chapter-v1-01-08]
// ① updatable=false 하나만으로 현재 시각을 생성한다.
// ② 생성 시각의 일반 UPDATE 반영을 막고 리스너·활성화 설정으로 생성 시각을 채운다.
// ③ @CreatedDate를 붙이면 리스너와 활성화 설정은 항상 필요 없다.
// ④ createdAt은 조회마다 현재 시각으로 덮어써야 한다.
// 정답: ②
// 해설: updatable=false는 갱신 매핑 설정이고 Auditing은 값 입력 기능이다. 필드·리스너·@EnableJpaAuditing을 연결한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ UserRole 3값  □ @Entity+@Table(users)  □ @Id+IDENTITY  □ email unique
// □ password 255  □ @Enumerated(STRING)  □ @CreatedDate/@LastModifiedDate  □ create 팩토리
