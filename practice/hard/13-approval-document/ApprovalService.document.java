// 중학생 눈높이: 결재 작성과 제출
// 문서를 임시 저장한 뒤 지정한 결재자에게 처리를 요청합니다.
// 작성자와 결재자는 서로 다른 역할입니다. 자기 문서를 자기 자신에게 승인 요청하지 않도록 확인합니다.
// 작성자·결재자 확인 → 임시 문서 만들기 → 작성자가 제출하기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 작성(DRAFT) → 요청(PENDING)을 거의 백지에서. PRD 2.4.3, 2.5.6, TRD 3.8.4.
// 막히면 starter/13-approval-document, answers.md 13장 참고.

// 초보자 안내 (13장): 작성과 제출 분리
// 개념: 임시 저장과 제출은 다른 업무 동작이며 서로 다른 상태를 만듭니다.
// 사용 이유: 작성 중인 문서가 결재자의 처리 목록에 바로 섞이지 않게 합니다.
// 동작 흐름: createDraft는 작성자·결재자 조건을 확인하고 DRAFT로 저장합니다. submit은 문서를 조회하고 작성자를 확인한 뒤 도메인 메서드로 제출 상태로 바꿉니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@____
public ApprovalResponse createDraft(Long currentEmployeeId, ApprovalCreateRequest request) {

    // TODO 01: 작성자=결재자 금지.
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (currentEmployeeId.____(request.getApproverId())) {
        // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
        throw new BusinessException(ErrorCode.____, "작성자와 결재자는 같을 수 없습니다.");
    }

    // TODO 02: 결재자 존재 + ACTIVE 확인.
    ____ approver = employeeRepository.findById(request.getApproverId())
        .orElseThrow(() -> new BusinessException(ErrorCode.____));
    if (!approver.____()) {
        throw new BusinessException(ErrorCode.INVALID_STATUS, "결재자는 재직 중이어야 합니다.");
    }

    ApprovalDocument doc = ApprovalDocument.createDraft(
        currentEmployeeId, request.getApproverId(),
        request.getTitle().trim(), request.getContent()
    );
    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return ApprovalResponse.from(approvalRepository.save(doc));
}

@____
public ApprovalResponse submit(Long currentEmployeeId, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 03: 본인 작성 문서만 요청.
    if (!doc.getWriterId().____(currentEmployeeId)) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 04: 상태 전이는 도메인 메서드.
    doc.____();

    return ApprovalResponse.from(doc);
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-13
//
// Q1. 전자결재 작성과 submit 호출을 분리하는 주된 이유는? [chapter-v1-13-04]
// ① 임시저장·편집 단계와 결재 요청 시작 시점을 명확히 구분하기 위해서다.
// ② 작성한 문서를 자동으로 최종 승인하기 위해서다.
// ③ 작성자가 결재자 검사 없이 상태를 바꾸게 하기 위해서다.
// ④ 결재 대기 목록에 모든 미작성 문서를 넣기 위해서다.
// 정답: ①
// 해설: DRAFT에서 준비하고 submit으로 PENDING에 진입하는 업무 흐름을 상태·권한 규칙과 연결한다.
//
// Q2. 존재하지 않는 결재자 ID로 문서를 작성할 때 알맞은 처리는? [chapter-v1-13-05]
// ① 없는 ID를 임의의 관리자 ID로 바꿔 저장한다.
// ② 성공 201을 보내고 유효하지 않은 문서를 남긴다.
// ③ DB 비밀번호와 stack trace를 응답에 넣는다.
// ④ 결재자 조회 실패를 사용자 없음 등 정의된 업무 오류와 404 응답으로 변환하고 저장하지 않는다.
// 정답: ④
// 해설: 이 저장소의 존재 확인·ErrorCode 계약을 따른다. 외래키 오류 이전에 명확한 업무 응답을 주고 실패 저장이 없는지 검증한다.
//
// Q3. doc.submit의 상태 검사를 Service 조건문으로만 옮기면 생길 수 있는 문제는? [chapter-v1-13-06]
// ① 모든 호출자가 Service 조건문을 자동 상속한다.
// ② submit 메서드에 현재 상태는 필요 없다.
// ③ 다른 호출자가 submit을 사용하면서 도메인 상태 규칙을 우회할 수 있다.
// ④ PENDING·APPROVED 상태를 구분할 필요가 없어진다.
// 정답: ③
// 해설: 전이 메서드는 자기 상태 규칙을 지키고 Service는 조회·권한·트랜잭션을 조정한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ 두 메서드 @Transactional  □ equals/INVALID_INPUT  □ APPROVAL_NOT_FOUND  □ EMPLOYEE_NOT_FOUND
// □ approver.isActive()  □ writerId.equals(current)/ACCESS_DENIED  □ doc.submit()
