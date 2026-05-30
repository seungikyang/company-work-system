// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 전자결재 문서의 작성(DRAFT) → 결재 요청(PENDING) 흐름을 채우세요.
//       PRD 2.4.3, 2.5.6, TRD 3.8.4 참고.

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
