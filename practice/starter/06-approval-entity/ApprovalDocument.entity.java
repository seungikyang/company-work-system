// 중학생 눈높이: 결재 문서
// ApprovalDocument는 작성자가 허락을 요청하고 결재자가 처리하는 문서입니다.
// 상태 전이는 진행 단계가 바뀌는 일입니다. 임시 저장에서 제출하고, 대기 중인 문서를 처리합니다.
// 작성자·결재자 확인 → 작성 → 제출 → 승인 또는 반려 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
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
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (writerId != null && writerId.____(approverId)) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new IllegalArgumentException("작성자와 결재자는 같을 수 없습니다.");
        }

        ApprovalDocument doc = new ApprovalDocument();
        doc.writerId = writerId;
        doc.approverId = approverId;
        doc.title = title;
        doc.content = content;
        // TODO 06: 새 문서의 초기 상태는?
        doc.status = ApprovalStatus.____;
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-06
//
// Q1. 허용되지 않는 결재 상태 전이와 방어 위치를 짝지은 것은? [chapter-v1-06-04]
// ① 어떤 상태에서도 approve를 허용하고 화면에서만 막는다.
// ② DRAFT에서 PENDING으로 가는 정상 제출을 항상 차단한다.
// ③ 완료 문서를 조회할 때마다 PENDING으로 돌린다.
// ④ APPROVED에서 재승인하거나 REJECTED에서 바로 승인하는 전이를 Entity 변경 메서드에서 차단한다.
// 정답: ④
// 해설: 도메인 상태 전이 표를 정하고 메서드 내부에서 현재 상태를 검사한다. 다른 계층의 권한 검사도 함께 유지한다.
//
// Q2. 작성자와 결재자가 같은 문서를 막는 검증의 배치로 알맞은 것은? [chapter-v1-06-05]
// ① Entity 생성 규칙으로 ID 동등성을 검사하고 Service에서 사용자 존재·역할 등 외부 조건을 확인한다.
// ② 자기결재 금지는 요청 화면에서만 검사한다.
// ③ 같은 ID여도 역할이 있으면 무조건 자기결재를 허용한다.
// ④ DB CHECK로 두 컬럼의 불일치를 검사하는 것은 모든 DB에서 원천적으로 불가능하다.
// 정답: ①
// 해설: Entity와 Service의 검사 책임을 구분한다. 두 ID의 불일치는 DB CHECK도 검토할 수 있으나 사용자 역할·조회 조건은 별도 검증이 필요하다.
//
// Q3. 결재 승인 시각 approvedAt을 nullable로 두는 이유는? [chapter-v1-06-06]
// ① 미승인 문서도 생성 시각을 승인 시각으로 저장해야 한다.
// ② 미승인 문서는 승인 시각이 없으며 임의 기본값이 실제 승인으로 오해되는 것을 막기 위해서다.
// ③ null은 항상 DB 무결성 오류이므로 사용할 수 없다.
// ④ approvedAt은 문서를 조회할 때마다 갱신해야 한다.
// 정답: ②
// 해설: 승인 여부와 승인 시각의 의미를 일치시킨다. 기본값으로 미승인과 승인 완료를 혼동하지 않도록 한다.
//
// Q4. 결재에서 DRAFT와 PENDING을 분리하면 표현할 수 있는 기능은? [chapter-v1-06-07]
// ① 임시저장한 문서를 자동으로 최종 승인한다.
// ② PENDING 문서는 작성자가 아무 제약 없이 삭제·수정한다.
// ③ 임시저장·편집 단계와 제출 후 결재 대기 단계를 구별할 수 있다.
// ④ DRAFT 문서에도 승인 시각을 무조건 기록한다.
// 정답: ③
// 해설: 작성 중인 문서와 결재 요청된 문서의 편집·취소·승인 정책을 상태별로 정할 수 있다.
//
// Q5. Long ID를 == 대신 equals로 비교해야 하는 이유는? [chapter-v1-06-08]
// ① Long의 ==는 모든 숫자 범위에서 값만 비교한다.
// ② equals는 객체의 저장 메모리 주소만 비교한다.
// ③ ID를 문자열로 출력하면 모든 비교가 자동으로 값 비교가 된다.
// ④ ==는 객체 참조를 비교하므로 같은 숫자인 서로 다른 Long 객체를 다르다고 판단할 수 있다.
// 정답: ④
// 해설: ID가 null일 가능성도 처리한다. 작은 수의 캐시 때문에 일부 == 비교가 우연히 통과해도 값 비교 규칙으로 사용하지 않는다.
//
// 4지선다 학습 문제 (자동 생성 끝)
