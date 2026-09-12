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

// 학습 질문:
// Q1. @PreAuthorize 가 Controller 에서 검사한 권한을 Service 에서 또 검사해야 할까?
//     A:
// Q2. 직원 등록 응답에 Location 헤더를 추가하려면 어떻게 할까?
//     A:
// Q3. PUT 과 PATCH 의 의미 차이를 한 줄로 설명해 보세요.
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - 권한 다층: @PreAuthorize(1차, HTTP 경계) + Service requireAdmin(2차). Controller 에만 두면 Service 재사용 경로가 무방비.
// - 201 Created + Location 헤더: ServletUriComponentsBuilder 로 새 자원 URI 를 헤더에 담으면 REST 규약에 맞다.
// - PUT = 전체 교체(누락 필드는 비워짐), PATCH = 부분 갱신. 직원 수정은 의도에 맞는 쪽을 선택.
// - 삭제는 204 No Content(응답 본문 없음). 목록은 Page<EmployeeResponse> 로 — 엔티티 직접 노출 금지.
