// 중학생 눈높이: 휴가 흐름 시험
// 테스트는 가짜 요청을 보내 예상대로 처리되는지 자동으로 확인하는 연습입니다.
// MockMvc는 브라우저를 직접 누르지 않고 웹 요청을 시험하는 도구입니다. 예상 결과와 실제 결과를 비교합니다.
// 시험 자료 준비 → 휴가 신청 → 관리자 승인 → 결과와 실패 상황 확인 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/test/java/com/example/companywork/integration/LeaveFlowTest.java
// 목표: 휴가 신청 → 관리자 승인 → 결과 확인 흐름을 MockMvc 통합 테스트로 채우세요.
//       TRD 3.12와 현재 1차 세션 인증 흐름을 함께 검증합니다.

// 초보자 안내 (21장): JUnit 5와 MockMvc
// 개념: JUnit은 테스트를 실행하고 MockMvc는 실제 네트워크 서버를 띄우지 않고 Spring MVC 요청 처리를 시험합니다.
// 사용 이유: 휴가 신청부터 승인까지의 성공·실패를 수정 후에도 반복 확인합니다.
// 동작 흐름: 테스트 데이터를 준비하고 로그인·신청·승인 요청을 보낸 뒤 응답 상태와 저장 결과를 비교합니다. 브라우저의 실제 클릭이나 운영 DB의 모든 특성까지 검증하는 것은 아닙니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LeaveFlowTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("직원이 휴가를 신청하면 관리자가 승인까지 갈 수 있다")
    void leaveRequest_then_adminApprove_flow() throws Exception {

        // 1. (사전) 부서/관리자/직원 시드 데이터는 @BeforeEach 또는 setup 메서드로 준비했다고 가정.
        // 두 세션은 서로 다른 로그인 사용자를 재현합니다. 각 요청에 어느 세션을 붙였는지가 권한 검사의 입력입니다.
        MockHttpSession employeeSession = loginAs("employee@test.com", "pass1234!");
        MockHttpSession adminSession    = loginAs("admin@test.com", "adminpw!");

        // 2. 휴가 신청
        Map<String, Object> body = Map.of(
            "leaveType", "ANNUAL",
            "startDate", "2026-06-01",
            "endDate",   "2026-06-03",
            "reason",    "개인 사유"
        );

        // TODO 01: 로그인 응답에서 얻은 세션을 다음 요청에 연결하세요.
        // perform은 요청을 실행하고 andExpect는 실제 응답을 예상값과 비교합니다.
        // MockMvc가 실제 MVC 처리를 호출하려면 실행 프로젝트의 Bean과 테스트 계정 데이터가 준비되어야 합니다.
        MvcResult requestResult = mockMvc.perform(post("/api/leaves")
                .____(employeeSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            // TODO 02: 새 자원이 생성되었으므로 어떤 status?
            .andExpect(status().is____())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andReturn();

        // TODO 03: 응답에서 leaveId 를 꺼내 다음 요청에 사용한다.
        Long leaveId = objectMapper.readTree(requestResult.getResponse().getContentAsString())
            .get("____").asLong();

        // 3. 관리자 승인
        mockMvc.perform(patch("/api/admin/leaves/{leaveId}/approve", leaveId)
                .session(adminSession))
            .andExpect(status().isOk())
            // TODO 04: 응답의 status 가 APPROVED 로 바뀌어야 한다.
            .andExpect(jsonPath("$.status").value("____"));

        // 4. 직원이 본인 휴가를 다시 조회하면 APPROVED 상태여야 한다.
        mockMvc.perform(get("/api/leaves/{leaveId}", leaveId)
                .session(employeeSession))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("일반 사용자가 관리자 API 를 호출하면 403 이 떨어진다")
    void user_cannot_call_admin_api() throws Exception {

        MockHttpSession userSession = loginAs("employee@test.com", "pass1234!");

        // TODO 05: 권한 실패 검증. 어떤 status?
        mockMvc.perform(get("/api/admin/leaves")
                .session(userSession))
            .andExpect(status().is____());
    }

    @Test
    @DisplayName("이미 승인된 휴가를 다시 승인하면 INVALID_STATUS 가 나온다")
    void cannot_approve_already_approved_leave() throws Exception {
        // TODO 06: 휴가 신청 → 승인 → 다시 승인 시도 흐름을 작성해 보세요.
        //          어떤 ErrorCode 가 응답 body 의 code 필드에 담겨야 할까요?
    }

    // ===== helper =====
    private MockHttpSession loginAs(String email, String password) throws Exception {
        Map<String, String> body = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andReturn();
        // TODO 07: 로그인 요청에서 생성된 세션을 꺼내 이후 요청에서 재사용하세요.
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return (MockHttpSession) result.getRequest().getSession(____);
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-21
//
// Q1. 테스트 메서드의 @Transactional 사용에서 주의할 점은? [chapter-v1-21-13]
// ① 테스트 트랜잭션은 모든 HTTP 서버 스레드에 자동 공유된다.
// ② 자동 롤백 테스트 하나가 실제 커밋 후 이벤트도 항상 검증한다.
// ③ 자동 롤백으로 정리를 돕지만 커밋 시점 동작·다른 스레드 요청까지 같은 범위라고 가정하지 않는다.
// ④ @Transactional 테스트는 DB를 전혀 사용하지 않는다.
// 정답: ③
// 해설: 테스트 관리 트랜잭션과 실제 요청 트랜잭션의 범위를 구분한다. 커밋 후 동작은 커밋을 포함한 별도 검증이 필요할 수 있다.
//
// Q2. SpringBootTest와 WebMvcTest의 범위 차이로 알맞은 것은? [chapter-v1-21-14]
// ① WebMvcTest는 언제나 모든 JPA Repository와 DB를 자동으로 실행한다.
// ② SpringBootTest는 Controller만 항상 로드한다.
// ③ 두 어노테이션은 테스트 이름의 글자 크기만 바꾼다.
// ④ SpringBootTest는 전체 애플리케이션 구성 중심이고 WebMvcTest는 MVC 슬라이스 중심으로 협력자를 대체할 수 있다.
// 정답: ④
// 해설: 검증 대상에 맞는 테스트 범위를 선택하고 Service·DB·보안 같은 필요한 협력자 구성을 명시한다.
//
// Q3. 시간 의존적인 통합 테스트를 안정화하는 방법은? [chapter-v1-21-15]
// ① Clock 같은 시간 공급자를 주입해 고정 시각으로 실행하고 경계 날짜를 검증한다.
// ② LocalDateTime.now의 결과를 실행 시점과 무관하게 같은 값으로 가정한다.
// ③ 시간 비교 테스트를 모두 삭제한다.
// ④ 테스트마다 임의로 결과 시각을 덮어써 통과시킨다.
// 정답: ①
// 해설: 통제 가능한 시간 입력으로 반복 결과를 안정화한다. 실제 시간 계산 경로에서 주입한 시계를 사용한다.
//
// Q4. JWT 기반 인증 요청에서 세션 대신 전달하는 일반적인 정보는? [chapter-v1-21-16]
// ① 사용자의 평문 비밀번호를 모든 URL에 붙인다.
// ② 검증할 액세스 토큰을 Authorization: Bearer 헤더 등에 전달한다.
// ③ 클라이언트가 직접 ADMIN이라고 적은 역할 문자열만 전달한다.
// ④ MockHttpSession 이름만 JWT로 바꾸면 토큰이 된다.
// 정답: ②
// 해설: 서버는 서명·만료·정책을 검증해 인증 주체를 얻는다. 토큰이 없는 요청과 잘못된 토큰의 실패도 검증한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - @SpringBootTest(전체 컨텍스트, 진짜 통합) vs @WebMvcTest(웹 레이어만, 빠름, Service 는 @MockBean).
//   "이 테스트가 무엇을 보장하나" 에 따라 범위를 고른다.
// - @Transactional 테스트는 각 메서드 후 자동 롤백 → 테스트 격리. 단점: 실제 커밋 시점 동작(AFTER_COMMIT 이벤트, 트리거)은 검증 못 함.
// - 권한 실패 403(인가 실패) 와 인증 누락 401 을 구분해서 단정한다.
// - 1차 세션 구현은 MockHttpSession 을 요청마다 재사용한다. JWT 확장 단계에서만 Authorization 헤더를 사용한다.
// - 비결정성: LocalDateTime.now() 는 Clock 주입으로 고정. 응답 JSON 에서 leaveId 를 파싱해 다음 요청에 체이닝.
