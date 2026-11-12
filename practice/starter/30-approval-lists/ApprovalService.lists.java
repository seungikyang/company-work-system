// 중학생 눈높이: 결재 목록과 상세
// 내가 쓴 문서와 내가 처리할 문서를 다른 조건으로 조회합니다.
// 전체 목록 대신 현재 사용자와 관계있는 문서만 찾도록 검색 조건과 권한을 함께 확인합니다.
// 현재 사용자 확인 → 작성자·결재자 조건 선택 → 목록 또는 상세 응답 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 — 내 목록 / 승인 대기 목록 / 상세. PRD 2.5.6 (FR-APPROVAL-003/004), TRD 3.7.6 참고.

// 초보자 안내 (30장): 조건별 결재 조회
// 개념: 같은 결재 테이블도 작성자·결재자·상태에 따라 다른 목록이 됩니다.
// 사용 이유: 내가 쓴 문서와 내가 처리할 문서를 구분하고 타인의 문서 노출을 막습니다.
// 동작 흐름: my는 작성자 조건, pending은 결재자와 대기 상태 조건으로 조회합니다. 상세에서도 작성자·결재자·관리자 여부를 검사한 뒤 DTO로 변환합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional(readOnly = true)
public Page<ApprovalResponse> findMyDocuments(Long currentEmployeeId, ApprovalStatus status, Pageable pageable) {

    Page<ApprovalDocument> page;
    // status가 없다는 것은 상태로 좁히지 않는다는 뜻입니다. 두 분기 모두 작성자 ID 조건은 유지해야
    // 상태 필터를 지웠을 때 다른 사람의 문서가 섞이지 않습니다.
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (status == null) {
        // TODO 01: 내가 작성한 결재 문서 전체.
        page = approvalRepository.____(currentEmployeeId, pageable);
    } else {
        page = approvalRepository.findByWriterIdAndStatus(currentEmployeeId, status, pageable);
    }
    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return page.map(ApprovalResponse::from);
}

@Transactional(readOnly = true)
public Page<ApprovalResponse> findPendingForApprover(Long currentEmployeeId, Pageable pageable) {

    // TODO 02: "내가 결재해야 할" + "PENDING" 두 조건을 모두 만족해야 합니다.
    return approvalRepository.findByApproverIdAndStatus(currentEmployeeId, ApprovalStatus.____, pageable)
            .map(ApprovalResponse::from);
}

@Transactional(readOnly = true)
public ApprovalResponse detail(Long currentEmployeeId, UserRole currentRole, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));

    // TODO 03: 작성자도 결재자도 아니고 ADMIN 도 아닌 사람이 접근하면 차단.
    boolean isWriter   = doc.getWriterId().equals(currentEmployeeId);
    boolean isApprover = doc.getApproverId().equals(currentEmployeeId);
    boolean isAdmin    = currentRole == UserRole.____;
    if (!isWriter && !isApprover && !isAdmin) {
        // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
        throw new BusinessException(ErrorCode.____);
    }

    return ApprovalResponse.from(doc);
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-30
//
// Q1. 내 문서와 승인 대기 목록을 별도 메서드로 나누는 장점은? [chapter-v1-30-04]
// ① 메서드가 둘이면 권한 검사가 필요 없다.
// ② 작성자·결재자·역할·상태의 서로 다른 조회 계약을 명확하게 유지한다.
// ③ 어떤 목록이든 전체 문서를 반환하게 한다.
// ④ 같은 ID 값이면 작성자와 결재자 의미도 항상 같다.
// 정답: ②
// 해설: 조회 목적별로 입력과 조건을 명시해 잘못된 분기와 권한 범위 혼동을 줄인다.
//
// Q2. 결재 위임 기능을 추가할 때 대기 목록 쿼리에 반영할 조건은? [chapter-v1-30-05]
// ① 위임 요청이 한 번 있었으면 영구히 모든 문서를 공개한다.
// ② 모든 작성자 ID를 현재 결재자 ID로 덮어쓴다.
// ③ 유효한 위임 관계·기간·문서 범위와 대기 상태를 적용해 현재 행위자의 처리 가능 문서만 조회한다.
// ④ 위임은 화면 표시만 바꾸고 서버 조건은 유지한다.
// 정답: ③
// 해설: 기존 approverId 조건만으로 새 권한 모델을 표현하지 못할 수 있다. 조회와 실제 처리의 인가 정책을 함께 변경한다.
//
// Q3. 관리자의 전체 결재 목록을 제공하는 Service 설계로 알맞은 것은? [chapter-v1-30-06]
// ① 클라이언트가 보낸 isAdmin=true만으로 전체 문서를 반환한다.
// ② 기존 내 문서 메서드에서 소유권 조건을 항상 없앤다.
// ③ 역할 검사를 Repository 이름으로만 대체한다.
// ④ 신뢰할 수 있는 행위자·역할을 검증하는 별도 관리자 조회 메서드와 페이지·필터 계약을 둔다.
// 정답: ④
// 해설: 관리자 전체 조회의 허용 범위를 명시한다. 임의의 요청 역할 값을 신뢰하지 않고 인증된 권한을 확인한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - /my(writerId 조건) vs /pending(approverId + status=PENDING) — 쿼리 조건이 달라 한 메서드에서 분기하지 않고 분리.
// - detail 권한: writer/approver/ADMIN 셋 다 아니면 ACCESS_DENIED(403). @PreAuthorize 의 hasAnyRole 을 통과한 APPROVER 라도
//   "남의 결재 문서" 면 차단된다 → "역할" 과 "소유/담당" 은 다른 검사.
// - 권한 위임(approverId 변경)이 생기면 pending 은 현재 approverId 기준이라 위임 즉시 새 결재자에게 노출된다.
// - 관리자 전체 조회는 시그니처에 UserRole 을 받아 ADMIN 분기를 명시하는 편이 읽기 쉽다.
