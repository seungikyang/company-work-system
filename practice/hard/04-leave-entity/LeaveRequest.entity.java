// 중학생 눈높이: 휴가 신청
// LeaveRequest는 누가 언제 쉬려고 하는지 기록하는 휴가 신청서입니다.
// LocalDate는 시각 없이 날짜만 담습니다. 상태는 대기·승인·반려처럼 진행 단계를 나타냅니다.
// 날짜와 신청자 읽기 → 신청 만들기 → 승인 또는 반려로 상태 바꾸기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/LeaveRequest.java
// 목표: 휴가 신청 엔티티 + LeaveType + ApprovalStatus 를 거의 백지에서 작성. TRD 3.3.4, 3.6.3, 3.6.4.
// 막히면 starter/04-leave-entity, answers.md 4·11장 참고.

// 초보자 안내 (04장): LocalDate와 상태 모델
// 개념: LocalDate는 시각 없는 날짜를, 상태 enum은 신청의 처리 단계를 표현합니다.
// 사용 이유: 휴가 기간과 승인 여부를 자유로운 문자열 대신 정해진 규칙으로 다룹니다.
// 동작 흐름: create에서 날짜와 초기 상태를 정하고 approve·reject 같은 메서드가 허용된 상태인지 확인한 뒤 승인자와 처리 결과를 변경합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (startDate == null || endDate == null || startDate.____(____)) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new ____("휴가 시작일은 종료일보다 늦을 수 없습니다.");
        }
        ____ r = new ____();
        r.employee = employee;
        r.leaveType = type;
        r.startDate = startDate;
        r.endDate = endDate;
        r.reason = reason;
        r.status = ApprovalStatus.____;
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ LeaveType 4값  □ ApprovalStatus 4값  □ @ManyToOne(LAZY) employee  □ LocalDate 날짜
// □ create→isAfter+PENDING  □ approve→PENDING체크→APPROVED  □ reject→사유+PENDING→REJECTED  □ cancel→PENDING만
