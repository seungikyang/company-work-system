// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 승인 / 반려 처리. TRD 3.8.4, 3.10.3 참고.

@Transactional
public ApprovalResponse approve(Long currentEmployeeId, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));

    // TODO 01: 도메인 메서드 안에서 결재자/상태 검증 + 승인일시 세팅을 모두 처리합니다.
    doc.____(currentEmployeeId);

    return ApprovalResponse.from(doc);
}

@Transactional
public ApprovalResponse reject(Long currentEmployeeId, Long approvalId, ApprovalRejectRequest request) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));

    // TODO 02: 반려 사유가 비어 있으면 즉시 차단. 응답 코드는?
    if (request.getRejectReason() == null || request.getRejectReason().isBlank()) {
        throw new BusinessException(ErrorCode.____, "반려 사유는 필수입니다.");
    }

    doc.reject(currentEmployeeId, request.getRejectReason());
    return ApprovalResponse.from(doc);
}

// 학습 질문:
// Q1. 도메인 메서드 안에서 검증을 모두 처리하면 Service 가 가벼워지는데, 단점은 없을까?
//     A:
// Q2. 결재자가 자기 자신을 승인자로 등록한 문서를 승인하려고 하면 어디서 어떻게 막히는가?
//     A:
// Q3. approvedAt 을 LocalDateTime.now() 로 직접 채우는 대신 시계(Clock) 를 주입하면 어떤 이점이 있나?
//     A:
