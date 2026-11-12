// 중학생 눈높이: 직원 검색과 페이지
// 많은 직원을 한 번에 보여 주지 않고 조건에 맞는 일부씩 나눠 가져옵니다.
// Pageable은 몇 번째 묶음을 가져올지 적은 요청표이고, Page는 결과와 전체 개수 정보를 담습니다.
// 검색 조건 고르기 → DB에서 한 페이지 찾기 → 화면에 필요한 정보로 바꾸기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 목록을 검색 + 페이징으로 반환하세요. PRD 2.5.2 (FR-EMP-006/007) 참고.

// 초보자 안내 (09장): Pageable과 Page
// 개념: Pageable은 페이지 번호·크기·정렬 조건이고 Page는 조회 결과와 전체 건수 등의 페이지 정보입니다.
// 사용 이유: 직원이 많아도 한 화면에서 필요한 분량만 조회합니다.
// 동작 흐름: 검색 조건과 Pageable을 Repository에 전달하고 Page.map으로 각 Employee를 응답 DTO로 바꿉니다. 페이지 정보는 유지되며 전체 건수 계산에 추가 쿼리가 필요할 수 있습니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// TODO 01: 읽기 전용 트랜잭션을 명시하면 성능과 의도를 동시에 챙길 수 있습니다.
@Transactional(readOnly = ____)
public Page<EmployeeResponse> search(String keyword, Long departmentId, Pageable pageable) {

    // 검색 조건이 다양해질 때는 Querydsl/Specification 이 더 깔끔하지만,
    // 학습 단계에서는 if 분기로 충분합니다.

    // Page는 조회 항목과 전체 개수 등 페이지 정보를 담고 Pageable은 요청한 페이지·크기·정렬 조건입니다.
    // 이름과 부서가 모두 있는 첫 분기는 아래 예제에서 부서 조건을 아직 적용하지 않으므로 복합 검색 구현이 필요합니다.
    Page<Employee> page;

    // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
    if (keyword != null && !keyword.isBlank() && departmentId != null) {
        // TODO 02: 이름/사번 검색 + 부서 조건이 동시에 들어온 경우.
        //          여기서는 학습용으로 단순하게 두 단계 필터를 거치는 예시를 보여줍니다.
        page = employeeRepository.findByUser_NameContainingOrEmployeeNumberContaining(
            keyword, keyword, pageable);
        // 학습 메모: 위 쿼리는 부서 조건을 반영하지 못하므로,
        //           실제로는 @Query 나 Specification 으로 합쳐서 1회 쿼리에 처리하는 편이 정확합니다.
    } else if (keyword != null && !keyword.isBlank()) {
        page = employeeRepository.____(keyword, keyword, pageable);
    } else if (departmentId != null) {
        page = employeeRepository.findByDepartment_Id(departmentId, pageable);
    } else {
        page = employeeRepository.____(pageable);
    }

    // TODO 03: Page<Entity> 를 Page<DTO> 로 변환하세요. (페이지 메타 정보가 유지됩니다)
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
//
// 심화 노트 (면접 답변 포인트):
// - @Transactional(readOnly=true) → Hibernate flush 모드 MANUAL, dirty checking 생략 → 성능 + 의도 표현.
// - 이 if 분기는 keyword+departmentId 동시 입력 시 부서조건을 못 건다(학습용 한계).
//   실무는 Querydsl/Specification 으로 동적 조건을 1회 쿼리에 결합.
// - page.map(...) 은 content 만 DTO 로 바꾸고 totalElements/totalPages 등 메타를 유지한다.
//   List 로 바꾸면 프론트 페이지네이션이 깨진다.
