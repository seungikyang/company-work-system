# 보안 흐름 워크북

TRD 3.11 에서 정의한 1차/2차/3차 보안 진화를 직접 채워봅니다. 아래 빈칸과 질문에 한 줄씩 답을 적어 두면, 면접에서 “이 시스템의 보안은 어떻게 진화시킬 수 있나요?”에 자연스럽게 답할 수 있습니다.

---

## 1차: 세션 기반 로그인

### 흐름

```text
1. 클라이언트가 POST /api/auth/login 으로 email + password 전송
2. AuthService 가 ____Repository.findByEmail(email) 로 사용자 조회
3. passwordEncoder.____(rawPassword, user.getPassword()) 로 비밀번호 검증
4. 성공 시 HttpSession 에 사용자 ID 와 Role 저장
   session.setAttribute("USER_ID", user.getId());
   session.setAttribute("USER_ROLE", user.getRole());
5. 이후 요청마다 ____Interceptor 가 세션을 확인하여 currentUser 를 구성
```

### TODO

- [ ] `JSESSIONID` 쿠키의 `HttpOnly`, `Secure`, `SameSite` 옵션을 설정한 이유를 한 줄씩 적어 보세요.
- [ ] 비밀번호를 평문으로 저장하면 PRD 의 어떤 비기능 요구사항을 위반하나요?
- [ ] 로그인 실패 시 `email` 이 존재하지 않는지, 비밀번호가 틀린지 메시지에서 구분하면 안 되는 이유는?

### 학습 질문

- Q. 세션 기반 인증의 가장 큰 단점은? (스케일 아웃 관점)
- Q. 같은 사용자가 두 브라우저에서 로그인하면 세션은 몇 개가 만들어지나요?

---

## 2차: Spring Security + BCrypt + Role 기반 접근 제어

### 핵심 구성요소

| 요소 | 역할 |
|---|---|
| `SecurityFilterChain` | 필터 체인 정의 (formLogin, csrf, authorizeHttpRequests …) |
| `UserDetailsService` | DB 의 사용자 정보를 Spring Security 가 이해할 수 있는 형태로 변환 |
| `____PasswordEncoder` | 단방향 해시 + salt 자동 처리 |
| `@PreAuthorize` / `hasRole(...)` | 메서드/엔드포인트 단위 권한 검사 |

### 빈칸 채우기

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.____())  // REST API + JWT 단계에서는 disable. 세션+폼 로그인 단계에서는 활성.
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/h2-console/**").____()
            .requestMatchers("/api/admin/**").hasRole("____")
            .requestMatchers("/api/approvals/pending", "/api/approvals/*/approve", "/api/approvals/*/reject")
                .hasAnyRole("APPROVER", "ADMIN")
            .anyRequest().____()
        )
        .formLogin(form -> form.loginProcessingUrl("/api/auth/login").permitAll())
        .logout(logout -> logout.logoutUrl("/api/auth/logout"));
    return http.build();
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new ____PasswordEncoder();
}
```

### 학습 질문

- Q. `hasRole("ADMIN")` 과 `hasAuthority("ROLE_ADMIN")` 의 차이는?
- Q. CSRF 보호를 disable 해도 되는 조건은?

---

## 3차: JWT 무상태 인증

### 흐름

```text
1. POST /api/auth/login → AccessToken + (선택) RefreshToken 발급
2. 클라이언트가 Authorization: ____ {token} 헤더로 요청
3. JwtAuthenticationFilter 가 헤더를 파싱하여 SecurityContext 에 Authentication 주입
4. 서버는 ____ 상태이므로, 토큰만 유효하면 어떤 인스턴스든 인증을 처리 가능
```

### TODO

- [ ] JWT payload 에 어떤 클레임을 담을지 적어 보세요. (sub, role, exp 등)
- [ ] AccessToken 의 만료 시간을 짧게 두는 이유는?
- [ ] RefreshToken 을 도입하면 어떤 새로운 보안 위협이 생기나요?
- [ ] 토큰 서명에 사용하는 비밀키를 환경 변수로 분리해야 하는 이유는?

### 비교 표 (직접 채워보세요)

| 항목 | 세션 | JWT |
|---|---|---|
| 상태 위치 | 서버 메모리 | ____ |
| 강제 로그아웃 | 세션 삭제로 즉시 가능 | ____ |
| 분산 환경 | 세션 클러스터링/Redis 필요 | ____ |
| 페이로드 변조 | 서버 메모리라 불가능 | 서명 검증으로 차단 |
| 모바일 친화도 | 쿠키 관리 필요 | ____ |

---

## 권한 정책 매트릭스 (TRD 3.11.3 채우기)

| 기능 | USER | ADMIN | APPROVER |
|---|---|---|---|
| 내 정보 조회 | ____ | 가능 | 가능 |
| 직원 관리 | ____ | 가능 | ____ |
| 부서 관리 | ____ | 가능 | ____ |
| 휴가 신청 | 가능 | 가능 | 가능 |
| 휴가 승인 | ____ | 가능 | ____ |
| 공지 등록 | ____ | 가능 | ____ |
| 결재 작성 | 가능 | 가능 | 가능 |
| 결재 승인 | ____ | 가능 | 가능 |

---

## 셀프 체크

- [ ] 1차/2차/3차 보안 흐름을 도식 없이 말로 설명할 수 있는가?
- [ ] BCrypt 가 단순 SHA-256 보다 안전한 이유를 한 줄로 답할 수 있는가?
- [ ] 권한 검사가 다층(필터 → 어노테이션 → 서비스 가드)으로 있어야 하는 이유를 답할 수 있는가?

---

## 심화 노트 (면접 답변 포인트)

- **BCrypt vs SHA-256**: BCrypt 는 **salt 를 자동 포함**하고 **work factor(cost)** 로 의도적으로 느리게 만들어 무차별 대입(brute force)을 어렵게 한다. SHA-256 은 빠르고 salt 가 없어 같은 비밀번호가 같은 해시 → 레인보우 테이블에 취약하다. "비밀번호 해시는 빠르면 안 된다" 가 핵심.
- **세션의 스케일아웃 한계**: 세션은 서버 메모리에 상태를 둔다. 인스턴스를 늘리면 sticky session 또는 Redis 세션 공유가 필요하다. JWT 는 상태를 토큰(클라이언트)에 두어 무상태 → 수평 확장이 쉽다. 대가는 **강제 로그아웃이 어렵다**(블랙리스트 / 짧은 만료 + RefreshToken 으로 보완).
- **CSRF disable 조건**: 세션+쿠키 인증은 브라우저가 쿠키를 자동 전송하므로 CSRF 토큰이 필요하다. JWT 를 `Authorization` 헤더로 보내면 쿠키 자동 전송이 아니라서 CSRF 위험이 줄어 disable 할 수 있다. (H2 콘솔 사용을 위해 disable 하는 것과는 이유가 다르다)
- **hasRole vs hasAuthority**: `hasRole("ADMIN")` 은 내부적으로 `ROLE_ADMIN` 권한을 찾는다(접두사 자동 부여). `hasAuthority("ROLE_ADMIN")` 은 접두사를 직접 적는다. 권한 문자열을 어떻게 저장했는지와 맞춰야 한다.
- **다층 권한**: 필터(인증 — 누구인가) → `@PreAuthorize`(인가 — 역할) → Service 가드(도메인 권한·소유자 — 본인 데이터인가). 각 층이 다른 질문에 답한다.
