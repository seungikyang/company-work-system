// 실제 구현 위치 예: src/test/java/com/example/companywork/integration/LeaveFlowTest.java
// 목표: 휴가 신청 → 관리자 승인 → 결과 확인 MockMvc 통합 테스트를 거의 백지에서. TRD 3.12.
// 막히면 starter/21-test-flow, answers.md 21장 참고.

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LeaveFlowTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("직원이 휴가를 신청하면 관리자가 승인까지 갈 수 있다")
    void leaveRequest_then_adminApprove_flow() throws Exception {

        MockHttpSession employeeSession = loginAs("employee@test.com", "pass1234!");
        MockHttpSession adminSession    = loginAs("admin@test.com", "adminpw!");

        Map<String, Object> body = Map.of(
            "leaveType", "ANNUAL", "startDate", "2026-06-01",
            "endDate", "2026-06-03", "reason", "개인 사유"
        );

        // TODO 01: 로그인 세션 재사용.
        MvcResult requestResult = mockMvc.perform(post("/api/leaves")
                .____(employeeSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            // TODO 02: 새 자원 생성 status.
            .andExpect(status().is____())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andReturn();

        // TODO 03: 응답에서 다음 요청에 쓸 식별자 추출.
        Long leaveId = objectMapper.readTree(requestResult.getResponse().getContentAsString())
            .get("____").asLong();

        mockMvc.perform(patch("/api/admin/leaves/{leaveId}/approve", leaveId)
                .session(adminSession))
            .andExpect(status().isOk())
            // TODO 04: 승인 후 상태.
            .andExpect(jsonPath("$.status").value("____"));

        mockMvc.perform(get("/api/leaves/{leaveId}", leaveId)
                .session(employeeSession))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("일반 사용자가 관리자 API 를 호출하면 403 이 떨어진다")
    void user_cannot_call_admin_api() throws Exception {
        MockHttpSession userSession = loginAs("employee@test.com", "pass1234!");
        // TODO 05: 권한 실패 status.
        mockMvc.perform(get("/api/admin/leaves")
                .session(userSession))
            .andExpect(status().is____());
    }

    @Test
    @DisplayName("이미 승인된 휴가를 다시 승인하면 INVALID_STATUS 가 나온다")
    void cannot_approve_already_approved_leave() throws Exception {
        // TODO 06: 신청 → 승인 → 재승인 시도. 응답 body 의 code 필드에 담길 ErrorCode 는?
    }

    private MockHttpSession loginAs(String email, String password) throws Exception {
        Map<String, String> body = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andReturn();
        // TODO 07: 생성된 세션 추출.
        return (MockHttpSession) result.getRequest().getSession(____);
    }
}

// 학습 질문 (직접 답):
// Q1. @Transactional 을 테스트에 달면 이점/단점은?
//     A:
// Q2. @SpringBootTest 와 @WebMvcTest 의 차이는?
//     A:
// Q3. 시간(now()) 같은 비결정성을 테스트에서 다루는 패턴은?
//     A:
// Q4. JWT 단계에서는 세션 대신 무엇을 전달하나?
//     A:

// 자가 채점:
// □ .session(employeeSession)  □ isCreated()  □ "leaveId"  □ "APPROVED"
// □ isForbidden()  □ getSession(false)  □ 재승인 code=INVALID_STATUS
