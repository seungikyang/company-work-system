// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/EmployeeController.java
// 목표: 직원 관리 REST API 의 매핑을 채우세요. TRD 3.7.2 참고.

// TODO 01: REST 컨트롤러 + 공통 prefix.
@____
@RequestMapping("____")
@RequiredArgsConstructor
public class EmployeeController {

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
