// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Department.java
// 목표: 부서 엔티티를 거의 백지에서 작성. TRD 3.3.3, 3.5.2.
// 막히면 starter/02-department-entity, answers.md 2장 참고.

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
