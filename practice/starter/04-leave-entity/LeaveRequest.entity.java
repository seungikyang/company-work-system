// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/LeaveRequest.java
// 목표: 휴가 신청 엔티티 + LeaveType + ApprovalStatus enum 을 채우세요.
//       TRD 3.3.4, 3.6.3, 3.6.4 참고.

// ===== LeaveType =====
public enum LeaveType {
    // TODO 01: 연차 / 반차 / 병가 / 공가 네 가지 휴가 종류를 채우세요.
    ____, ____, ____, ____
}

// ===== ApprovalStatus (휴가/결재 공용) =====
public enum ApprovalStatus {
    // TODO 02: 결재 문서는 DRAFT 가 있고, 휴가는 곧바로 PENDING 으로 들어가지만,
    //          하나의 enum 으로 묶어 관리한다고 가정합니다.
    DRAFT, ____, ____, ____
}

// ===== LeaveRequest entity =====
@Entity
@Table(name = "leave_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 03: 신청자(직원) 연관관계. 한 명의 직원이 여러 휴가를 신청할 수 있다.
    @____(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 30)
    private LeaveType leaveType;

    // TODO 04: 시간 정보는 필요 없습니다. 어떤 타입이 적절할까요?
    @Column(name = "start_date", nullable = false)
    private ____ startDate;

    @Column(name = "end_date", nullable = false)
    private ____ endDate;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStatus status;

    // TODO 05: 승인자는 처음에는 null 입니다.
    @Column(name = "approver_id")
    private Long ____;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static LeaveRequest create(Employee employee, LeaveType type,
                                      LocalDate startDate, LocalDate endDate, String reason) {
        // TODO 06: 휴가 시작일이 종료일보다 늦으면 어떻게 처리해야 할까요?
        //          (도메인에서 throw 할지, Service 에서 검증할지 트레이드오프를 생각해보세요)
        if (startDate == null || endDate == null || startDate.isAfter(____)) {
            throw new IllegalArgumentException("휴가 시작일은 종료일보다 늦을 수 없습니다.");
        }

        LeaveRequest r = new LeaveRequest();
        r.employee = employee;
        r.leaveType = type;
        r.startDate = startDate;
        r.endDate = endDate;
        r.reason = reason;
        // TODO 07: 신청 직후의 기본 상태는?
        r.status = ApprovalStatus.____;
        return r;
    }

    public void approve(Long approverId) {
        // TODO 08: PENDING 상태가 아니면 승인할 수 없습니다.
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("PENDING 상태만 승인할 수 있습니다.");
        }
        this.approverId = approverId;
        this.status = ApprovalStatus.____;
    }

    public void reject(Long approverId, String rejectReason) {
        // TODO 09: 반려 사유가 비어 있으면 안 됩니다.
        if (rejectReason == null || rejectReason.____()) {
            throw new IllegalArgumentException("반려 사유는 필수입니다.");
        }
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태만 반려할 수 있습니다.");
        }
        this.approverId = approverId;
        this.rejectReason = rejectReason;
        this.status = ApprovalStatus.____;
    }

    public void cancelByOwner() {
        // TODO 10: 본인이 PENDING 휴가를 취소하는 경우만 허용한다.
        //          이미 승인/반려된 휴가를 취소할 수 있게 두면 어떤 문제가 생길까요?
        if (this.status != ApprovalStatus.____) {
            throw new IllegalStateException("대기 중인 휴가만 취소할 수 있습니다.");
        }
        this.status = ApprovalStatus.REJECTED; // 또는 별도의 CANCELED 상태를 새로 정의해도 좋습니다.
    }
}

// 학습 질문 (면접 대비):
// Q1. 상태 검증(PENDING 인지)을 approve()/reject() 같은 Entity 도메인 메서드에 두는 것과
//     Service 의 if 문에만 두는 것은 어떤 차이가 있나요? Service 에만 두면 어떤 우회 버그가 가능한가요?
//     (TRD 3.16.1 트러블슈팅과 연결)
//     A:
// Q2. 휴가 status 는 결재와 같은 ApprovalStatus 를 공유하지만 DRAFT 는 쓰지 않습니다.
//     enum 을 공유하는 것의 장점과, 휴가에 DRAFT 가 노출될 때의 위험을 말해 보세요.
//     A:
// Q3. 두 관리자가 같은 PENDING 휴가를 "동시에" 승인하면 어떤 일이 벌어질까요?
//     낙관적 락(@Version) 과 비관적 락 중 무엇으로 막을 수 있을까요?
//     A:
// Q4. 취소를 REJECTED 로 재사용하는 것과 별도 CANCELED 상태를 추가하는 것 중
//     "휴가 통계/이력"관점에서 무엇이 더 정확할까요?
//     A:
