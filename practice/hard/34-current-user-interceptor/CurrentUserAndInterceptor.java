// 중학생 눈높이: 현재 사용자 확인
// 요청이 업무 코드에 들어가기 전에 로그인 여부를 확인하고 사용자 정보를 전달합니다.
// Interceptor는 창구 앞 확인 담당, ArgumentResolver는 메서드에 필요한 입력을 준비하는 담당입니다.
// 세션 확인 → 보호 요청 통과 여부 결정 → 사용자 ID를 메서드에 전달 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
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
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (session == null || session.getAttribute("USER_ID") == null) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new BusinessException(ErrorCode.____);
        }
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-34
//
// Q1. 현재 사용자 ArgumentResolver가 없으면 반복될 수 있는 코드는? [chapter-v1-34-05]
// ① 모든 Repository의 SQL 문법을 고치는 코드다.
// ② JPA 테이블 생성 코드를 모든 Controller에 쓰는 것이다.
// ③ CSS 색상 계산을 Java 메서드에 반복하는 것이다.
// ④ Controller마다 인증 주체·세션에서 사용자 ID를 추출하고 자료형·누락 상태를 처리하는 코드다.
// 정답: ④
// 해설: 공통 추출·실패 정책을 인자 해석기로 묶고 인증된 값을 전달한다. 업무 권한 검사는 별도로 적용한다.
//
// Q2. AuthenticationPrincipal과 사용자 정의 CurrentUser를 구분한 것은? [chapter-v1-34-06]
// ① CurrentUser라는 이름이면 해석기 없이 자동으로 사용자 ID를 얻는다.
// ② AuthenticationPrincipal은 언제나 모든 Entity를 DB에서 조회한다.
// ③ 전자는 Security 인증 principal을 해석하고 후자는 프로젝트의 등록된 해석기가 정한 세션·principal 규칙을 따른다.
// ④ 두 어노테이션이 어떤 구성에서도 반드시 같은 값을 반환한다.
// 정답: ③
// 해설: 어노테이션 이름보다 실제 해석기와 인증 자료의 출처를 확인한다. 반환 자료형과 누락 처리를 명시한다.
//
// Q3. Servlet Filter와 MVC Interceptor의 일반적인 진입 순서는? [chapter-v1-34-07]
// ① Interceptor가 모든 요청에서 필터보다 먼저 실행된다.
// ② 요청이 필터 체인을 지나 DispatcherServlet에 들어온 뒤 매핑된 핸들러의 Interceptor가 동작한다.
// ③ Filter는 Entity를 저장할 때만 실행된다.
// ④ Interceptor와 Filter는 등록 여부와 관계없이 모두 실행된다.
// 정답: ②
// 해설: HTTP 필터와 MVC 핸들러 처리의 범위를 구분한다. 응답 복귀 순서와 제외 경로는 등록 구성에 따라 확인한다.
//
// Q4. JWT 인증으로 전환할 때 CurrentUser 해석기의 입력을 바꾸는 방향은? [chapter-v1-34-08]
// ① 검증된 토큰에서 만든 SecurityContext·principal 등 인증 결과를 사용해 현재 사용자 정보를 해석한다.
// ② 검증하지 않은 토큰 payload의 userId만 무조건 신뢰한다.
// ③ 클라이언트가 보낸 ADMIN 문자열을 현재 역할로 사용한다.
// ④ 세션 객체 이름만 JWT로 바꾸고 검증 단계는 생략한다.
// 정답: ①
// 해설: 토큰 검증은 인증 단계에서 수행하고 인자 해석기는 신뢰할 수 있는 인증 결과를 전달한다. 기존 세션 전제도 제거·조정한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ ElementType.PARAMETER  □ implements HandlerInterceptor  □ AUTHENTICATION_REQUIRED
// □ hasParameterAnnotation(CurrentUser.class)  □ getAttribute("USER_ID")
// □ Filter → Interceptor → ArgumentResolver 순
