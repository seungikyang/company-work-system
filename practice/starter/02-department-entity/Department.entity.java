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
        return d;
    }

    // null 확인 뒤 && 오른쪽을 평가하므로 null에 isBlank()를 호출하지 않습니다.
    // 이름을 생략한 요청과 이름을 빈 값으로 지우는 요청의 정책을 구분해서 읽으세요.
    public void update(String name, String description) {
        // TODO 04: 이름이 비어 있거나 null 이면 변경하지 않도록 가드를 두세요.
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

// 학습 질문 (면접 대비):
// Q1. 부서명 중복을 Service 의 existsByName 검사로 막는데도 DB unique 제약을 또 두는 이유는?
//     (동시 요청 race 를 그려 보세요 — 두 요청이 동시에 exists 통과 후 둘 다 insert)
//     A:
// Q2. of(...) 정적 팩토리에서 name.trim() 으로 공백을 제거하는데, 이걸 Service 가 아니라
//     도메인(엔티티)에서 하면 어떤 장점이 있을까요?
//     A:
// Q3. update(...) 가 name 이 blank 면 변경하지 않도록 가드를 둡니다. 이런 "부분 갱신" 의도라면
//     HTTP 메서드는 PUT 과 PATCH 중 무엇이 더 자연스러울까요? (25장과 연결)
//     A:
