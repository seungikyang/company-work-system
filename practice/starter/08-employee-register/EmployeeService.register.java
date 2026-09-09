// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 등록 시 User + Employee 를 같은 트랜잭션으로 묶어 저장하세요.
//       TRD 3.7.2, 3.8.1, 3.10.1 참고.

// 초보자 안내 (08장): Service와 트랜잭션
// 개념: Service는 업무 순서를 조율하고 트랜잭션은 여러 DB 변경을 하나의 성공·실패 단위로 묶습니다.
// 사용 이유: 직원 등록이 실패했는데 로그인 계정만 남는 부분 저장을 막습니다.
// 동작 흐름: 중복·부서 확인 → 비밀번호 해시 → User 저장 → Employee 저장 → 응답 변환 순서입니다. Spring이 관리하는 트랜잭션 경계 밖으로 RuntimeException이 전달되면 기본 설정에서 두 저장을 롤백합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
//
// 심화 노트 (면접 답변 포인트):
// - 같은 메서드 = 같은 물리 트랜잭션. userRepository.save 후 employee.save 에서 RuntimeException 이 나면
//   둘 다 rollback (트러블슈팅 3.16.2 의 "부분 저장" 케이스).
// - checked exception 은 기본 commit → 필요하면 @Transactional(rollbackFor = Exception.class).
// - save 순서: User 를 먼저 영속화해야 Employee 의 FK(user_id)가 결정된다.
// - 정규화(trim().toLowerCase())는 중복검사 "전에" 1회 — User@x.com 과 user@x.com 분리 사고 방지.
