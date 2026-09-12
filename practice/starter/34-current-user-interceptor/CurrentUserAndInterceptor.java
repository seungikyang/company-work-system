// 실제 구현 위치 예:
//   - src/main/java/com/example/companywork/security/AuthCheckInterceptor.java
//   - src/main/java/com/example/companywork/security/CurrentUser.java
//   - src/main/java/com/example/companywork/security/CurrentUserArgumentResolver.java
// 목표: 세션 기반 1차 보안에서 “로그인 사용자 ID 를 Controller 메서드 인자로 주입” 하는
//       표준 패턴을 채우세요. TRD 3.11.1 참고.

// 초보자 안내 (34장): Interceptor와 ArgumentResolver
// 개념: 인터셉터는 Controller 실행 전 요청을 확인하고 인자 해석기는 메서드에 넣을 값을 구합니다.
// 사용 이유: Controller마다 세션 유무 검사와 사용자 ID 추출 코드를 반복하지 않게 합니다.
// 동작 흐름: 인터셉터가 세션의 USER_ID를 확인하고 @CurrentUser 해석기가 이를 인자로 넘깁니다. @CurrentEmployee는 선언·사용 예만 있으므로 계정 ID로 Employee를 찾는 해석기를 별도로 구현해야 합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
    // MVC가 어떤 인자를 이 해석기에 맡길지 묻는 단계입니다. 어노테이션과 타입이 모두 맞는지 확인합니다.
    public boolean supportsParameter(MethodParameter parameter) {
        // TODO 03: @CurrentUser 가 붙어 있고 타입이 Long 일 때만 처리.
        return parameter.hasParameterAnnotation(____.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    // 선택된 인자의 실제 값을 만드는 단계입니다. 서버 세션에서 계정 ID를 읽어 반환합니다.
    // 계정 ID와 직원 ID는 다르므로 CurrentEmployee에는 계정으로 직원을 조회하는 별도 연결이 필요합니다.
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
//
// 심화 노트 (면접 답변 포인트):
// - ArgumentResolver 가 없으면 Controller 마다 session.getAttribute("USER_ID") + null 체크 + (Long) 캐스팅이 반복된다.
//   이 중복을 한 곳에 모으는 것이 ArgumentResolver 의 목적.
// - 실행 순서: Servlet Filter → DispatcherServlet → Interceptor.preHandle → ArgumentResolver → Controller 메서드.
//   (인증 누락 차단은 Interceptor 에서 1차, 주입 실패는 ArgumentResolver 에서 2차)
// - @AuthenticationPrincipal(Security 표준, SecurityContext 에서 추출) vs @CurrentUser(직접 만든 세션 기반). 같은 목적, 다른 출처.
// - JWT 진화: ArgumentResolver 가 세션 대신 SecurityContextHolder.getContext().getAuthentication() 에서 사용자 정보를 꺼내도록 바뀐다.
