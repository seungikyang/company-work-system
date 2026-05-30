// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 상세 / 수정 / 비활성·삭제 흐름을 채우세요. PRD 2.5.2 (FR-EMP-003~005) 참고.

@Transactional(readOnly = true)
public EmployeeResponse detail(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 01: User 와 Department 가 LAZY 로 묶여 있다면 응답 변환 시점에 어떤 일이 일어날까요?
    return EmployeeResponse.from(employee);
}

@Transactional
public EmployeeResponse update(Long employeeId, EmployeeUpdateRequest request) {

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

    // TODO 02: 부서를 바꾸는 경우, 부서가 실제 존재하는지 검증한다.
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

    if (hardDelete) {
        // TODO 05: 하드 삭제는 휴가/결재/공지에서 참조 무결성이 깨질 수 있습니다.
        //          어떤 정책으로 막거나 허용해야 할까요?
        long leaveCount = leaveRepository.countByEmployee_Id(employeeId);
        if (leaveCount > 0) {
            throw new BusinessException(ErrorCode.INVALID_STATUS,
                    "휴가 기록이 있는 직원은 삭제할 수 없습니다. 퇴사 처리로 대신하세요.");
        }
        employeeRepository.delete(employee);
    } else {
        // TODO 06: 일반적으로는 hard delete 보다 어떤 처리가 안전한가요?
        employee.____();
    }
}

// 학습 질문:
// Q1. Service 에서 employee.setName(...) 처럼 setter 를 직접 쓰지 않고 도메인 메서드를 만든 이유는?
//     A:
// Q2. 부서 변경 시 기존 부서의 직원 수 캐시 같은 게 있다면 동시에 갱신해야 한다. 어디서 처리하는 게 좋은가?
//     A:
// Q3. soft delete (RESIGNED) 와 hard delete 의 트레이드오프를 한 줄로 정리해 보세요.
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - open-in-view 함정: true 면 LAZY 연관이 뷰 렌더 시점까지 풀려 편하지만 영속성 컨텍스트/커넥션을 오래 점유한다.
//   Service 안에서 DTO 까지 변환해 반환하면 트랜잭션 경계 안에서 LAZY 가 안전하게 초기화된다.
// - 도메인 메서드(changeDepartment/updateProfile) vs setter: 규칙을 엔티티에 모아 호출부가 검증을 잊지 못하게.
// - soft(RESIGNED) vs hard delete: soft 는 휴가/결재/공지의 과거 참조 보존(인사 시스템 표준), hard 는 FK 무결성 위험.
// - employee.getUser().changeName(...) 은 Employee 를 통해 User 를 바꾸는 부수효과 — 같은 트랜잭션 dirty checking 으로 함께 반영.
