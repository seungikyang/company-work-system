// 실제 구현 위치 예: security/CurrentUser.java, AuthCheckInterceptor.java, CurrentUserArgumentResolver.java
// 목표: "로그인 사용자 ID 를 Controller 인자로 주입" 패턴을 거의 백지에서. TRD 3.11.1.
// 막히면 starter/34-current-user-interceptor, answers.md 34장 참고.

// 1. @CurrentUser
// TODO 01: 메서드 파라미터에만 붙도록.
@Target(ElementType.____)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser { }

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentEmployee { }

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserRole { }

// 2. AuthCheckInterceptor
@Component
@RequiredArgsConstructor
public class AuthCheckInterceptor implements ____ {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        HttpSession session = req.getSession(false);
        // TODO 02: 세션/USER_ID 없으면 인증 필요.
        if (session == null || session.getAttribute("USER_ID") == null) {
            throw new BusinessException(ErrorCode.____);
        }
        return true;
    }
}

// 3. CurrentUserArgumentResolver
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // TODO 03: @CurrentUser 가 붙고 타입이 Long 일 때만.
        return parameter.hasParameterAnnotation(____.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpSession session = ((HttpServletRequest) webRequest.getNativeRequest()).getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        // TODO 04: 세션에서 USER_ID 추출.
        Object userId = session.getAttribute("____");
        if (userId == null) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        return (Long) userId;
    }
}

// 학습 질문 (직접 답):
// Q1. ArgumentResolver 가 없다면 Controller 마다 어떤 코드가 반복?
//     A:
// Q2. @AuthenticationPrincipal 과 @CurrentUser 의 차이는?
//     A:
// Q3. Interceptor 와 Filter 의 실행 순서 한 줄.
//     A:
// Q4. JWT 단계로 가면 이 ArgumentResolver 는 어떻게 바뀌나?
//     A:

// 자가 채점:
// □ ElementType.PARAMETER  □ implements HandlerInterceptor  □ AUTHENTICATION_REQUIRED
// □ hasParameterAnnotation(CurrentUser.class)  □ getAttribute("USER_ID")
// □ Filter → Interceptor → ArgumentResolver 순
