// 중학생 눈높이: 로그인과 세션
// 로그인은 입력한 계정이 본인 것인지 확인하는 과정입니다.
// 세션은 서버가 기억하는 로그인 기록입니다. 비밀번호 해시는 원래 비밀번호 대신 비교에 쓰는 값입니다.
// 입력 받기 → 계정·비밀번호 확인 → 로그인 결과 또는 세션 처리 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/AuthService.java
// 목표: 로그인 / 로그아웃 / 내 정보 조회 / 비밀번호 변경. PRD 2.5.1, TRD 3.11 참고.

// 초보자 안내 (23장): BCrypt와 로그인 세션
// 개념: BCrypt는 비밀번호를 단방향 해시로 저장하는 방법이고 세션은 서버가 로그인 상태를 기억하는 공간입니다.
// 사용 이유: 평문 비밀번호 저장을 피하고 매 요청마다 비밀번호를 다시 보내지 않게 합니다.
// 동작 흐름: 가입은 encode, 로그인은 matches로 비교합니다. 성공 후 Controller가 세션 ID를 바꾸고 USER_ID·USER_ROLE을 저장하며 로그아웃은 세션을 폐기합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    // ===== 로그인 =====
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 로그인 Service는 DB 계정과 비밀번호를 검증하고 결과 DTO를 돌려줍니다. 세션 생성은 Controller 책임입니다.
        // 23장의 record DTO를 사용한다면 getEmail() 같은 호출을 email() 접근자와 맞추는 작업이 필요합니다.
        String email = request.getEmail().trim().toLowerCase();

        // TODO 01: 이메일 사용자가 존재하는지 확인. 존재 여부와 비번 불일치를
        //          같은 메시지로 응답하는 이유는?
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.____, "이메일 또는 비밀번호가 올바르지 않습니다."));

        // TODO 02: 비밀번호 검증. 평문이 아닌 해시 비교를 위한 메서드는?
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (!passwordEncoder.____(request.getPassword(), user.getPassword())) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 세션 생성과 ID 재발급은 웹 기술을 아는 Controller 에서 처리합니다.
        // Service 는 자격 증명 검증과 응답 생성에만 집중합니다.
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return LoginResponse.from(user);
    }

    // ===== 내 정보 조회 =====
    @Transactional(readOnly = true)
    public MyInfoResponse me(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));

        // 직원 정보가 함께 필요하면 Employee 도 조회
        Employee employee = employeeRepository.findByUser_Id(currentUserId).orElse(null);

        return MyInfoResponse.from(user, employee);
    }

    // ===== 비밀번호 변경 =====
    @Transactional
    public void changePassword(Long currentUserId, PasswordChangeRequest request) {

        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // TODO 03: 현재 비밀번호 확인. 본인이 맞는지 한 번 더 검증해야 합니다.
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.____, "현재 비밀번호가 일치하지 않습니다.");
        }

        // TODO 04: 새 비밀번호를 해시해 반영하세요.
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
// Q4. 로그인 조회와 비밀번호 변경의 트랜잭션 설정을 구분한 이유는? [chapter-v1-23-14]
// ① readOnly=true는 모든 환경에서 INSERT·UPDATE를 반드시 차단하는 권한 기능이다.
// ② 비밀번호를 변경해도 읽기 전용 힌트만 유지해야 한다.
// ③ readOnly 설정이 BCrypt 계산을 자동 수행한다.
// ④ 조회 업무에는 readOnly 힌트를, 해시 갱신 업무에는 쓰기 트랜잭션을 적용한다.
// 정답: ④
// 해설: readOnly는 조회 의도를 나타내는 최적화 힌트이며 강제 보안 장치로 가정하지 않는다. 쓰기 경로의 실제 저장을 검증한다.
//
// Q5. AuthService가 Servlet API를 직접 다루지 않게 하는 이유는? [chapter-v1-23-15]
// ① 인증 업무를 HTTP 환경과 분리해 단위 테스트와 다른 호출 경로에서 재사용하기 위해서다.
// ② 비밀번호 비교를 Controller의 문자열 ==로 옮기기 위해서다.
// ③ HTTP 정보가 없으면 어떤 사용자도 검증할 수 없기 때문이다.
// ④ 업무 계층에서 역할과 인증 결과를 모두 제거하기 위해서다.
// 정답: ①
// 해설: 입력·검증·업무 결과와 세션 생성·폐기를 분리한다. Service는 Servlet 요청 객체 없이도 테스트할 수 있다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - 사용자 열거(enumeration) 방지: "없는 이메일" 과 "비번 틀림" 을 같은 메시지 + 같은 status(401)로 응답.
//   여유가 있으면 응답 시간도 비슷하게(timing attack 방지) — 존재하지 않아도 dummy 해시 비교.
// - matches(raw, encoded): BCrypt 는 encoded 안에 박힌 salt 를 꺼내 raw 를 같은 salt 로 해시한 뒤 비교한다.
//   그래서 평문 == 비교가 불가능하고, DB 가 털려도 원문 복원이 어렵다.
// - Session Fixation 방어와 세션 저장은 AuthController 책임. Service 에 Servlet API 를 넣지 않아 단위 테스트를 단순하게 유지한다.
// - login()/me() 는 조회라 readOnly=true, changePassword() 는 쓰기라 일반 @Transactional.
