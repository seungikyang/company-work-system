// 실제 구현 위치 예: src/main/java/com/example/companywork/service/LeaveService.java
// 목표: 휴가 승인 / 반려 처리. TRD 3.8.2, 3.10.2, 3.16.1 (트러블슈팅) 참고.

// 초보자 안내 (11장): 변경 감지와 승인 처리
// 개념: 변경 감지는 JPA가 관리 중인 객체의 바뀐 값을 DB에 반영하는 기능입니다.
// 사용 이유: 승인할 때마다 모든 컬럼을 직접 갱신하는 코드를 반복하지 않아도 됩니다.
// 동작 흐름: 트랜잭션 안에서 조회한 휴가에 approve 또는 reject를 호출합니다. 관리 상태 객체의 변경은 flush 시 SQL에 반영되고 커밋으로 확정됩니다. 동시 승인은 별도 잠금 정책을 검토합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional
public LeaveResponse approve(Long approverUserId, Long leaveId) {

    // TODO 01: 휴가 신청을 조회. 없으면 404.
    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 02: 도메인 메서드 안에서 상태/승인자 처리를 캡슐화한다.
    //          (Service 가 직접 leave.setStatus(...) 하면 도메인 규칙이 누수됩니다)
    leave.____(approverUserId);

    // saveAndFlush 를 굳이 부르지 않아도 트랜잭션 종료 시 dirty checking 으로 반영됩니다.
    return LeaveResponse.from(leave);
}

@Transactional
public LeaveResponse reject(Long approverUserId, Long leaveId, LeaveRejectRequest request) {

    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    // TODO 03: 반려 사유가 비어 있으면 검증 단계에서 막혀야 하지만,
    //          Service 도 한 번 더 방어선을 둡니다.
    if (request.getRejectReason() == null || request.getRejectReason().____()) {
        throw new BusinessException(ErrorCode.INVALID_INPUT, "반려 사유는 필수입니다.");
    }

    // TODO 04: 반려 처리.
    leave.____(approverUserId, request.getRejectReason());

    return LeaveResponse.from(leave);
}

// 학습 질문:
// Q1. PENDING 상태 검증을 Entity.approve 가 아닌 Service 에서만 했다면 어떤 버그가 가능할까요?
//     A:
// Q2. 두 관리자가 동시에 같은 휴가 승인을 시도하면 어떤 상황이 벌어질까요?
//     A:
// Q3. 승인 후 직원에게 알림을 보내야 한다면, 어디서 트리거하는 게 안전할까요?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - approve()/reject() 도메인 메서드 안에 (상태검증 + 승인자세팅 + 상태전이)를 모으면 Service 는 조회→호출만.
//   검증을 빠뜨릴 경로가 사라진다.
// - 동시 승인: 낙관적 락(@Version, 충돌 드물 때 가벼움) vs 비관적 락(SELECT FOR UPDATE, 충돌 잦을 때).
//   휴가 승인은 충돌이 드물어 낙관적이 적합.
// - 승인 알림은 커밋 후. dirty checking 으로 save 호출 없이도 트랜잭션 종료 시 반영됨.
