// 중학생 눈높이: 공통 도구 설정
// 여러 코드가 함께 쓸 비밀번호 도구와 시간 기록·웹 설정 등을 준비합니다.
// Bean은 Spring이 만들고 관리하는 객체입니다. 필요한 곳에 같은 도구를 연결해 줍니다.
// 필요한 도구 확인 → 생성·설정 → 어느 기능에 연결하는지 읽기 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: config/*
// 목표: Auditing / PasswordEncoder / WebMvc 핵심 Bean 을 거의 백지에서. TRD 3.11, 3.5.
// 막히면 starter/33-config-beans, answers.md 33장 참고.

// 초보자 안내 (33장): Bean과 설정
// 개념: Bean은 Spring이 생성·관리하는 객체이며 설정 클래스는 이 객체와 기능을 등록하는 곳입니다.
// 사용 이유: 여러 Service가 같은 방식으로 인코더 등을 주입받고 공통 설정을 사용하도록 합니다.
// 동작 흐름: @Bean 메서드가 PasswordEncoder를 제공하고 Auditing 설정이 생성·수정 시각 기록을 돕습니다. WebMvc 설정은 34장의 인터셉터와 인자 해석기를 실제 요청 처리에 연결합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// 1. JPA Auditing
// TODO 01: createdAt/updatedAt 자동 채움 활성화 어노테이션.
@Configuration
@____
public class JpaAuditingConfig {
    // 엔티티에 @EntityListeners(AuditingEntityListener.class) 도 필요.
}

// 2. PasswordEncoder
@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO 02: salt + work factor. SHA-256 만으로 부족한 이유?
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return new ____();
    }
}

// 3. (학습 1차) 세션 인터셉터 + ArgumentResolver 등록
// TODO 03: 인터셉터/리졸버를 묶어 등록하는 인터페이스.
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements ____ {

    private final AuthCheckInterceptor authCheckInterceptor;
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authCheckInterceptor)
            // TODO 04: 보호 제외 경로(로그인/정적/H2)를 직접 채우세요.
            .excludePathPatterns(
                "/api/auth/login", "/api/auth/logout", "/h2-console/**",
                "/css/**", "/js/**", "/images/**"
            )
            .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}

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
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ @EnableJpaAuditing  □ new BCryptPasswordEncoder()  □ implements WebMvcConfigurer
// □ PasswordEncoder 는 정책이라 수동 @Bean  □ 1차/2차 동시 사용 금지
