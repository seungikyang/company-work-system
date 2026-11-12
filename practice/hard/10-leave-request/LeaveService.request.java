// 중학생 눈높이: 휴가 신청 처리
// 신청서를 저장하기 전에 직원과 날짜가 업무 규칙에 맞는지 확인합니다.
// 검증은 틀린 입력을 걸러내는 과정입니다. 시작일이 종료일보다 늦은 신청은 막아야 합니다.
// 직원 확인 → 날짜 검사 → 신청서 만들기 → 저장 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 신청 흐름을 거의 백지에서. PRD 2.4.2, 2.5.4, TRD 3.8.2.
// 막히면 starter/10-leave-request, answers.md 10장 참고.

// 초보자 안내 (10장): 업무 규칙 검증
// 개념: 업무 규칙은 재직자만 신청 가능 같은 서비스 고유의 조건입니다.
// 사용 이유: 필수값 검사만으로는 잘못된 날짜 순서나 퇴사자의 신청을 걸러낼 수 없습니다.
// 동작 흐름: 직원 조회 → 재직 여부 확인 → 시작일·종료일 검사 → 휴가 생성·저장 → DTO 반환 순서입니다. 실패 조건에서는 예외를 던져 이후 저장으로 진행하지 않습니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@____
public LeaveResponse request(Long currentEmployeeId, LeaveCreateRequest request) {

    // TODO 01: 신청 직원 존재 확인.
    ____ employee = employeeRepository.findById(currentEmployeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 02: 퇴사 직원 차단.
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (!employee.____()) {
        // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 03: 날짜 검증(Service 1차).
    if (request.getStartDate() == null || request.getEndDate() == null
            || request.getStartDate().____(request.getEndDate())) {
        throw new BusinessException(ErrorCode.____);
    }

    LeaveRequest entity = LeaveRequest.create(
        employee, request.getLeaveType(),
        request.getStartDate(), request.getEndDate(), request.getReason()
    );
    LeaveRequest saved = leaveRepository.save(entity);

    // TODO 04: DTO 변환.
    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return LeaveResponse.____(saved);
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-10
//
// Q1. 기간 순서 검증을 DTO의 @AssertTrue로 옮기는 이점과 한계는? [chapter-v1-10-04]
// ① 입력 단계에서 일관된 검증 오류를 내고 업무 핵심 규칙은 다른 호출 경로에서도 지킨다.
// ② DTO 어노테이션을 붙이면 @Valid 없이도 모든 요청이 검사된다.
// ③ DTO 검증을 추가하면 DB 조회가 필요한 모든 업무 검증도 자동 처리된다.
// ④ DTO가 유효하면 휴가 상태 검증도 삭제해야 한다.
// 정답: ①
// 해설: 시작일·종료일 입력 검증과 상태·소유권 같은 업무 검증의 책임을 나눈다. 필요한 도메인 불변식도 유지한다.
//
// Q2. 기존 PENDING·APPROVED 휴가와 새 휴가의 기간 겹침을 판단하는 조건은? [chapter-v1-10-05]
// ① 기존 시작일과 새 시작일이 같을 때만 검사한다.
// ② 반려된 다른 직원의 휴가가 있으면 모든 새 신청을 거절한다.
// ③ 종료일이 시작일보다 앞서면 항상 겹치지 않으므로 허용한다.
// ④ 같은 직원의 유효 상태 휴가에서 기존 시작일 ≤ 새 종료일이고 기존 종료일 ≥ 새 시작일인 경우를 검사한다.
// 정답: ④
// 해설: 닫힌 날짜 구간의 겹침 조건과 직원·상태 범위를 함께 적용한다. 경계 포함 여부와 동시 신청 정책도 정한다.
//
// Q3. 휴가 신청 저장 후 외부 알림을 보내는 안전한 기본 흐름은? [chapter-v1-10-06]
// ① 커밋 전에 발송하면 DB 롤백 시 알림도 자동 회수된다.
// ② 외부 알림 호출은 @Transactional이 있으면 DB와 항상 함께 롤백된다.
// ③ 커밋 후 이벤트나 outbox 등으로 저장 성공과 알림 발송을 연결한다.
// ④ 저장 여부를 확인하지 않고 Controller 진입 즉시 보낸다.
// 정답: ③
// 해설: 외부 전송과 DB 트랜잭션은 자동으로 원자적이지 않다. 커밋 후 처리와 재시도·발송 보장 요구를 함께 설계한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ @Transactional  □ EMPLOYEE_NOT_FOUND  □ isActive()/ACCESS_DENIED  □ isAfter()/INVALID_DATE_RANGE
// □ LeaveResponse.from  □ 알림은 AFTER_COMMIT
