// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/ApprovalDocument.java
// 목표: 전자결재 문서 엔티티를 채우세요. TRD 3.3.6, 3.6.4, 3.8.4 참고.
//
// 상태 전이 그래프(직접 그려보세요):
//   ____  →  ____  →  ____
//                  ↘  ____
//   (그 외 전이는 모두 차단)

// 초보자 안내 (06장): 상태 전이와 도메인 메서드
// 개념: 상태 전이는 DRAFT 같은 현재 단계에서 다음 단계로 이동하는 규칙입니다.
// 사용 이유: 임시 문서를 바로 승인하거나 완료 문서를 다시 처리하는 일을 막습니다.
// 동작 흐름: 문서는 DRAFT → PENDING → APPROVED 또는 REJECTED 순서로 바뀝니다. 도메인 메서드가 현재 상태와 행위자를 확인한 후 값을 바꿉니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Entity
@Table(name = "approval_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ApprovalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 01: 작성자(직원 FK)
    @Column(name = "writer_id", nullable = false)
    private Long ____;

    // TODO 02: 결재자(직원 FK)
    @Column(name = "approver_id", nullable = false)
    private Long ____;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    // TODO 03: 결재 문서 상태는 DRAFT 로 시작하고 결재 요청 시 PENDING 으로 전이됩니다.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStatus status;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // TODO 04: 승인된 시점만 채워집니다. 그래서 nullable.
    @Column(name = "approved_at")
    private LocalDateTime ____;

    public static ApprovalDocument createDraft(Long writerId, Long approverId, String title, String content) {
        // TODO 05: 작성자와 결재자가 같으면 안 됩니다.
        //          이 검증을 Entity 에 둘지 Service 에 둘지 트레이드오프를 생각해 보세요.
        if (writerId != null && writerId.____(approverId)) {
            throw new IllegalArgumentException("작성자와 결재자는 같을 수 없습니다.");
        }

        ApprovalDocument doc = new ApprovalDocument();
        doc.writerId = writerId;
        doc.approverId = approverId;
        doc.title = title;
        doc.content = content;
        // TODO 06: 새 문서의 초기 상태는?
        doc.status = ApprovalStatus.____;
        return doc;
    }

    // submit은 임시저장을 결재 대기로 옮기는 업무 동작입니다. 필드의 아무 값이나 허용하는 setter 대신
    // 이름 있는 메서드에 상태 조건을 모아 잘못된 순서의 요청을 막습니다.
    public void submit() {
        // TODO 07: DRAFT 인 문서만 결재 요청할 수 있다.
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("임시저장 상태의 문서만 결재 요청할 수 있습니다.");
        }
        this.status = ApprovalStatus.____;
    }

    public void approve(Long approverId) {
        // TODO 08: 결재자가 본인이 맞는지 확인합니다.
        if (!this.approverId.equals(____)) {
            throw new IllegalStateException("결재자만 승인할 수 있습니다.");
        }
        // TODO 09: PENDING 인 문서만 승인할 수 있다.
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("결재 대기 문서만 승인할 수 있습니다.");
        }
        this.status = ApprovalStatus.APPROVED;
        // TODO 10: 승인 시점을 기록한다.
        this.approvedAt = LocalDateTime.____();
    }

    public void reject(Long approverId, String reason) {
        if (!this.approverId.equals(approverId)) {
            throw new IllegalStateException("결재자만 반려할 수 있습니다.");
        }
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("결재 대기 문서만 반려할 수 있습니다.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("반려 사유는 필수입니다.");
        }
        this.status = ApprovalStatus.REJECTED;
        this.rejectReason = reason;
    }
}

// 학습 질문 (면접 대비):
// Q1. 결재 상태 머신(DRAFT→PENDING→APPROVED/REJECTED)에서 "허용되지 않는 전이"의 예를 두 개 들고,
//     그것을 어디에서(Entity/Service/DB) 막는 것이 가장 견고한지 말해 보세요.
//     A:
// Q2. 작성자=결재자 금지 검증을 Entity createDraft() 에 둔 것과 Service 에 둔 것의 차이는?
//     DB 제약으로 표현하기 어려운 이유는 무엇인가요?
//     A:
// Q3. approvedAt 을 nullable 로 둔 이유는? 만약 NOT NULL + 기본값으로 두면 어떤 의미가 왜곡되나요?
//     A:
// Q4. DRAFT 와 PENDING 을 굳이 분리하면 어떤 사용자 기능(임시저장 등)을 자연스럽게 표현할 수 있나요?
//     A:
// Q5. writerId.equals(approverId) 처럼 Long 을 == 가 아니라 equals 로 비교해야 하는 이유는?
//     (Long 캐시 범위 -128~127 함정)
//     A:
