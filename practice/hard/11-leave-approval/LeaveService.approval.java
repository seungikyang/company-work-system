// 중학생 눈높이: 휴가 승인과 반려
// 관리자가 아직 대기 중인 휴가 신청을 승인하거나 반려합니다.
// 처리자의 권한과 신청의 현재 상태를 모두 확인해야 같은 신청을 잘못 처리하지 않습니다.
// 관리자 확인 → 신청 찾기 → 대기 상태 확인 → 결과 기록 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 승인/반려를 거의 백지에서. TRD 3.8.2, 3.10.2, 3.16.1.
// 막히면 starter/11-leave-approval, answers.md 11장 참고.

// 초보자 안내 (11장): 변경 감지와 승인 처리
// 개념: 변경 감지는 JPA가 관리 중인 객체의 바뀐 값을 DB에 반영하는 기능입니다.
// 사용 이유: 승인할 때마다 모든 컬럼을 직접 갱신하는 코드를 반복하지 않아도 됩니다.
// 동작 흐름: 트랜잭션 안에서 조회한 휴가에 approve 또는 reject를 호출합니다. 관리 상태 객체의 변경은 flush 시 SQL에 반영되고 커밋으로 확정됩니다. 동시 승인은 별도 잠금 정책을 검토합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@____
public LeaveResponse approve(Long approverUserId, Long leaveId) {

    // TODO 01: 휴가 조회. 없으면?
    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 02: 상태/승인자 처리를 도메인 메서드에 캡슐화(Service 가 직접 setStatus 하지 않는다).
    leave.____(approverUserId);

    // saveAndFlush 불필요 — dirty checking 으로 반영.
    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return LeaveResponse.from(leave);
}

@____
public LeaveResponse reject(Long approverUserId, Long leaveId, LeaveRejectRequest request) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 03: 반려 사유 비면 차단(Service 2차 방어).
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (request.getRejectReason() == null || request.getRejectReason().____()) {
        // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
        throw new BusinessException(ErrorCode.____, "반려 사유는 필수입니다.");
    }

    // TODO 04: 반려 처리(도메인 메서드).
    leave.____(approverUserId, request.getRejectReason());

    return LeaveResponse.from(leave);
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-11
//
// Q1. Entity 승인 메서드에 PENDING 검증을 유지해야 하는 이유는? [chapter-v1-11-05]
// ① Service 조건문이 모든 직접 Entity 호출을 자동 차단하기 때문이다.
// ② Service를 거치지 않는 호출에서도 이미 처리된 휴가의 중복 전이를 막기 위해서다.
// ③ 화면에서 승인 버튼만 숨기면 서버 검증은 필요 없기 때문이다.
// ④ DB가 approve 메서드를 읽고 상태를 자동 검사하기 때문이다.
// 정답: ②
// 해설: 현재 상태와 허용 전이는 도메인 메서드의 계약으로 둔다. 호출자가 늘어도 같은 규칙을 유지한다.
//
// Q2. 같은 휴가를 동시에 승인하는 요청을 검증할 방법은? [chapter-v1-11-06]
// ① 동시 실행 테스트와 버전·잠금 정책으로 하나의 유효 처리와 충돌 응답을 확인한다.
// ② 순차 테스트 한 번이 모든 동시성을 보장한다.
// ③ 상태 enum을 사용하면 동시 갱신 충돌이 없어지는지 확인한다.
// ④ 마지막 요청이 모든 승인 정보를 덮어쓰게 한다.
// 정답: ①
// 해설: 두 요청이 동일한 상태를 읽는 경쟁을 재현하고 충돌 정책을 검증한다. 트랜잭션만으로 중복 처리가 자동 방지되지는 않는다.
//
// Q3. 승인 후 직원 알림의 트리거 위치로 적절한 것은? [chapter-v1-11-07]
// ① Entity가 HTTP 알림 API를 직접 호출하고 항상 DB와 함께 롤백한다.
// ② 승인 상태 검사 전에 무조건 발송한다.
// ③ 알림이 한 번 실패하면 이미 커밋된 승인을 항상 자동 취소한다.
// ④ 승인 커밋 뒤 처리하는 이벤트·outbox 등에서 발송하며 실패 재시도 정책을 둔다.
// 정답: ④
// 해설: 도메인 상태 변경과 외부 통신을 분리하고 발송 신뢰성 요구에 맞춰 재시도·중복 처리를 설계한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ 두 메서드 @Transactional  □ LEAVE_NOT_FOUND  □ leave.approve(approverId)
// □ rejectReason isBlank → INVALID_INPUT  □ leave.reject(approverId, reason)  □ 낙관/비관 락 인지
