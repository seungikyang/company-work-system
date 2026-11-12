// 중학생 눈높이: 휴가 신청
// LeaveRequest는 누가 언제 쉬려고 하는지 기록하는 휴가 신청서입니다.
// LocalDate는 시각 없이 날짜만 담습니다. 상태는 대기·승인·반려처럼 진행 단계를 나타냅니다.
// 날짜와 신청자 읽기 → 신청 만들기 → 승인 또는 반려로 상태 바꾸기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/LeaveRequest.java
// 목표: 휴가 신청 엔티티 + LeaveType + ApprovalStatus enum 을 채우세요.
//       TRD 3.3.4, 3.6.3, 3.6.4 참고.

// 초보자 안내 (04장): LocalDate와 상태 모델
// 개념: LocalDate는 시각 없는 날짜를, 상태 enum은 신청의 처리 단계를 표현합니다.
// 사용 이유: 휴가 기간과 승인 여부를 자유로운 문자열 대신 정해진 규칙으로 다룹니다.
// 동작 흐름: create에서 날짜와 초기 상태를 정하고 approve·reject 같은 메서드가 허용된 상태인지 확인한 뒤 승인자와 처리 결과를 변경합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (startDate == null || endDate == null || startDate.isAfter(____)) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
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
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return r;
    }

    // 상태 변경 전에 조건을 검사하는 이유는 이미 처리된 신청을 다시 승인하지 않기 위해서입니다.
    // throw가 실행되면 아래 대입문으로 진행하지 않습니다. 승인 권한 확인은 Service·보안 계층도 함께 읽으세요.
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-04
//
// Q1. 휴가 상태 검사를 Service에만 두었을 때의 우회 위험은? [chapter-v1-04-05]
// ① 모든 Entity 호출이 자동으로 Service를 거친다.
// ② DB가 모든 자바 상태 전이를 자동 검사한다.
// ③ 다른 호출 경로가 Entity를 직접 변경하면 완료된 휴가를 다시 승인할 수 있다.
// ④ Controller에 버튼이 없으면 재승인 호출도 불가능하다.
// 정답: ③
// 해설: approve·reject 내부에서 허용 상태를 확인해야 호출 경로가 달라도 도메인 규칙을 지킨다.
//
// Q2. 휴가와 결재가 상태 enum을 공유할 때 올바른 설계는? [chapter-v1-04-06]
// ① enum에 DRAFT가 있으므로 모든 휴가를 DRAFT로 만든다.
// ② 같은 enum을 쓰면 두 업무의 상태 전이가 항상 같다.
// ③ 요청에서 어떤 enum 값이 오든 저장한다.
// ④ 공통 이름을 재사용하되 휴가에 허용되지 않는 DRAFT 상태를 생성·전이 규칙으로 막는다.
// 정답: ④
// 해설: 상태 이름 공유와 업무별 허용 상태는 별개다. 필요하면 별도 enum으로 더 강하게 제약할 수 있다.
//
// Q3. 두 관리자의 동시 휴가 승인에서 상태 검사만으로 부족한 이유는? [chapter-v1-04-07]
// ① 둘 다 PENDING을 읽을 수 있으므로 낙관적 버전 검사나 비관적 잠금과 충돌 처리가 필요하다.
// ② PENDING 문자열이 자동으로 DB 잠금을 건다.
// ③ @Transactional만 붙이면 모든 동시 승인이 순서대로 실행된다.
// ④ 나중 요청의 결과를 무조건 덮어쓰면 승인 이력도 안전하다.
// 정답: ①
// 해설: 읽은 뒤 쓰기까지의 경쟁을 제어해야 한다. @Version 충돌 또는 잠금 정책과 재시도·실패 응답을 검증한다.
//
// Q4. 휴가 취소를 반려와 구분하는 상태의 이점은? [chapter-v1-04-08]
// ① 취소한 휴가를 승인 통계에 반드시 더한다.
// ② 신청자의 취소와 관리자의 반려를 구별해 통계·이력의 의미를 보존한다.
// ③ CANCELED 이름만 추가하면 모든 전이 규칙도 자동 생성된다.
// ④ 취소는 언제나 관리자 거절과 완전히 같은 업무다.
// 정답: ②
// 해설: 별도 상태를 도입할 때 생성·전이·권한·조회·통계도 함께 수정한다. 상태 이름만 바꾸는 것으로 끝나지 않는다.
//
// 4지선다 학습 문제 (자동 생성 끝)
