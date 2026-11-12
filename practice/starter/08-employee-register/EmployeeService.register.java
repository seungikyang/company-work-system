// 중학생 눈높이: 직원 등록
// 직원 등록은 로그인 계정 카드와 직원 카드를 한 쌍으로 만드는 일입니다.
// 트랜잭션은 함께 성공하거나 함께 취소할 작업 묶음입니다. 계정만 남는 상황을 막습니다.
// 중복과 부서 확인 → 계정 만들기 → 직원 만들기 → 응답 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
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

    // request는 Controller가 넘긴 입력 DTO입니다. 문자열 메서드의 결과를 email에 담고 같은 값으로 조회·저장합니다.
    String email = request.getEmail().trim().toLowerCase();

    // TODO 02: 이메일 중복 검사. 어떤 ErrorCode 를 던질까요?
    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (employeeRepository.existsByUser_Email(email)) {
        // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 03: 사번 중복 검사.
    if (employeeRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
        throw new BusinessException(ErrorCode.____);
    }

    // TODO 04: 부서가 존재하는지 확인.
    // findById 결과는 없을 수도 있습니다. Optional.orElseThrow는 값이 없을 때만 예외 생성 함수를 실행합니다.
    // () -> 는 나중에 호출할 함수를 나타내며, 부서가 없으면 아래 계정 생성으로 진행하지 않습니다.
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

    // savedUser를 연결해 계정과 인사 정보를 짝지읍니다. 두 Repository 작업을 감싸는 트랜잭션이 필요합니다.
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
    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return EmployeeResponse.____(saved);
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-08
//
// Q1. 직원 등록 전체에 하나의 트랜잭션이 없으면 가능한 문제는? [chapter-v1-08-05]
// ① User만 저장되고 Employee 저장이 실패해 계정과 인사 정보가 부분 저장될 수 있다.
// ② Repository save를 여러 번 호출하면 항상 한 트랜잭션으로 자동 묶인다.
// ③ 트랜잭션이 없으면 읽기만 가능하고 INSERT는 절대 실행되지 않는다.
// ④ User와 Employee는 서로 다른 데이터여서 부분 저장이 항상 정상이다.
// 정답: ①
// 해설: 여러 저장 작업을 하나의 업무 원자성 경계로 묶는다. Repository 개별 트랜잭션만으로 전체 업무의 원자성을 보장하지 못한다.
//
// Q2. 같은 트랜잭션에서 Employee 저장의 RuntimeException이 밖으로 전파된 경우 기본 기대 결과는? [chapter-v1-08-06]
// ① 이미 수행한 User 저장도 함께 롤백된다.
// ② User 저장만 무조건 커밋된다.
// ③ 실패한 Employee 저장은 항상 자동 재시도되어 성공한다.
// ④ RuntimeException이면 트랜잭션 처리와 무관하게 HTTP 200을 반환한다.
// 정답: ①
// 해설: 기본 롤백 규칙과 예외 전파를 전제로 검증한다. 예외를 삼키거나 트랜잭션 경계가 다르면 결과가 달라질 수 있다.
//
// Q3. 이메일 사전 중복 검사와 DB unique의 역할을 올바르게 연결한 것은? [chapter-v1-08-07]
// ① 사전 검사만으로 동시 요청도 완전히 직렬화된다.
// ② unique가 있으면 오류 응답은 필요 없다.
// ③ 사전 검사는 친절한 오류 안내에, unique는 동시 요청의 최종 무결성 보장에 사용한다.
// ④ 브라우저 입력 검사가 DB 제약을 대체한다.
// 정답: ③
// 해설: 조회 후 저장 사이의 경쟁을 고려하고 unique 충돌을 일관된 업무 오류로 처리한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - Spring 프록시의 트랜잭션 경계 안에서 두 저장이 같은 트랜잭션에 참여해야 부분 저장을 막는다.
//   RuntimeException이 경계 밖으로 전달되면 기본 설정에서 rollback (트러블슈팅 3.16.2 참고).
// - checked exception 은 기본 commit → 필요하면 @Transactional(rollbackFor = Exception.class).
// - save 순서: User 를 먼저 영속화해야 Employee 의 FK(user_id)가 결정된다.
// - 정규화(trim().toLowerCase())는 중복검사 "전에" 1회 — User@x.com 과 user@x.com 분리 사고 방지.
