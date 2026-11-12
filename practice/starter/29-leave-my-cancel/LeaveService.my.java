// 중학생 눈높이: 내 휴가 조회와 취소
// 로그인한 직원이 자기 휴가를 찾거나 대기 중인 신청을 취소합니다.
// 소유자 검사는 이 자료가 현재 사용자의 것인지 확인하는 과정입니다. 남의 신청 취소를 막습니다.
// 휴가 찾기 → 소유자·권한 확인 → 현재 상태 확인 → 조회 또는 취소 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 — 내 목록 / 상세 / 취소 + 관리자 목록 흐름을 채우세요. PRD 2.5.4 (FR-LEAVE-002~004, FR-LEAVE-008), TRD 3.7.4 참고.

// 초보자 안내 (29장): 소유자 검증
// 개념: 소유자 검증은 요청한 자료가 현재 사용자에게 허용된 자료인지 확인하는 일입니다.
// 사용 이유: 휴가 ID만 바꿔 다른 사람의 신청을 보거나 취소하는 일을 막습니다.
// 동작 흐름: 내 목록 쿼리에는 현재 직원 ID를 넣고 상세·취소에서도 소유자 또는 허용 역할을 확인합니다. 취소는 소유권 검사 후 도메인 메서드로 상태 규칙을 적용합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional(readOnly = true)
public Page<LeaveResponse> findMyLeaves(Long currentEmployeeId, Pageable pageable) {

    // TODO 01: 내 휴가만 보여주려면 어떤 Repository 메서드를 호출해야 하나요?
    Page<LeaveRequest> page = leaveRepository.____(currentEmployeeId, pageable);

    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return page.map(LeaveResponse::from);
}

@Transactional(readOnly = true)
public LeaveResponse detail(Long currentEmployeeId, UserRole currentRole, Long leaveId) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 02: 본인 또는 ADMIN 만 상세 조회 가능.
    // isOwner와 isAdmin은 각각 본인·관리자라는 판정 결과입니다.
    // !는 부정, &&는 두 조건 모두 참이라는 뜻이므로 아래 if는 둘 다 아닌 사람을 거절합니다.
    boolean isOwner = leave.getEmployee().getId().equals(currentEmployeeId);
    boolean isAdmin = currentRole == UserRole.____;
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (!isOwner && !isAdmin) {
        // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
        throw new BusinessException(ErrorCode.____);
    }

    return LeaveResponse.from(leave);
}

@Transactional
public LeaveResponse cancel(Long currentEmployeeId, Long leaveId) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 03: 본인 신청만 취소 가능.
    if (!leave.getEmployee().getId().equals(currentEmployeeId)) {
        throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    // TODO 04: 도메인 메서드에서 상태 검증 + 전이를 캡슐화.
    //          이미 승인된 휴가를 본인이 취소할 수 있게 두면 어떤 문제가 생기나?
    leave.____();

    return LeaveResponse.from(leave);
}

// ===== 관리자용 목록 =====

@Transactional(readOnly = true)
public Page<LeaveResponse> findForAdmin(ApprovalStatus status, Long employeeId, Pageable pageable) {
    // TODO 05: 조건이 모두 null 이면 전체, 일부만 있으면 동적으로 조건 적용.
    //          학습 단계에서는 if 분기 또는 @Query 두 개로 시작합니다.

    if (status != null && employeeId != null) {
        return leaveRepository.findByStatusAndEmployee_Id(status, employeeId, pageable)
                .map(LeaveResponse::from);
    } else if (status != null) {
        return leaveRepository.findByStatus(status, pageable).map(LeaveResponse::from);
    } else if (employeeId != null) {
        return leaveRepository.findByEmployee_Id(employeeId, pageable).map(LeaveResponse::from);
    } else {
        return leaveRepository.findAll(pageable).map(LeaveResponse::from);
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-29
//
// Q1. 내 휴가 조회에서 Service와 Repository의 보호를 연결한 것은? [chapter-v1-29-06]
// ① 요청의 직원 ID만 신뢰하고 전체 휴가를 먼저 반환한다.
// ② 인증된 직원 ID를 Service에서 확정하고 조회 쿼리에도 employeeId 조건을 적용한다.
// ③ Service가 있으므로 Repository 조건은 항상 제거한다.
// ④ 사용자가 직원 번호를 알면 타인의 휴가도 조회하게 한다.
// 정답: ②
// 해설: 소유권 검증과 데이터 조회 범위를 맞춘다. 타인의 ID로 요청하는 실패 시나리오를 검증한다.
//
// Q2. 관리자 휴가 목록에 상태·직원·기간 검색을 확장하는 적절한 방법은? [chapter-v1-29-07]
// ① 요청 문자열을 SQL에 그대로 연결한다.
// ② 검색 조건이 늘면 인증 검사를 없앤다.
// ③ 입력 범위를 검증하고 Specification·Querydsl·명시적 쿼리 등으로 선택 조건을 조합한다.
// ④ 기간 검색은 시작일과 종료일을 비교할 수 없다.
// 정답: ③
// 해설: 조건별 의미와 경계·페이지·정렬·권한을 명시한다. 동적 검색은 결과 정확성과 실행 비용을 검증한다.
//
// Q3. CANCELED와 REJECTED를 분리했을 때의 이력상 이점은? [chapter-v1-29-08]
// ① 반려 사유를 취소자 비밀번호로 대체한다.
// ② 상태 이름만 분리하면 모든 기존 데이터가 자동 이관된다.
// ③ 취소된 휴가를 승인된 휴가로 계산한다.
// ④ 신청자의 철회와 관리자의 거절을 구별해 원인별 통계와 이력 해석을 정확히 한다.
// 정답: ④
// 해설: 별도 상태 도입은 전이·조회·통계·기존 데이터 정책까지 포함한다. 현재 실습의 상태 재사용과 확장안을 구분한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - IDOR 이중 방어: ① Service 에서 소유자/ADMIN 검증 ② Repository 쿼리에 employeeId 조건(findByEmployee_Id).
//   URL 의 leaveId 만 믿고 findById 후 반환하면 남의 휴가가 새어나간다.
// - findForAdmin 의 if 분기는 조건 조합(status/employeeId)을 다룬다. 기간(from/to)까지 늘면 조합 폭발 →
//   Querydsl/Specification 으로 동적 쿼리 1개로 합친다.
// - cancelByOwner() 는 본인+PENDING 만 허용. 승인된 휴가를 취소 가능하게 두면 이미 반영된 일정/통계가 깨진다.
// - CANCELED 별도 상태 = "본인 취소" 와 "관리자 반려" 를 통계/이력에서 구분.
