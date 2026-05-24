// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 등록 시 User + Employee 를 같은 트랜잭션으로 묶어 저장하세요.
//       PRD/TRD 3.7.2, 3.8.1, 3.10.1 참고.

// TODO 01: 이 메서드가 트랜잭션 안에서 실행되도록 선언하세요.
@____
public EmployeeResponse register(EmployeeCreateRequest request) {

    String email = request.getEmail().trim().toLowerCase();

    // TODO 02: 이메일 중복 검사. 어떤 ErrorCode 를 던질까요?
    if (employeeRepository.existsByUser_Email(email)) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 03: 사번 중복 검사.
    if (employeeRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 04: 부서가 존재하는지 확인.
    Department department = departmentRepository.findById(request.getDepartmentId())
        .orElseThrow(() -> new BusinessException(ErrorCode.____));

    // TODO 05: 비밀번호는 절대 평문으로 저장하지 않습니다.
    User user = User.create(
        email,
        passwordEncoder.____(request.getPassword()),
        request.getName().trim(),
        UserRole.USER
    );

    // TODO 06: User 가 먼저 저장되어야 Employee 의 FK 를 채울 수 있습니다.
    User savedUser = userRepository.____(user);

    Employee employee = Employee.create(
        savedUser,
        department,
        request.getEmployeeNumber(),
        request.getPosition(),
        request.getPhone(),
        request.getHireDate()
    );

    Employee saved = employeeRepository.save(employee);

    // TODO 07: Entity 를 그대로 반환하지 말고 응답 DTO 로 변환하세요. 이유는?
    return EmployeeResponse.____(saved);
}

// 학습 질문 (한 줄 답을 적어 보세요):
// Q1. 이 메서드에서 @Transactional 이 빠지면 어떤 데이터 정합성 문제가 생기는가?
//     A:
// Q2. userRepository.save 가 성공한 뒤 employeeRepository.save 에서 RuntimeException 이 나면?
//     A:
// Q3. 중복 이메일 검증을 했는데도 unique 제약을 두는 이유는?
//     A:
