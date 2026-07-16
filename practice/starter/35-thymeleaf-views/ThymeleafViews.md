# Thymeleaf 화면 워크북

PRD 2.7 의 화면 요구사항을 Thymeleaf 템플릿으로 옮길 때 외워야 할 기본기를 채워봅니다. 화면은 백엔드 포트폴리오에서도 데모 페이지로 자주 쓰입니다.

워크북 프로그램에서 화면의 목적은 디자인 기능을 늘리는 것이 아니라 백엔드 업무 흐름을 면접관이 직접 확인할 수 있게 만드는 것입니다. 핵심 시나리오를 시연할 최소 화면만 구현하고, 인증·검증·에러 표시를 함께 확인합니다.

---

## 1. 디렉터리 구조 채우기

```text
src/main/resources/
├── ____            # Thymeleaf 템플릿 (.html)
│   ├── fragments/
│   │   ├── header.html
│   │   └── nav.html
│   ├── auth/
│   │   └── login.html
│   ├── employee/
│   │   ├── list.html
│   │   ├── detail.html
│   │   └── form.html
│   ├── leave/
│   │   ├── request.html
│   │   ├── my-list.html
│   │   └── admin-list.html
│   ├── notice/
│   │   ├── list.html
│   │   └── detail.html
│   └── approval/
│       ├── my-list.html
│       └── pending.html
└── ____            # 정적 리소스 (.css, .js, .png)
    ├── css/
    └── js/
```

---

## 2. 기본 문법 빈칸 채우기

### A. 네임스페이스 선언

```html
<!DOCTYPE html>
<html xmlns:th="____">
<head>...</head>
```

### B. 텍스트 출력

```html
<!-- TODO 01: model 에 담긴 employee.name 을 escape 처리하여 출력 -->
<span th:____="${employee.name}">기본값</span>
```

### C. 조건 / 반복

```html
<!-- TODO 02: 직원 목록이 비어 있으면 안내 메시지 -->
<p th:____="${#lists.isEmpty(employees)}">등록된 직원이 없습니다.</p>

<!-- TODO 03: 반복문 -->
<tr th:____="emp : ${employees}">
    <td th:text="${emp.employeeNumber}">EMP-001</td>
    <td th:text="${emp.name}">홍길동</td>
    <td th:text="${emp.departmentName}">개발팀</td>
</tr>
```

### D. URL / 폼

```html
<!-- TODO 04: 동적 URL — /api/employees/{id} -->
<a th:____="@{/employees/{id}(id=${emp.employeeId})}">상세</a>

<!-- TODO 05: CSRF 토큰을 폼에 자동으로 넣는 방법 -->
<form th:action="@{/api/leaves}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.____}"/>
    ...
</form>
```

### E. Fragment 재사용

```html
<!-- header.html -->
<header th:____="header">
    <h1>사내 업무관리 시스템</h1>
</header>

<!-- 사용처 -->
<div th:____="~{fragments/header :: header}"></div>
```

---

## 3. Controller 가 view 를 반환하는 흐름

```java
@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeViewController {

    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       Pageable pageable) {
        Page<EmployeeResponse> page = employeeService.search(keyword, null, pageable);
        // TODO 06: 템플릿에서 ${employees}, ${page} 로 접근하기 위해 어떻게 담을까?
        model.____("employees", page.getContent());
        model.addAttribute("page", page);
        return "employee/list"; // → templates/employee/list.html
    }
}
```

`@RestController` 와 `@Controller` 의 차이를 한 줄로 적어 보세요.

> A:

---

## 4. JSON API 와 View 동시 운영

REST API 와 Thymeleaf 페이지를 같은 프로젝트에 두는 패턴:

| 경로 | 역할 | 컨트롤러 |
|---|---|---|
| `/api/**` | JSON 응답 | `@RestController` |
| `/employees`, `/leaves`, ... | 화면 응답 | `@Controller` (View 이름 반환) |

이 구조는 PRD 2.7 화면 요구사항과 TRD 3.7 API 명세를 자연스럽게 동시에 만족시킵니다.

---

## 5. 자주 빠뜨리는 보안 포인트

- [ ] `th:text` 가 아닌 `th:utext` 를 쓰면 HTML escape 가 사라집니다. 어떤 경우에만 사용 가능한가요?
- [ ] 로그인하지 않은 사용자가 보호 화면(/leaves/admin) 에 접근할 때 어디서 막아야 하나요?
- [ ] 폼 제출에 CSRF 토큰이 빠지면 어떤 상황에서 문제가 되나요?
- [ ] 응답에 비밀번호 같은 민감 정보를 model 에 담아 두지 않았는지 점검했나요?

---

## 6. 학습 질문

- Q1. SPA(React 등) 대신 Thymeleaf 를 선택했을 때의 장점/단점을 한 줄씩 적어 보세요.
- Q2. JSP 와 Thymeleaf 의 가장 큰 차이는?
- Q3. Thymeleaf 의 `th:object`, `th:field` 가 일반 form 과 다른 점은?
- Q4. 페이지가 깨졌을 때 Spring Boot 기본 에러 페이지를 커스텀하려면 어디에 어떤 템플릿을 두면 될까요? (templates/error/*.html)

### 완료 후 남길 증거

- [ ] 로그인부터 휴가 신청·승인 결과 확인까지 화면으로 시연할 수 있다.
- [ ] Validation 오류와 권한 실패가 사용자에게 안전하게 표시된다.
- [ ] 대표 화면 캡처 또는 데모 링크를 [취업 준비 통합 워크북](../../job-preparation-workbook.md)에 기록했다.

---

## 7. 심화 노트 (면접 답변 포인트)

- **th:text vs th:utext (XSS)**: `th:text` 는 `<`, `>`, `&` 를 escape 해 안전하다. `th:utext` 는 raw HTML 을 그대로 출력하므로 사용자 입력(공지 본문 등)에 쓰면 `<script>` 가 실행되는 **저장형 XSS** 가 된다. utext 는 관리자가 만든 신뢰된 HTML 에만.
- **SSR(Thymeleaf) vs SPA(React)**: SSR 은 초기 로딩이 빠르고 SEO 에 유리하며 배포가 단순(백엔드 한 덩어리). SPA 는 풍부한 상호작용과 프론트/백 분리가 강점이지만 빌드·CORS·인증 토큰 관리가 늘어난다. 백엔드 포트폴리오 데모로는 SSR 이 가볍다.
- **th:object / th:field**: 폼과 DTO 를 바인딩하고 검증 실패 시 필드별 에러 메시지를 자동 연결한다. 일반 `<input name=>` 보다 바인딩·에러 표시가 자동화된다.
- **화면도 인증이 필요**: `/leaves/admin` 같은 보호 화면은 JSON API 와 똑같이 인터셉터/Security 로 컨트롤러 진입 전에 막는다. "화면이라 그냥 보여준다" 가 가장 흔한 보안 구멍.
- **에러 페이지 커스텀**: `templates/error/404.html`, `templates/error/5xx.html` 을 두면 Spring Boot 가 자동으로 매핑한다.
