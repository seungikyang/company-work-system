// 실제 구현 위치 예:
//   - src/main/java/com/example/companywork/security/AuthCheckInterceptor.java
//   - src/main/java/com/example/companywork/security/CurrentUser.java
//   - src/main/java/com/example/companywork/security/CurrentUserArgumentResolver.java
// 목표: 세션 기반 1차 보안에서 “로그인 사용자 ID 를 Controller 메서드 인자로 주입” 하는
//       표준 패턴을 채우세요. PRD/TRD 3.11.1 참고.

// =====================================================================
// 1. @CurrentUser — 커스텀 어노테이션
// =====================================================================
// TODO 01: 메서드 파라미터에만 붙일 수 있도록 ElementType 을 채우세요.
@Target(ElementType.____)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser { }

// 같은 방식으로 @CurrentEmployee, @CurrentUserRole 도 만들 수 있습니다.
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentEmployee { }

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserRole { }


// =====================================================================
// 2. AuthCheckInterceptor — 보호 자원 진입 전 세션 확인
// =====================================================================
@Component
@RequiredArgsConstructor
public class AuthCheckInterceptor implements ____ {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {

        // 정적 리소스 / 로그인 자체는 WebMvcConfig 의 exclude 로 빠집니다.

        HttpSession session = req.getSession(false);

        // TODO 02: 세션이 없거나 USER_ID 가 없으면 로그인 필요로 응답.
        if (session == null || session.getAttribute("USER_ID") == null) {
            throw new BusinessException(ErrorCode.____);
        }

        return true;
    }
}


// =====================================================================
// 3. CurrentUserArgumentResolver — @CurrentUser Long userId 를 자동으로 주입
// =====================================================================
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // TODO 03: @CurrentUser 가 붙어 있고 타입이 Long 일 때만 처리.
        return parameter.hasParameterAnnotation(____.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpSession session = ((HttpServletRequest) webRequest.getNativeRequest()).getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        // TODO 04: 세션에서 USER_ID 를 꺼내 Long 으로 반환.
        Object userId = session.getAttribute("____");
        if (userId == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        return (Long) userId;
    }
}


// =====================================================================
// 4. 사용 예 (Controller)
// =====================================================================
// @PostMapping("/api/leaves")
// public LeaveResponse request(@CurrentEmployee Long currentEmployeeId,
//                              @Valid @RequestBody LeaveCreateRequest request) { ... }
//
// → 세션이 없으면 ArgumentResolver 에서 AUTHENTICATION_REQUIRED 가 떨어지고,
//   GlobalExceptionHandler 가 일관된 401 응답으로 변환합니다.


// =====================================================================
// 5. 학습 질문
// =====================================================================
// Q1. ArgumentResolver 가 없다면 Controller 마다 어떤 코드가 반복되어야 할까?
//     A:
// Q2. @AuthenticationPrincipal 과 @CurrentUser 의 차이는?
//     A:
// Q3. Interceptor 와 Filter 의 실행 순서를 한 줄로 설명해 보세요.
//     A:
// Q4. JWT 단계로 진화하면 이 ArgumentResolver 는 어떻게 바뀌어야 하나?
//     A:
