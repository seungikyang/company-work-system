// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 등록(User+Employee 원자적 저장)을 거의 백지에서. TRD 3.7.2, 3.8.1, 3.10.1.
// 막히면 starter/08-employee-register, answers.md 8장 참고.

// TODO 01: 트랜잭션 선언.
@____
public EmployeeResponse register(EmployeeCreateRequest request) {

    // TODO 02: 이메일 정규화 — 중복검사 "전에" 1회.
    String email = request.getEmail().____().____();

    // TODO 03: 이메일 중복.
    if (employeeRepository.existsByUser_Email(email)) {
        throw new ____(ErrorCode.____);
    }

    // TODO 04: 사번 중복.
    if (employeeRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 05: 부서 존재 확인.
    ____ department = departmentRepository.findById(request.getDepartmentId())
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 06: 비밀번호 해시(평문 저장 금지).
    User user = User.create(
        email,
        passwordEncoder.____(request.getPassword()),
        request.getName().trim(),
        UserRole.____
    );

    // TODO 07: User 먼저 영속화(FK 결정).
    User savedUser = userRepository.____(user);

    Employee employee = Employee.create(
        savedUser, department,
        request.getEmployeeNumber(), request.getPosition(),
        request.getPhone(), request.getHireDate()
    );
    Employee saved = employeeRepository.save(employee);

    // TODO 08: Entity 직접 반환 금지 — DTO 로.
    return EmployeeResponse.____(saved);
}

// 학습 질문 (직접 답):
// Q1. @Transactional 이 빠지면 어떤 정합성 문제가 생기나?
//     A:
// Q2. userRepository.save 성공 후 employee.save 에서 RuntimeException 이 나면?
//     A:
// Q3. 중복 이메일 검증을 했는데도 DB unique 가 필요한 이유는?
//     A:

// 자가 채점:
// □ @Transactional  □ trim().toLowerCase()  □ DUPLICATE_EMAIL  □ DUPLICATE_EMPLOYEE_NUMBER
// □ DEPARTMENT_NOT_FOUND  □ passwordEncoder.encode  □ UserRole.USER  □ userRepository.save  □ EmployeeResponse.from
