// 중학생 눈높이: 부서 정보
// Department는 여러 직원이 속하는 부서 카드입니다. 부서 이름과 설명을 담습니다.
// DB는 데이터를 보관하는 곳입니다. unique는 같은 값이 두 번 저장되지 않도록 하는 규칙입니다.
// 부서 이름·설명 읽기 → 만들기 → 수정하기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Department.java
// 목표: 부서 엔티티를 채우세요. TRD 3.3.3, 3.5.2 참고.

// 초보자 안내 (02장): 테이블 제약과 관계
// 개념: unique 제약은 같은 값의 중복 저장을 DB에서 막는 규칙입니다.
// 사용 이유: 동일한 부서가 중복 생성되어 직원 정보가 나뉘는 일을 방지합니다.
// 동작 흐름: 부서 이름과 설명을 객체에 담고 저장합니다. 직원은 부서 ID를 참조합니다. 객체의 양방향 관계를 추가할지는 실제 조회 요구에 따라 결정합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Entity
@Table(
    name = "departments",
    uniqueConstraints = {
        // TODO 01: 부서명이 중복되지 않도록 unique 제약을 선언하세요.
        @UniqueConstraint(name = "uk_departments_name", columnNames = "____")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 02: 부서명 컬럼 길이를 TRD 3.5.2 에 맞춰 채우세요.
    @Column(nullable = false, length = ____)
    private String name;

    @Column(length = 255)
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Department of(String name, String description) {
        Department d = new Department();
        // TODO 03: 부서명은 앞뒤 공백을 제거하는 것이 안전합니다.
        d.name = name == null ? null : name.____();
        d.description = description;
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return d;
    }

    // null 확인 뒤 && 오른쪽을 평가하므로 null에 isBlank()를 호출하지 않습니다.
    // 이름을 생략한 요청과 이름을 빈 값으로 지우는 요청의 정책을 구분해서 읽으세요.
    public void update(String name, String description) {
        // TODO 04: 이름이 비어 있거나 null 이면 변경하지 않도록 가드를 두세요.
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
        this.description = description;
    }

    // 학습 메모:
    // - Department → List<Employee> 양방향을 둘지 단방향으로 둘지 트레이드오프를 적어 보세요.
    // - 부서 삭제 시 소속 직원이 있으면 어떻게 처리할지(거부 vs 부서 NULL 처리 vs 기본 부서로 이동)
    //   를 비즈니스 규칙으로 정해 두는 것이 좋습니다.
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
// Q3. 부서 수정에서 제공된 필드만 바꾸려는 의도를 나타내는 HTTP 메서드는? [chapter-v1-02-06]
// ① 부분 변경 계약의 PATCH를 사용한다.
// ② 자원 읽기 계약의 GET을 사용한다.
// ③ 자원 삭제 계약의 DELETE를 사용한다.
// ④ 폼 입력에 빈 값이 있으면 PUT도 항상 부분 변경을 뜻한다.
// 정답: ①
// 해설: PATCH는 부분 변경을 위한 메서드다. 생략·null·공백의 처리 의미도 요청 계약에 명시한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
