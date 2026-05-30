// 실제 구현 위치 예: config/*
// 목표: Auditing / PasswordEncoder / WebMvc 핵심 Bean 을 거의 백지에서. TRD 3.11, 3.5.
// 막히면 starter/33-config-beans, answers.md 33장 참고.

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

// 학습 질문 (직접 답):
// Q1. @EnableJpaAuditing 을 빼면 createdAt 이 null 인 이유는?
//     A:
// Q2. BCrypt 가 SHA-256 보다 안전한 2가지(salt, work factor).
//     A:
// Q3. WebMvcConfig 와 SecurityConfig 공존 시 권한 검사 순서는?
//     A:

// 자가 채점:
// □ @EnableJpaAuditing  □ new BCryptPasswordEncoder()  □ implements WebMvcConfigurer
// □ PasswordEncoder 는 정책이라 수동 @Bean  □ 1차/2차 동시 사용 금지
