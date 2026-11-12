// 중학생 눈높이: 공통 도구 설정
// 여러 코드가 함께 쓸 비밀번호 도구와 시간 기록·웹 설정 등을 준비합니다.
// Bean은 Spring이 만들고 관리하는 객체입니다. 필요한 곳에 같은 도구를 연결해 줍니다.
// 필요한 도구 확인 → 생성·설정 → 어느 기능에 연결하는지 읽기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/config/*
// 목표: 학습용 세션 / Security / Audit 단계에서 필요한 핵심 Bean 들을 채우세요.
//       TRD 3.11 (보안), 3.5 (Audit 컬럼) 참고.

// 초보자 안내 (33장): Bean과 설정
// 개념: Bean은 Spring이 생성·관리하는 객체이며 설정 클래스는 이 객체와 기능을 등록하는 곳입니다.
// 사용 이유: 여러 Service가 같은 방식으로 인코더 등을 주입받고 공통 설정을 사용하도록 합니다.
// 동작 흐름: @Bean 메서드가 PasswordEncoder를 제공하고 Auditing 설정이 생성·수정 시각 기록을 돕습니다. WebMvc 설정은 34장의 인터셉터와 인자 해석기를 실제 요청 처리에 연결합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// =====================================================================
// 1. JPA Auditing 활성화 — createdAt/updatedAt 자동 채움
// =====================================================================
// TODO 01: @CreatedDate, @LastModifiedDate 를 사용하려면 부트스트랩에 어떤 어노테이션이 필요한가?
@Configuration
@____
public class JpaAuditingConfig {
    // 비어 있어도 됩니다. Entity 쪽에서 @EntityListeners(AuditingEntityListener.class) 를 잊지 마세요.
}


// =====================================================================
// 2. PasswordEncoder Bean — 평문 저장 금지의 인프라
// =====================================================================
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO 02: 단방향 해시 + salt 자동 처리. SHA-256 만으로는 왜 부족한가요?
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return new ____PasswordEncoder();
    }
}


// =====================================================================
// 3. (학습 1차) 세션 기반 인증 인터셉터 등록
// =====================================================================
// TODO 03: HandlerInterceptor 와 ArgumentResolver 를 묶어 등록하려면 어떤 인터페이스를 구현해야 하나?
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements ____ {

    private final AuthCheckInterceptor authCheckInterceptor;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authCheckInterceptor)
            // TODO 04: 어떤 경로를 보호 대상에서 제외해야 할까요? (로그인, 정적 리소스, H2 콘솔)
            .excludePathPatterns(
                "/api/auth/login",
                "/api/auth/logout",
                "/h2-console/**",
                "/css/**", "/js/**", "/images/**"
            )
            .addPathPatterns("/api/**");
    }

    @Override
    // 구현 클래스를 작성한 뒤 MVC에 등록해야 Controller 인자를 해석할 때 사용됩니다.
    // 여기는 CurrentUser 해석기만 등록합니다. 직원 ID·역할용 어노테이션에는 별도 해석·등록이 필요합니다.
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}


// =====================================================================
// 4. (학습 2차) Spring Security 단계의 SecurityFilterChain — 보강용 미리보기
// =====================================================================
// 이 단계는 TRD 3.11.2 의 “확장 구현” 입니다.
// 1차(세션) 단계와 2차(Security) 단계를 동시에 켜지 않습니다.
//
// @Configuration
// @EnableMethodSecurity
// public class SecurityConfig {
//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable())
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
//                 .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                 .anyRequest().authenticated()
//             )
//             .formLogin(form -> form.loginProcessingUrl("/api/auth/login").permitAll())
//             .logout(logout -> logout.logoutUrl("/api/auth/logout"));
//         return http.build();
//     }
// }


// =====================================================================
// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-33
//
// Q1. JPA Auditing 활성화가 빠졌을 때 생성 시각이 채워지지 않는 원인은? [chapter-v1-33-05]
// ① createdAt 컬럼은 DB가 언제나 자동으로 현재 시각을 넣는다.
// ② @CreatedDate는 단순 자바 필드만으로 항상 동작한다.
// ③ 시간 필드가 LocalDateTime이면 어떤 설정도 필요 없다.
// ④ Auditing 인프라와 리스너 연결이 실제 저장 경로에서 동작하지 않기 때문이다.
// 정답: ④
// 해설: @CreatedDate·리스너·@EnableJpaAuditing과 저장 경로를 확인한다. DB 기본값 등 별도 시각 공급 방식과 구분한다.
//
// Q2. 비밀번호 저장에서 BCrypt와 단순 SHA-256의 차이로 알맞은 것은? [chapter-v1-33-06]
// ① SHA-256 문자열에 BCrypt라는 이름을 붙이면 같은 보호가 된다.
// ② BCrypt 결과에서 원문 비밀번호를 복호화할 수 있다.
// ③ BCrypt는 salt와 조절 가능한 계산 비용을 사용해 비밀번호 추측 비용을 높이는 목적의 해시다.
// ④ salt가 있으면 계산 비용과 비밀번호 정책은 필요 없다.
// 정답: ③
// 해설: 빠른 일반 해시와 비밀번호 전용 해시의 목적을 구분한다. 환경에서 비용을 측정하고 적절한 강도를 정한다.
//
// Q3. 일반적인 Servlet MVC 요청에서 Security와 MVC 검사 순서는? [chapter-v1-33-07]
// ① MVC Interceptor가 모든 Servlet Filter보다 항상 먼저 실행된다.
// ② Security 필터 체인 뒤에 DispatcherServlet의 매핑·Interceptor·컨트롤러 처리 등이 이어진다.
// ③ JPA 리스너가 Security 필터보다 먼저 HTTP 인증을 수행한다.
// ④ WebMvcConfigurer는 Security 필터 체인을 자동 제거한다.
// 정답: ②
// 해설: 필터와 MVC 내부 처리의 경계를 구분한다. 실제 등록·제외 경로와 메서드 인가 설정도 확인한다.
//
// Q4. 이 실습에서 PasswordEncoder를 직접 Bean으로 구성하는 이유는? [chapter-v1-33-08]
// ① 선택한 인코더와 설정을 주입할 수 있도록 명시적으로 등록해야 하기 때문이다.
// ② crypto 의존성 하나가 모든 애플리케이션에 같은 Bean을 무조건 등록한다.
// ③ @EnableJpaAuditing이 BCrypt Bean을 자동으로 생성한다.
// ④ PasswordEncoder는 new로 만들면 절대 사용할 수 없다.
// 정답: ①
// 해설: 인코더 라이브러리 제공과 애플리케이션 Bean 등록은 다르다. 실제 구성에 Bean이 존재하고 Service에 주입되는지 확인한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - @EnableJpaAuditing 이 없으면 AuditingEntityListener 가 콜백을 받지 못해 createdAt 이 null → 33장 트러블슈팅 직결.
//   엔티티의 @EntityListeners(AuditingEntityListener.class) 와 "둘 다" 있어야 동작.
// - PasswordEncoder 가 자동 등록 안 되는 이유: 어떤 인코더(BCrypt/Argon2/Pbkdf2)를 쓸지는 정책이라 Spring 이 임의로 정하지 않는다 → 명시적 @Bean.
// - 1차(세션 인터셉터)와 2차(SecurityFilterChain)를 동시에 켜면 인증 책임이 두 곳에 흩어져 디버깅이 어렵다 → 단계 전환.
// - WebMvcConfigurer: addInterceptors(요청 진입 전후 가로채기) + addArgumentResolvers(컨트롤러 인자 주입).
