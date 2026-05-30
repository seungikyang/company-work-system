// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/ApprovalDocument.java
// 목표: 전자결재 문서 엔티티를 거의 백지에서 작성. TRD 3.3.6, 3.6.4, 3.8.4.
// 막히면 starter/06-approval-entity, answers.md 6장 참고.
//
// 상태 전이 그래프(직접 그려보세요):
//   ____ → ____ → ____
//                ↘ ____

@Entity
@Table(name = "approval_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ApprovalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 01: 작성자 FK.
    @Column(name = "writer_id", nullable = false)
    private ____ writerId;

    // TODO 02: 결재자 FK.
    @Column(name = "approver_id", nullable = false)
    private ____ approverId;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

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

    // TODO 03: 승인 시점만 채워지므로 nullable.
    @Column(name = "approved_at")
    private ____ approvedAt;

    // TODO 04: 정적 팩토리 — 작성자=결재자 금지, 초기 상태?
    public static ApprovalDocument createDraft(Long writerId, Long approverId, String title, String content) {
        if (writerId != null && writerId.____(approverId)) {
            throw new ____("작성자와 결재자는 같을 수 없습니다.");
        }
        ____ doc = new ____();
        doc.writerId = writerId;
        doc.approverId = approverId;
        doc.title = title;
        doc.content = content;
        doc.status = ApprovalStatus.____;
        return doc;
    }

    // TODO 05: 결재 요청 — DRAFT 만.
    public void submit() {
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("임시저장 상태의 문서만 결재 요청할 수 있습니다.");
        }
        this.status = ApprovalStatus.____;
    }

    // TODO 06: 승인 — 결재자 본인 + PENDING + 승인시각.
    public void approve(Long approverId) {
        if (!this.approverId.____(____)) {
            throw new IllegalStateException("결재자만 승인할 수 있습니다.");
        }
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("결재 대기 문서만 승인할 수 있습니다.");
        }
        this.status = ApprovalStatus.____;
        this.approvedAt = LocalDateTime.____();
    }

    // TODO 07: 반려 — 결재자 본인 + PENDING + 사유 필수.
    public void reject(Long approverId, String reason) {
        if (!this.approverId.equals(approverId)) {
            throw new IllegalStateException("결재자만 반려할 수 있습니다.");
        }
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("결재 대기 문서만 반려할 수 있습니다.");
        }
        if (reason == null || reason.____()) {
            throw new IllegalArgumentException("반려 사유는 필수입니다.");
        }
        this.status = ApprovalStatus.____;
        this.rejectReason = reason;
    }
}

// 학습 질문 (직접 답):
// Q1. 허용되지 않는 상태 전이 2개를 들고, 어디서 막는 게 가장 견고한가?
//     A:
// Q2. 작성자=결재자 금지를 Entity 에 두는 것과 Service 에 두는 것의 차이는?
//     A:
// Q3. writerId.equals(approverId) 를 == 가 아니라 equals 로 비교해야 하는 이유는? (Long 캐시)
//     A:

// 자가 채점:
// □ writerId/approverId Long  □ approvedAt nullable LocalDateTime  □ createDraft: equals+DRAFT
// □ submit: DRAFT→PENDING  □ approve: 본인+PENDING→APPROVED+now()  □ reject: 본인+PENDING+사유→REJECTED
