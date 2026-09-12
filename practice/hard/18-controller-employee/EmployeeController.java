// 실제 구현 위치 예: controller/EmployeeController.java
// 목표: 직원 REST API 매핑을 거의 백지에서. TRD 3.7.2.
// 막히면 starter/18-controller-employee, answers.md 18장 참고.

// 초보자 안내 (18장): Spring MVC와 REST Controller
// 개념: Controller는 HTTP 주소·메서드와 Java 메서드를 연결하는 입구입니다.
// 사용 이유: 통신 처리와 직원 등록 같은 업무 규칙을 나누어 변경하기 쉽게 합니다.
// 동작 흐름: 요청 본문 → 검증한 DTO → employeeService 호출 → 응답 DTO와 HTTP 상태 순서입니다. @PreAuthorize는 Spring Security와 메서드 보안을 활성화한 2차 단계에서 동작합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// TODO 01: REST 컨트롤러 + 공통 prefix.
@____
@RequestMapping("____")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // TODO 02: 등록 — HTTP 메서드?
    @____
    @PreAuthorize("hasRole('____')")
    public ResponseEntity<EmployeeResponse> register(
            // TODO 03: 본문 검증.
            @____ @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.register(request);
        // TODO 04: 생성 상태 코드.
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    // TODO 05: 목록 + 검색 + 페이징.
    @____
    @PreAuthorize("hasRole('ADMIN')")
    public Page<EmployeeResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            ____ pageable) {
        return employeeService.search(keyword, departmentId, pageable);
    }

    // TODO 06: 상세 — URL 식별자 받기.
    @GetMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse detail(@____ Long employeeId) {
        return employeeService.detail(employeeId);
    }

    // TODO 07: 수정(전체 갱신) — HTTP 메서드?
    @____("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse update(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        return employeeService.update(employeeId, request);
    }

    // TODO 08: 삭제/비활성 — 204 가 자연스러운 이유?
    @____("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity.____Content().build();
    }
}

// 학습 질문 (직접 답):
// Q1. @PreAuthorize 로 막았는데 Service 에서 또 검사해야 하나?
//     A:
// Q2. 등록 응답에 Location 헤더를 추가하려면?
//     A:
// Q3. PUT 과 PATCH 의 차이를 한 줄로.
//     A:

// 자가 채점:
// □ @RestController+@RequestMapping("/api/employees")  □ @PostMapping+hasRole('ADMIN')  □ @Valid
// □ HttpStatus.CREATED  □ @GetMapping list + Pageable  □ @PathVariable  □ @PutMapping  □ @DeleteMapping + noContent()
