// 중학생 눈높이: 직원 요청 창구
// Controller는 브라우저의 요청을 받는 창구입니다. 직원 관련 요청을 Service에 맡깁니다.
// API는 다른 프로그램이 정해진 주소와 형식으로 작업을 요청하는 약속입니다.
// 요청 주소·방식 확인 → 입력 검사 → Service 호출 → 결과 응답 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/EmployeeController.java
// 목표: 직원 관리 REST API 의 매핑을 채우세요. TRD 3.7.2 참고.

// 초보자 안내 (18장): Spring MVC와 REST Controller
// 개념: Controller는 HTTP 주소·메서드와 Java 메서드를 연결하는 입구입니다.
// 사용 이유: 통신 처리와 직원 등록 같은 업무 규칙을 나누어 변경하기 쉽게 합니다.
// 동작 흐름: 요청 본문 → 검증한 DTO → employeeService 호출 → 응답 DTO와 HTTP 상태 순서입니다. @PreAuthorize는 Spring Security와 메서드 보안을 활성화한 2차 단계에서 동작합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// TODO 01: REST 컨트롤러 + 공통 prefix.
@____
@RequestMapping("____")
@RequiredArgsConstructor
public class EmployeeController {

    // final 필드는 필요한 협력 객체를 보관합니다. Lombok 생성자를 통해 Spring이 Service를 주입하도록 구성합니다.
    // 요청마다 Controller가 Service를 새로 만들 필요가 없어 테스트에서 다른 구현을 넣기도 쉽습니다.
    private final EmployeeService employeeService;

    // TODO 02: 신규 직원 등록. 어떤 HTTP 메서드일까요?
    @____
    @PreAuthorize("hasRole('ADMIN')") // Spring Security 적용 시
    public ResponseEntity<EmployeeResponse> register(
            // TODO 03: 요청 본문에 대해 검증을 적용하려면?
            @____ @RequestBody EmployeeCreateRequest request) {

        EmployeeResponse response = employeeService.register(request);

        // TODO 04: 신규 자원을 만들었음을 표현하는 상태 코드는?
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    // TODO 05: 직원 목록 조회 + 검색 + 페이징.
    //          Pageable 을 Controller 가 그대로 받으면 ?page=&size=&sort= 가 자동 매핑됩니다.
    @____
    @PreAuthorize("hasRole('ADMIN')")
    public Page<EmployeeResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            ____ pageable) {
        return employeeService.search(keyword, departmentId, pageable);
    }

    // TODO 06: 직원 상세 조회. URL 의 식별자를 어떤 어노테이션으로 받나?
    @GetMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse detail(@____ Long employeeId) {
        return employeeService.detail(employeeId);
    }

    // TODO 07: 직원 수정 (전체 갱신). HTTP 메서드는?
    @____("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse update(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        return employeeService.update(employeeId, request);
    }

    // TODO 08: 직원 비활성/삭제. 상태 코드 204 가 자연스러운 이유는?
    @____("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity.____Content().build();
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-18
//
// Q1. Controller 인가와 Service 업무 권한 검사의 관계로 알맞은 것은? [chapter-v1-18-04]
// ① HTTP 진입점의 역할 인가와 Service의 소유권·업무 가드를 각각 필요한 범위에서 유지한다.
// ② Controller 검사가 배치·다른 Service 호출에도 항상 자동 적용된다.
// ③ @PreAuthorize 하나로 모든 데이터 소유권이 자동 확인된다.
// ④ Service에는 인증된 행위자 정보가 절대로 필요 없다.
// 정답: ①
// 해설: 역할 기반 진입 제한과 자료별 업무 권한은 구분한다. 다른 호출 경로에서도 핵심 규칙을 지킨다.
//
// Q2. 직원 생성 성공 응답에 Location을 넣는 일반적인 방법은? [chapter-v1-18-05]
// ① 기존 모든 자원의 목록을 비밀번호와 함께 헤더에 넣는다.
// ② Location에는 HTTP 상태 숫자만 넣는다.
// ③ GET 요청을 다시 보내기 전에는 생성 URI를 응답할 수 없다.
// ④ 생성 자원의 URI로 ResponseEntity.created(uri).body(response)를 구성한다.
// 정답: ④
// 해설: Location은 생성된 자원의 위치를 알린다. 201 응답과 실제 접근 가능한 URI 계약을 함께 확인한다.
//
// Q3. PUT과 PATCH의 요청 의미를 올바르게 구분한 것은? [chapter-v1-18-06]
// ① PUT은 읽기, PATCH는 삭제를 의미한다.
// ② 두 메서드는 언제나 같은 생략 필드 처리 규칙을 가진다.
// ③ PUT은 자원 표현의 교체 의도, PATCH는 부분 변경 의도를 나타낸다.
// ④ PATCH는 인증 없이 실행해야 한다.
// 정답: ③
// 해설: 전체 교체와 부분 변경의 계약을 구분하고 생략·null·공백의 처리와 멱등성 요구를 명시한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - 권한 다층: @PreAuthorize(1차, HTTP 경계) + Service requireAdmin(2차). Controller 에만 두면 Service 재사용 경로가 무방비.
// - 201 Created + Location 헤더: ServletUriComponentsBuilder 로 새 자원 URI 를 헤더에 담으면 REST 규약에 맞다.
// - PUT = 전체 교체(누락 필드는 비워짐), PATCH = 부분 갱신. 직원 수정은 의도에 맞는 쪽을 선택.
// - 삭제는 204 No Content(응답 본문 없음). 목록은 Page<EmployeeResponse> 로 — 엔티티 직접 노출 금지.
