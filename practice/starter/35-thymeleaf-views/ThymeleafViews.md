# Thymeleaf 화면 워크북

이 챕터의 개념과 설계 판단을 4지선다로 확인합니다. [선택·채점 화면](../../../problems.html#problem-35)에서 풀고 실제 구현·검증 결과를 [통합 워크북](../../job-preparation-workbook.md)에 남깁니다.

## 챕터 계약

| 구분 | 완료 기준 |
|---|---|
| 학습 목표 | Controller 모델과 Thymeleaf 템플릿·폼·검증 오류를 안전하게 연결한다. |
| 취업 결과물 | 로그인부터 휴가 신청·승인 결과까지 시연하는 최소 화면 |
| 완료 검증 | 모델 렌더링·HTML escape·CSRF·Validation 실패 표시를 화면 테스트로 확인한다. |

<!-- 객관식 시작 -->
## 4지선다 문제

### Q35-01. Thymeleaf 템플릿의 일반적인 위치와 구성은?

- ① src/main/resources/templates 아래에 도메인별 화면과 공통 fragments를 둔다.
- ② 모든 HTML을 .git 폴더에 둔다.
- ③ 템플릿은 DB 비밀번호 설정 파일 안에만 둔다.
- ④ templates에는 Java Entity만 둔다.

**정답:** ①

**해설:** 화면과 공통 조각을 분리해 찾기·재사용·유지보수를 쉽게 한다.

### Q35-02. 사용자 입력을 템플릿에 출력할 때 안전한 기본 선택은?

- ① 사용자 입력을 항상 th:utext로 그대로 출력한다.
- ② HTML escape를 수행하는 th:text를 사용한다.
- ③ 스크립트 태그를 허용하면 자동으로 안전해진다.
- ④ 비밀번호를 HTML 주석에 넣는다.

**정답:** ②

**해설:** th:utext는 HTML을 해석하므로 신뢰하지 않는 입력에는 정제·검증이 필요하다.

### Q35-03. CSRF 보호가 활성화된 폼 요청에 필요한 것은?

- ① 폼의 색상을 초록색으로 지정한다.
- ② 사용자 ID를 CSRF 토큰 대신 넣는다.
- ③ 서버가 기대하는 유효한 CSRF 토큰을 포함하고 인증·인가도 검사한다.
- ④ CSRF를 적용하면 모든 권한 검사를 제거한다.

**정답:** ③

**해설:** 쿠키 기반 인증 요청에서 토큰을 검증하며 CSRF와 권한 검사는 다른 목적이다.

### Q35-04. Thymeleaf 공통 화면 조각 재사용의 올바른 조합은?

- ① th:text로 DB 트랜잭션을 시작한다.
- ② th:utext로 로그인 역할을 설정한다.
- ③ 모든 화면에 헤더를 복사해야만 렌더링된다.
- ④ th:fragment로 조각을 정의하고 th:replace 등으로 포함한다.

**정답:** ④

**해설:** 공통 헤더·메뉴 등을 조각으로 관리하여 중복을 줄인다.

### Q35-05. @Controller와 @RestController의 차이로 알맞은 것은?

- ① Controller는 뷰 렌더링을 할 수 있고 RestController는 반환값을 응답 본문으로 전달한다.
- ② RestController에서 문자열을 반환하면 항상 템플릿 이름으로 처리된다.
- ③ Controller는 인증된 사용자만 자동 허용한다.
- ④ 두 어노테이션은 모든 동작과 응답 방식이 항상 같다.

**정답:** ①

**해설:** 화면용과 JSON API용 Controller를 역할에 맞게 나눠 함께 운영할 수 있다.

## 렌더링 코드 참고

```java
return "employee/list";
```

```html
<p th:text="${employee.name}"></p>
<tr th:each="emp : ${employees}"></tr>
<input th:field="*{name}">
<header th:replace="~{fragments/header :: header}"></header>
```

## 7. 심화 노트 (면접 답변 포인트)

- **th:text vs th:utext (XSS)**: `th:text` 는 `<`, `>`, `&` 를 escape 해 안전하다. `th:utext` 는 raw HTML 을 그대로 출력하므로 사용자 입력(공지 본문 등)에 쓰면 `<script>` 가 실행되는 **저장형 XSS** 가 된다. utext 는 관리자가 만든 신뢰된 HTML 에만.
- **SSR(Thymeleaf) vs SPA(React)**: SSR 은 초기 로딩이 빠르고 SEO 에 유리하며 배포가 단순(백엔드 한 덩어리). SPA 는 풍부한 상호작용과 프론트/백 분리가 강점이지만 빌드·CORS·인증 토큰 관리가 늘어난다. 백엔드 포트폴리오 데모로는 SSR 이 가볍다.
- **th:object / th:field**: 폼과 DTO 를 바인딩하고 검증 실패 시 필드별 에러 메시지를 자동 연결한다. 일반 `<input name=>` 보다 바인딩·에러 표시가 자동화된다.
- **화면도 인증이 필요**: `/leaves/admin` 같은 보호 화면은 JSON API 와 똑같이 인터셉터/Security 로 컨트롤러 진입 전에 막는다. "화면이라 그냥 보여준다" 가 가장 흔한 보안 구멍.
- **에러 페이지 커스텀**: `templates/error/404.html`, `templates/error/5xx.html` 을 두면 Spring Boot 가 자동으로 매핑한다.
