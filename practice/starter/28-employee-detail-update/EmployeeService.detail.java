// 중학생 눈높이: 직원 조회와 변경
// 직원 한 명의 정보를 찾고, 부서나 재직 상태를 바꿉니다.
// 퇴사 상태로 남기면 과거 업무 기록의 연결을 보존할 수 있습니다. 행을 삭제하는 것과 다릅니다.
// 직원 찾기 → 바꿀 값·부서 확인 → 변경 메서드 호출 → 응답 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 상세 / 수정 / 비활성·삭제 흐름을 채우세요. PRD 2.5.2 (FR-EMP-003~005) 참고.

// 초보자 안내 (28장): 수정과 퇴사 상태 처리
// 개념: 상태로 퇴사를 표현하는 방식은 행을 삭제하는 물리 삭제와 다릅니다.
// 사용 이유: 과거 휴가·결재에서 참조하는 직원 이력을 유지합니다.
// 동작 흐름: 직원을 조회하고 변경할 부서 등을 확인한 뒤 도메인 메서드로 수정합니다. 삭제 예제의 hardDelete 분기와 퇴사 처리 분기를 비교하고 Controller 호출 인자를 실행 프로젝트에서 맞춥니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Transactional(readOnly = true)
public EmployeeResponse detail(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 01: User 와 Department 가 LAZY 로 묶여 있다면 응답 변환 시점에 어떤 일이 일어날까요?
    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return EmployeeResponse.from(employee);
}

@Transactional
public EmployeeResponse update(Long employeeId, EmployeeUpdateRequest request) {

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

    // TODO 02: 부서를 바꾸는 경우, 부서가 실제 존재하는지 검증한다.
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (request.getDepartmentId() != null
            && !request.getDepartmentId().equals(employee.getDepartment().getId())) {
        Department newDept = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new BusinessException(ErrorCode.____));
        employee.____(newDept);
    }

    // TODO 03: 이름/연락처/직급 등은 도메인 메서드로 변경.
    //          Service 에서 직접 setter 를 쓰지 않는 이유는?
    employee.updateProfile(request.getPosition(), request.getPhone());

    // TODO 04: User 이름 변경은 Employee 의 user 를 통해. 부수효과가 다른 도메인까지 가는지 확인.
    if (request.getName() != null && !request.getName().isBlank()) {
        employee.getUser().changeName(request.getName().trim());
    }

    // dirty checking 으로 commit 시 반영됩니다.
    return EmployeeResponse.from(employee);
}

@Transactional
public void delete(Long employeeId, boolean hardDelete) {

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

    // hardDelete는 행 자체를 지울지 구분하는 입력입니다. 다른 분기는 직원 상태를 변경해 과거 참조를 보존합니다.
    // 이 예제는 휴가 참조만 검사하므로 실행 프로젝트에서는 결재 등 다른 참조와 삭제 정책도 맞추세요.
    if (hardDelete) {
        // TODO 05: 하드 삭제는 휴가/결재/공지에서 참조 무결성이 깨질 수 있습니다.
        //          어떤 정책으로 막거나 허용해야 할까요?
        long leaveCount = leaveRepository.countByEmployee_Id(employeeId);
        if (leaveCount > 0) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new BusinessException(ErrorCode.INVALID_STATUS,
                    "휴가 기록이 있는 직원은 삭제할 수 없습니다. 퇴사 처리로 대신하세요.");
        }
        employeeRepository.delete(employee);
    } else {
        // TODO 06: 일반적으로는 hard delete 보다 어떤 처리가 안전한가요?
        employee.____();
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-28
//
// Q1. 직원 정보 변경을 도메인 메서드로 수행하는 이유는? [chapter-v1-28-05]
// ① 이름·부서·상태의 변경 규칙을 캡슐화해 setter를 통한 무검증 변경을 줄이기 위해서다.
// ② 메서드 이름을 길게 만드는 것이 유일한 목적이다.
// ③ 도메인 메서드가 HTTP 역할 검사를 항상 대신한다.
// ④ 모든 Entity 필드를 공개 mutable로 만들기 위해서다.
// 정답: ①
// 해설: Service는 업무를 조정하고 Entity 변경 메서드는 상태 규칙을 지킨다. 각 경로에서 같은 불변식을 적용한다.
//
// Q2. 부서 변경과 부서 직원 수 캐시 갱신을 함께 다루는 방법은? [chapter-v1-28-06]
// ① Entity가 모든 외부 캐시와 DB를 자동으로 함께 롤백한다.
// ② Service가 저장 일관성을 조정하고 캐시 종류에 따라 커밋 후 무효화·갱신 정책을 적용한다.
// ③ 직원만 바꾸고 캐시는 영구히 그대로 둔다.
// ④ DB 트랜잭션 안에서 외부 캐시를 바꾸면 어떤 경우에도 원자성이 보장된다.
// 정답: ②
// 해설: DB 내 집계와 외부 캐시는 원자성 범위가 다르다. 커밋·무효화·재시도·정합성 요구에 맞춰 설계한다.
//
// Q3. RESIGNED 처리와 물리 삭제의 트레이드오프로 알맞은 것은? [chapter-v1-28-07]
// ① 상태 처리만 하면 모든 개인정보가 즉시 삭제된다.
// ② 물리 삭제는 관계와 이력에 어떤 영향도 주지 않는다.
// ③ 상태 처리는 업무 이력·참조를 보존하지만 조회 조건·접근 제한·보관 정책을 추가로 관리한다.
// ④ 상태 처리된 직원은 언제나 새 업무를 수행해야 한다.
// 정답: ③
// 해설: 이력 보존과 데이터 정리의 요구를 구분한다. 필터 누락·접근 정책·참조 무결성을 함께 검증한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - open-in-view 함정: true 면 LAZY 연관이 뷰 렌더 시점까지 풀려 편하지만 영속성 컨텍스트/커넥션을 오래 점유한다.
//   Service 안에서 DTO 까지 변환해 반환하면 트랜잭션 경계 안에서 LAZY 가 안전하게 초기화된다.
// - 도메인 메서드(changeDepartment/updateProfile) vs setter: 규칙을 엔티티에 모아 호출부가 검증을 잊지 못하게.
// - soft(RESIGNED) vs hard delete: soft 는 휴가/결재/공지의 과거 참조 보존(인사 시스템 표준), hard 는 FK 무결성 위험.
// - employee.getUser().changeName(...) 은 Employee 를 통해 User 를 바꾸는 부수효과 — 같은 트랜잭션 dirty checking 으로 함께 반영.
