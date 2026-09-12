// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 전자결재 문서의 작성(DRAFT) → 결재 요청(PENDING) 흐름을 채우세요.
//       PRD 2.4.3, 2.5.6, TRD 3.8.4 참고.

// 초보자 안내 (13장): 작성과 제출 분리
// 개념: 임시 저장과 제출은 다른 업무 동작이며 서로 다른 상태를 만듭니다.
// 사용 이유: 작성 중인 문서가 결재자의 처리 목록에 바로 섞이지 않게 합니다.
// 동작 흐름: createDraft는 작성자·결재자 조건을 확인하고 DRAFT로 저장합니다. submit은 문서를 조회하고 작성자를 확인한 뒤 도메인 메서드로 제출 상태로 바꿉니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional
public ApprovalResponse createDraft(Long currentEmployeeId, ApprovalCreateRequest request) {

    // TODO 01: 작성자와 결재자가 같으면 안 됩니다.
    //          Service 와 Entity 양쪽에서 막을지, Entity 한 곳에 맡길지 정해 보세요.
    if (currentEmployeeId.equals(request.getApproverId())) {
        throw new BusinessException(ErrorCode.____, "작성자와 결재자는 같을 수 없습니다.");
    }

    // TODO 02: 결재자가 실제 존재하고 ACTIVE 직원인지 확인.
    Employee approver = employeeRepository.findById(request.getApproverId())
        .orElseThrow(() -> new BusinessException(ErrorCode.____));
    if (!approver.____()) {
        throw new BusinessException(ErrorCode.INVALID_STATUS, "결재자는 재직 중이어야 합니다.");
    }

    ApprovalDocument doc = ApprovalDocument.createDraft(
        currentEmployeeId,
        request.getApproverId(),
        request.getTitle().trim(),
        request.getContent()
    );

    return ApprovalResponse.from(approvalRepository.save(doc));
}

@Transactional
public ApprovalResponse submit(Long currentEmployeeId, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 03: 본인이 작성한 문서만 결재 요청할 수 있다.
    // 로그인 여부와 작성자 여부는 다른 검사입니다. 같은 로그인 사용자라도 다른 사람의 초안을 상신하면 안 됩니다.
    // Long 식별자는 객체 참조 비교 대신 값 비교를 사용합니다.
    if (!doc.getWriterId().equals(currentEmployeeId)) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 04: 상태 전이는 도메인 메서드에 맡깁니다.
    doc.____();

    return ApprovalResponse.from(doc);
}

// 학습 질문:
// Q1. 작성과 결재 요청을 분리하는 이유(임시저장)는?
//     A:
// Q2. 결재자가 존재하지 않는 ID 라면 어떤 상태/에러 코드로 응답해야 하나?
//     A:
// Q3. doc.submit() 안의 상태 검증을 Service 가 if 로 하도록 바꾸면 어떤 단점이 있을까?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - 작성자=결재자 이중 방어: Service equals(친절한 에러) + Entity createDraft(불변식). 어느 경로든 차단.
// - 결재자 ACTIVE 검증: RESIGNED 직원을 결재자로 두면 상신 후 영원히 대기하는 문서가 생긴다.
// - DRAFT/PENDING 분리 = 임시저장 UX + submit() 시점에 필수필드(제목/내용/결재자) 재검증 게이트.
