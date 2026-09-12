// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 등록(User+Employee 원자적 저장)을 거의 백지에서. TRD 3.7.2, 3.8.1, 3.10.1.
// 막히면 starter/08-employee-register, answers.md 8장 참고.

// 초보자 안내 (08장): Service와 트랜잭션
// 개념: Service는 업무 순서를 조율하고 트랜잭션은 여러 DB 변경을 하나의 성공·실패 단위로 묶습니다.
// 사용 이유: 직원 등록이 실패했는데 로그인 계정만 남는 부분 저장을 막습니다.
// 동작 흐름: 중복·부서 확인 → 비밀번호 해시 → User 저장 → Employee 저장 → 응답 변환 순서입니다. Spring이 관리하는 트랜잭션 경계 밖으로 RuntimeException이 전달되면 기본 설정에서 두 저장을 롤백합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
