// 중학생 눈높이: 로그인과 세션
// 로그인은 입력한 계정이 본인 것인지 확인하는 과정입니다.
// 세션은 서버가 기억하는 로그인 기록입니다. 비밀번호 해시는 원래 비밀번호 대신 비교에 쓰는 값입니다.
// 입력 받기 → 계정·비밀번호 확인 → 로그인 결과 또는 세션 처리 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: service/AuthService.java
// 목표: 로그인/로그아웃/내 정보/비밀번호 변경을 거의 백지에서. PRD 2.5.1, TRD 3.11.
// 막히면 starter/23-auth-login, answers.md 23장 참고.

// 초보자 안내 (23장): BCrypt와 로그인 세션
// 개념: BCrypt는 비밀번호를 단방향 해시로 저장하는 방법이고 세션은 서버가 로그인 상태를 기억하는 공간입니다.
// 사용 이유: 평문 비밀번호 저장을 피하고 매 요청마다 비밀번호를 다시 보내지 않게 합니다.
// 동작 흐름: 가입은 encode, 로그인은 matches로 비교합니다. 성공 후 Controller가 세션 ID를 바꾸고 USER_ID·USER_ROLE을 저장하며 로그아웃은 세션을 폐기합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = ____)
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().____().____();

        // TODO 01: 사용자 없음/비번 불일치를 같은 메시지로.
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.____, "이메일 또는 비밀번호가 올바르지 않습니다."));

        // TODO 02: 해시 비교.
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (!passwordEncoder.____(request.getPassword(), user.getPassword())) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 세션 생성/회전/저장은 Controller 책임.
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return LoginResponse.from(user);
    }

    @Transactional(readOnly = true)
    public MyInfoResponse me(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));
        Employee employee = employeeRepository.findByUser_Id(currentUserId).orElse(null);
        return MyInfoResponse.from(user, employee);
    }

    @Transactional
    public void changePassword(Long currentUserId, PasswordChangeRequest request) {
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        // TODO 03: 현재 비번 확인.
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.____, "현재 비밀번호가 일치하지 않습니다.");
        }
        // TODO 04: 새 비번 해시 후 반영.
        user.changePassword(passwordEncoder.____(request.getNewPassword()));
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-23
//
// Q1. 로그인 실패에서 이메일 없음과 비밀번호 오류를 같은 응답으로 처리하는 이유는? [chapter-v1-23-11]
// ① 응답 차이를 이용한 계정 존재 추측을 줄이기 위해서다.
// ② 사용자가 모든 관리자 비밀번호를 알아야 하기 때문이다.
// ③ 같은 메시지면 모든 인증 공격이 완전히 사라지기 때문이다.
// ④ DB에 존재하는 이메일을 실패 응답에 모두 나열하기 위해서다.
// 정답: ①
// 해설: 일관된 오류 메시지 외에도 응답 시간·시도 제한·로그 정책 등 계정 추측 경로를 검토한다.
//
// Q2. 로그인 성공 직후 세션 ID를 재발급하는 이유는? [chapter-v1-23-12]
// ① 세션 ID가 비밀번호 해시를 대체하기 때문이다.
// ② 공격자가 로그인 전에 정한 세션 ID를 재사용하는 세션 고정 공격을 줄이기 위해서다.
// ③ 재발급하면 CSRF·XSS 등 모든 공격도 자동 제거되기 때문이다.
// ④ 모든 로그인 사용자가 같은 세션 ID를 공유해야 하기 때문이다.
// 정답: ②
// 해설: 인증 전후 세션 식별자를 바꾸고 필요한 세션 속성·쿠키 정책을 유지한다. 다른 보안 방어도 별도로 필요하다.
//
// Q3. 비밀번호 변경 후 기존 세션·토큰에 대한 안전한 정책은? [chapter-v1-23-13]
// ① 비밀번호를 바꾸면 모든 JWT가 구현 없이 자동 폐기된다.
// ② 탈취된 기존 세션을 항상 영구 허용한다.
// ③ 기존 인증 상태의 무효화·토큰 버전·재인증 정책을 정하고 실제 적용 여부를 검증한다.
// ④ 로그아웃 화면만 표시하고 서버 상태는 유지한다.
// 정답: ③
// 해설: 세션과 JWT의 무효화 방법은 다르다. 저장소·버전·만료 등 선택한 정책으로 이전 인증 자료의 사용을 제한한다.
//
// Q4. AuthService가 Servlet API를 직접 다루지 않게 하는 이유는? [chapter-v1-23-15]
// ① 인증 업무를 HTTP 환경과 분리해 단위 테스트와 다른 호출 경로에서 재사용하기 위해서다.
// ② 비밀번호 비교를 Controller의 문자열 ==로 옮기기 위해서다.
// ③ HTTP 정보가 없으면 어떤 사용자도 검증할 수 없기 때문이다.
// ④ 업무 계층에서 역할과 인증 결과를 모두 제거하기 위해서다.
// 정답: ①
// 해설: 입력·검증·업무 결과와 세션 생성·폐기를 분리한다. Service는 Servlet 요청 객체 없이도 테스트할 수 있다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ login readOnly=true  □ AUTHENTICATION_REQUIRED  □ passwordEncoder.matches
// □ Service 에 HttpSession 없음  □ USER_NOT_FOUND  □ passwordEncoder.encode
