// 실제 구현 위치 예: service/EmployeeService.java
// 목표: 직원 상세/수정/퇴사를 거의 백지에서. PRD 2.5.2 (FR-EMP-003~005).
// 막히면 starter/28-employee-detail-update, answers.md 28장 참고.

@Transactional(readOnly = true)
public EmployeeResponse detail(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.____));
    // TODO 01: LAZY 가 응답 변환 시점에 풀린다(트랜잭션 안에서 DTO 변환).
    return EmployeeResponse.from(employee);
}

@Transactional
public EmployeeResponse update(Long employeeId, EmployeeUpdateRequest request) {

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

    // TODO 02: 부서 변경 시 부서 존재 확인 + 도메인 메서드.
    if (request.getDepartmentId() != null
            && !request.getDepartmentId().equals(employee.getDepartment().getId())) {
        Department newDept = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new BusinessException(ErrorCode.____));
        employee.____(newDept);
    }

    // TODO 03: 직급/연락처는 도메인 메서드로.
    employee.updateProfile(request.getPosition(), request.getPhone());

    // TODO 04: 이름 변경은 Employee 의 user 를 통해.
    if (request.getName() != null && !request.getName().isBlank()) {
        employee.getUser().changeName(request.getName().trim());
    }
    return EmployeeResponse.from(employee);
}

@Transactional
public void delete(Long employeeId, boolean hardDelete) {

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

    if (hardDelete) {
        // TODO 05: 휴가 기록이 있으면 hard delete 금지.
        long leaveCount = leaveRepository.countByEmployee_Id(employeeId);
        if (leaveCount > 0) {
            throw new BusinessException(ErrorCode.____, "휴가 기록이 있는 직원은 삭제할 수 없습니다. 퇴사 처리로 대신하세요.");
        }
        employeeRepository.delete(employee);
    } else {
        // TODO 06: 일반적으로는 soft delete.
        employee.____();
    }
}

// 학습 질문 (직접 답):
// Q1. Service 가 setter 대신 도메인 메서드를 만든 이유는?
//     A:
// Q2. 부서 변경 시 부서 직원 수 캐시가 있다면 어디서 갱신?
//     A:
// Q3. soft(RESIGNED) vs hard delete 트레이드오프 한 줄.
//     A:

// 자가 채점:
// □ EMPLOYEE_NOT_FOUND  □ DEPARTMENT_NOT_FOUND  □ changeDepartment(newDept)  □ INVALID_STATUS  □ resign()
