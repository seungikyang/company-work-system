// 중학생 눈높이: 부서 정보
// Department는 여러 직원이 속하는 부서 카드입니다. 부서 이름과 설명을 담습니다.
// DB는 데이터를 보관하는 곳입니다. unique는 같은 값이 두 번 저장되지 않도록 하는 규칙입니다.
// 부서 이름·설명 읽기 → 만들기 → 수정하기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Department.java
// 목표: 부서 엔티티를 거의 백지에서 작성. TRD 3.3.3, 3.5.2.
// 막히면 starter/02-department-entity, answers.md 2장 참고.

// 초보자 안내 (02장): 테이블 제약과 관계
// 개념: unique 제약은 같은 값의 중복 저장을 DB에서 막는 규칙입니다.
// 사용 이유: 동일한 부서가 중복 생성되어 직원 정보가 나뉘는 일을 방지합니다.
// 동작 흐름: 부서 이름과 설명을 객체에 담고 저장합니다. 직원은 부서 ID를 참조합니다. 객체의 양방향 관계를 추가할지는 실제 조회 요구에 따라 결정합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// TODO 01: 엔티티 + 테이블 + 부서명 unique 제약.
@____
@____(
    name = "____",
    uniqueConstraints = {
        @____(name = "uk_departments_name", columnNames = "____")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Department {

    @____
    @GeneratedValue(strategy = GenerationType.____)
    private ____ id;

    // TODO 02: 부서명 길이(TRD 3.5.2).
    @Column(nullable = ____, length = ____)
    private ____ name;

    @Column(length = 255)
    private String description;

    @____
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @____
    private LocalDateTime updatedAt;

    // TODO 03: 정적 팩토리 — 부서명은 앞뒤 공백 제거(정규화는 도메인에서).
    public static Department of(String name, String description) {
        ____ d = new ____();
        d.name = name == null ? null : name.____();
        d.description = description;
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return d;
    }

    // TODO 04: 이름이 null/blank 면 변경하지 않는 가드.
    public void update(String name, String description) {
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (name != null && !name.____()) {
            this.name = name.____();
        }
        this.description = description;
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-02
//
// Q1. 사전 중복 검사 뒤에도 부서명 unique가 필요한 동시성 이유는? [chapter-v1-02-04]
// ① existsByName은 모든 다른 트랜잭션을 자동 잠근다.
// ② unique가 있으면 모든 입력 검증을 생략해야 한다.
// ③ 두 요청이 동시에 사전 검사를 통과해도 DB가 최종 중복 저장을 막아야 하기 때문이다.
// ④ 중복 검사는 화면에서만 해야 한다.
// 정답: ③
// 해설: 사전 조회와 INSERT 사이에 다른 요청이 들어올 수 있다. DB 제약과 충돌 오류 응답을 함께 다룬다.
//
// Q2. 부서명 trim 처리를 도메인 팩토리에 두는 이점은? [chapter-v1-02-05]
// ① trim이 대소문자와 모든 유니코드 표기를 자동 통일한다.
// ② 다른 생성 경로에서도 공백 정규화 규칙을 일관되게 적용한다.
// ③ Service에서 생성할 때만 공백이 제거된다.
// ④ trim은 DB unique 제약을 대신한다.
// 정답: ②
// 해설: 생성 규칙을 팩토리에 모으면 누락을 줄인다. 필요한 추가 정규화와 DB 제약은 별도로 설계한다.
//
// Q3. 부서명 수정의 중복 검사에서 자기 자신을 제외하는 이유는? [chapter-v1-02-07]
// ① 현재 부서와 다른 부서 모두의 중복 검사를 없애기 위해서다.
// ② 부서 ID가 같으면 DB 제약을 삭제하기 위해서다.
// ③ 부서명을 변경할 때마다 새 부서를 만들기 위해서다.
// ④ 현재 부서의 기존 이름을 유지하는 요청을 다른 부서와의 중복으로 오판하지 않기 위해서다.
// 정답: ④
// 해설: 이름이 같은 다른 ID의 부서가 있는지 검사한다. 본인의 행은 중복 대상에서 제외하되 DB unique는 유지한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ @Entity+@Table(departments)  □ uk_departments_name(name) unique  □ @Id+IDENTITY
// □ name length 100 not null  □ of()에서 trim  □ update() blank 가드
