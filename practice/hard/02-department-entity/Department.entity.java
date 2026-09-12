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
        return d;
    }

    // TODO 04: 이름이 null/blank 면 변경하지 않는 가드.
    public void update(String name, String description) {
        if (name != null && !name.____()) {
            this.name = name.____();
        }
        this.description = description;
    }
}

// 학습 질문 (직접 답):
// Q1. Service 의 existsByName 검사가 있는데도 DB unique 를 또 두는 이유는? (동시 등록 race)
//     A:
// Q2. trim 정규화를 Service 가 아니라 도메인 팩토리에 두면 무엇이 좋아지나?
//     A:
// Q3. 부서명 변경 시 중복검사를 "자기 자신 제외" 로 해야 하는 이유는?
//     A:

// 자가 채점:
// □ @Entity+@Table(departments)  □ uk_departments_name(name) unique  □ @Id+IDENTITY
// □ name length 100 not null  □ of()에서 trim  □ update() blank 가드
