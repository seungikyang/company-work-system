// 중학생 눈높이: 결재 승인과 반려
// 지정된 결재자가 대기 중인 문서의 처리 결과를 기록합니다.
// 로그인했다는 것과 이 문서를 처리할 권한이 있다는 것은 별개라서 두 조건을 확인합니다.
// 문서 찾기 → 지정 결재자 확인 → 상태 확인 → 승인 또는 반려 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/ApprovalService.java
// 목표: 결재 승인 / 반려 처리. TRD 3.8.4, 3.10.3 참고.

// 초보자 안내 (14장): 행위자 검증과 상태 변경
// 개념: 행위자는 지금 승인·반려를 요청한 사람입니다.
// 사용 이유: 로그인만 한 사람이 다른 사람에게 지정된 결재를 처리하지 못하게 합니다.
// 동작 흐름: 문서를 조회하고 도메인 메서드에서 지정 결재자와 현재 상태를 검사합니다. 반려는 사유도 확인하며, 성공하면 상태와 처리 정보를 함께 변경합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional
public ApprovalResponse approve(Long currentEmployeeId, Long approvalId) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));

    // TODO 01: 도메인 메서드 안에서 결재자/상태 검증 + 승인일시 세팅을 모두 처리합니다.
    // Service는 문서를 찾고 Entity 메서드는 지정 결재자·현재 상태를 검사합니다.
    // 06장의 메서드로 이동해 어떤 조건에서 throw로 중단되는지 함께 읽으세요.
    doc.____(currentEmployeeId);

    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return ApprovalResponse.from(doc);
}

@Transactional
public ApprovalResponse reject(Long currentEmployeeId, Long approvalId, ApprovalRejectRequest request) {

    ApprovalDocument doc = approvalRepository.findById(approvalId)
        .orElseThrow(() -> new BusinessException(ErrorCode.APPROVAL_NOT_FOUND));

    // TODO 02: 반려 사유가 비어 있으면 즉시 차단. 응답 코드는?
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (request.getRejectReason() == null || request.getRejectReason().isBlank()) {
        // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
        throw new BusinessException(ErrorCode.____, "반려 사유는 필수입니다.");
    }

    doc.reject(currentEmployeeId, request.getRejectReason());
    return ApprovalResponse.from(doc);
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-14
//
// Q1. Entity에 상태 규칙을 모아도 Service에 남겨야 하는 책임은? [chapter-v1-14-05]
// ① HTTP 요청과 DB 연결을 모두 Entity 필드에 저장하는 책임이다.
// ② Entity 안에서 모든 외부 API를 직접 호출하는 책임이다.
// ③ 업무 규칙과 무관하게 모든 검증을 제거하는 책임이다.
// ④ 인증된 행위자의 권한·조회·트랜잭션·외부 작업을 조정하는 책임이다.
// 정답: ④
// 해설: 상태 불변식과 외부 의존을 구분한다. Entity가 사용자 조회·통신까지 맡으면 결합과 테스트 부담이 커진다.
//
// Q2. 자기결재와 지정 결재자가 아닌 사람의 처리를 막는 올바른 검사는? [chapter-v1-14-06]
// ① 요청 본문에 결재자라고 쓰면 승인한다.
// ② URL을 아는 사용자라면 누구나 승인한다.
// ③ 생성 시 작성자·결재자 불일치를 지키고 승인 시 인증된 현재 직원과 지정 결재자·역할을 검사한다.
// ④ 작성자와 결재자가 같아도 화면에서만 경고한다.
// 정답: ③
// 해설: 생성 불변식과 처리 시 행위자 검사는 별개다. 지정 결재자 여부·역할·현재 상태를 서버에서 검증한다.
//
// Q3. 승인 시각 계산에 Clock을 주입하는 장점은? [chapter-v1-14-07]
// ① Clock이 모든 DB 트랜잭션을 자동 롤백한다.
// ② 테스트에서 시계를 고정해 approvedAt을 결정적으로 비교할 수 있다.
// ③ Clock을 넣으면 시간을 저장하지 않아도 된다.
// ④ 현재 시각은 언제나 같은 값이므로 테스트 차이가 없어진다.
// 정답: ②
// 해설: 운영 시각 공급과 테스트 시각 공급을 분리한다. Clock 기반 시간 계산을 실제 승인 경로에서 사용해야 효과가 있다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - 도메인 메서드로 옮겨도 Service 는 사라지지 않는다: 권한(APPROVER/ADMIN), 트랜잭션, 외부 의존(알림)은 Service 책임.
//   엔티티는 "자기 상태 전이 규칙" 만 담는다.
// - 결재자 본인 검증(approverId.equals(current))이 빠지면 URL 만 알면 누구나 승인하는 권한 누락(IDOR).
// - Clock 주입 시 테스트에서 시간 고정 → approvedAt 을 결정적으로 단정 가능(가산점).
