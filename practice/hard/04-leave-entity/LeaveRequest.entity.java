// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/LeaveRequest.java
// 목표: 휴가 신청 엔티티 + LeaveType + ApprovalStatus 를 거의 백지에서 작성. TRD 3.3.4, 3.6.3, 3.6.4.
// 막히면 starter/04-leave-entity, answers.md 4·11장 참고.

// TODO 01: 연차/반차/병가/공가.
public enum LeaveType {
    ____, ____, ____, ____
}

// TODO 02: 결재/휴가 공용. (휴가는 DRAFT 미사용)
public enum ApprovalStatus {
    ____, ____, ____, ____
}

@Entity
@Table(name = "leave_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 03: 신청자(직원) 연관관계 + fetch.
    @____(fetch = FetchType.____)
    @JoinColumn(name = "employee_id", nullable = false)
    private ____ employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 30)
    private ____ leaveType;

    // TODO 04: 날짜 타입(시간 불필요).
    @Column(name = "start_date", nullable = false)
    private ____ startDate;

    @Column(name = "end_date", nullable = false)
    private ____ endDate;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStatus status;

    // TODO 05: 승인자는 처음엔 null.
    @Column(name = "approver_id")
    private ____ approverId;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // TODO 06: 정적 팩토리 — 시작일>종료일이면 막고, 초기 상태는?
    public static LeaveRequest create(Employee employee, LeaveType type,
                                      LocalDate startDate, LocalDate endDate, String reason) {
        if (startDate == null || endDate == null || startDate.____(____)) {
            throw new ____("휴가 시작일은 종료일보다 늦을 수 없습니다.");
        }
        ____ r = new ____();
        r.employee = employee;
        r.leaveType = type;
        r.startDate = startDate;
        r.endDate = endDate;
        r.reason = reason;
        r.status = ApprovalStatus.____;
        return r;
    }

    // TODO 07: 승인 — PENDING 만, 승인자 세팅, 상태 전이.
    public void approve(Long approverId) {
        if (this.status != ApprovalStatus.____) {
            throw new ____("PENDING 상태만 승인할 수 있습니다.");
        }
        this.approverId = ____;
        this.status = ApprovalStatus.____;
    }

    // TODO 08: 반려 — 사유 필수, PENDING 만, 상태 전이.
    public void reject(Long approverId, String rejectReason) {
        if (rejectReason == null || rejectReason.____()) {
            throw new ____("반려 사유는 필수입니다.");
        }
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("PENDING 상태만 반려할 수 있습니다.");
        }
        this.approverId = approverId;
        this.rejectReason = rejectReason;
        this.status = ApprovalStatus.____;
    }

    // TODO 09: 본인 취소 — PENDING 만.
    public void cancelByOwner() {
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("대기 중인 휴가만 취소할 수 있습니다.");
        }
        this.status = ApprovalStatus.____; // 또는 별도 CANCELED 상태 정의
    }
}

// 학습 질문 (직접 답):
// Q1. PENDING 검증을 도메인 메서드가 아닌 Service if 에만 두면 어떤 버그가 가능한가?
//     A:
// Q2. 휴가가 ApprovalStatus 를 공유하되 DRAFT 를 쓰지 않는 이유/위험은?
//     A:
// Q3. 두 관리자가 동시에 같은 휴가를 승인하면? 어떤 락으로 막나?
//     A:

// 자가 채점:
// □ LeaveType 4값  □ ApprovalStatus 4값  □ @ManyToOne(LAZY) employee  □ LocalDate 날짜
// □ create→isAfter+PENDING  □ approve→PENDING체크→APPROVED  □ reject→사유+PENDING→REJECTED  □ cancel→PENDING만
