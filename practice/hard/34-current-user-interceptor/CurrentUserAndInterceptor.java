// 실제 구현 위치 예: security/CurrentUser.java, AuthCheckInterceptor.java, CurrentUserArgumentResolver.java
// 목표: "로그인 사용자 ID 를 Controller 인자로 주입" 패턴을 거의 백지에서. TRD 3.11.1.
// 막히면 starter/34-current-user-interceptor, answers.md 34장 참고.

// 초보자 안내 (34장): Interceptor와 ArgumentResolver
// 개념: 인터셉터는 Controller 실행 전 요청을 확인하고 인자 해석기는 메서드에 넣을 값을 구합니다.
// 사용 이유: Controller마다 세션 유무 검사와 사용자 ID 추출 코드를 반복하지 않게 합니다.
// 동작 흐름: 인터셉터가 세션의 USER_ID를 확인하고 @CurrentUser 해석기가 이를 인자로 넘깁니다. @CurrentEmployee는 선언·사용 예만 있으므로 계정 ID로 Employee를 찾는 해석기를 별도로 구현해야 합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
