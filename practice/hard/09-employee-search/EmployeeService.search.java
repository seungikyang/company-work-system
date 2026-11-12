// 중학생 눈높이: 직원 검색과 페이지
// 많은 직원을 한 번에 보여 주지 않고 조건에 맞는 일부씩 나눠 가져옵니다.
// Pageable은 몇 번째 묶음을 가져올지 적은 요청표이고, Page는 결과와 전체 개수 정보를 담습니다.
// 검색 조건 고르기 → DB에서 한 페이지 찾기 → 화면에 필요한 정보로 바꾸기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 검색 + 페이징을 거의 백지에서. PRD 2.5.2 (FR-EMP-006/007).
// 막히면 starter/09-employee-search, answers.md 9장 참고.

// 초보자 안내 (09장): Pageable과 Page
// 개념: Pageable은 페이지 번호·크기·정렬 조건이고 Page는 조회 결과와 전체 건수 등의 페이지 정보입니다.
// 사용 이유: 직원이 많아도 한 화면에서 필요한 분량만 조회합니다.
// 동작 흐름: 검색 조건과 Pageable을 Repository에 전달하고 Page.map으로 각 Employee를 응답 DTO로 바꿉니다. 페이지 정보는 유지되며 전체 건수 계산에 추가 쿼리가 필요할 수 있습니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// TODO 01: 읽기 전용 트랜잭션.
@Transactional(readOnly = ____)
public Page<EmployeeResponse> search(String keyword, Long departmentId, Pageable pageable) {

    Page<Employee> page;

    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (keyword != null && !keyword.isBlank() && departmentId != null) {
        // 학습용 단순화: 부서조건 결합은 Querydsl/Specification 으로(메모만)
        page = employeeRepository.findByUser_NameContainingOrEmployeeNumberContaining(
            keyword, keyword, pageable);
    } else if (keyword != null && !keyword.isBlank()) {
        // TODO 02: 이름/사번 검색.
        page = employeeRepository.____(keyword, keyword, pageable);
    } else if (departmentId != null) {
        page = employeeRepository.findByDepartment_Id(departmentId, pageable);
    } else {
        // TODO 03: 전체 조회.
        page = employeeRepository.____(pageable);
    }

    // TODO 04: Page<Entity> → Page<DTO> (메타 유지).
    // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
    return page.____(EmployeeResponse::from);
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-09
//
// Q1. Pageable 요청의 일반적인 쿼리 파라미터 조합은? [chapter-v1-09-04]
// ① offsetName=ADMIN&password=plain
// ② page=0&size=20&sort=name,asc
// ③ pageSize를 URL 경로의 직원 ID로만 보낸다.
// ④ Pageable은 JSON 본문으로만 전달할 수 있다.
// 정답: ②
// 해설: 기본 페이지 번호는 0부터 시작하며 페이지 크기·정렬 파라미터를 사용한다. 실제 설정으로 최대 크기와 허용 정렬도 관리한다.
//
// Q2. 직원 검색 응답을 List 대신 Page DTO로 반환하는 이점은? [chapter-v1-09-05]
// ① Page가 모든 검색 결과를 한 번에 메모리에 넣는다.
// ② Page는 로그인 역할을 자동 검증한다.
// ③ 현재 결과와 함께 페이지 번호·크기·전체 건수 등 탐색 정보를 전달한다.
// ④ Page이면 응답 DTO 변환이 필요 없다.
// 정답: ③
// 해설: 클라이언트가 다음 페이지를 탐색할 정보를 제공한다. 전체 건수 조회 비용에 따라 Slice 같은 대안도 검토한다.
//
// Q3. 클라이언트가 정렬 필드를 자유롭게 지정할 때 필요한 방어는? [chapter-v1-09-06]
// ① 모든 요청 값을 SQL 문자열에 그대로 연결한다.
// ② 정렬 요청이면 권한 검사를 생략한다.
// ③ 필드명 오류는 언제나 자동 수정된다고 가정한다.
// ④ 허용 필드·방향과 최대 페이지 크기를 제한하고 비용 큰 조회·잘못된 속성을 거절한다.
// 정답: ④
// 해설: 일반적인 속성 기반 정렬과 문자열 SQL 연결의 위험을 구분한다. 허용 목록과 성능·오류 처리를 검증한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ readOnly=true  □ findByUser_NameContainingOrEmployeeNumberContaining  □ findAll(pageable)  □ page.map(...)
