// 실제 구현 위치 예: src/test/java/com/example/companywork/integration/LeaveFlowTest.java
// 목표: 휴가 신청 → 관리자 승인 → 결과 확인 흐름을 MockMvc 통합 테스트로 채우세요.
//       PRD/TRD 3.12 참고. bank-system 의 BankSystemApiTest 와 비슷한 형태입니다.

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
        String employeeToken = loginAs("employee@test.com", "pass1234!");
        String adminToken    = loginAs("admin@test.com", "adminpw!");

        // 2. 휴가 신청
        Map<String, Object> body = Map.of(
            "leaveType", "ANNUAL",
            "startDate", "2026-06-01",
            "endDate",   "2026-06-03",
            "reason",    "개인 사유"
        );

        // TODO 01: 휴가 신청 API 호출. 인증 헤더는 어떤 형식인가요?
        MvcResult requestResult = mockMvc.perform(post("/api/leaves")
                .header("Authorization", "____ " + employeeToken)
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
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            // TODO 04: 응답의 status 가 APPROVED 로 바뀌어야 한다.
            .andExpect(jsonPath("$.status").value("____"));

        // 4. 직원이 본인 휴가를 다시 조회하면 APPROVED 상태여야 한다.
        mockMvc.perform(get("/api/leaves/{leaveId}", leaveId)
                .header("Authorization", "Bearer " + employeeToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("일반 사용자가 관리자 API 를 호출하면 403 이 떨어진다")
    void user_cannot_call_admin_api() throws Exception {

        String userToken = loginAs("employee@test.com", "pass1234!");

        // TODO 05: 권한 실패 검증. 어떤 status?
        mockMvc.perform(get("/api/admin/leaves")
                .header("Authorization", "Bearer " + userToken))
            .andExpect(status().is____());
    }

    @Test
    @DisplayName("이미 승인된 휴가를 다시 승인하면 INVALID_STATUS 가 나온다")
    void cannot_approve_already_approved_leave() throws Exception {
        // TODO 06: 휴가 신청 → 승인 → 다시 승인 시도 흐름을 작성해 보세요.
        //          어떤 ErrorCode 가 응답 body 의 code 필드에 담겨야 할까요?
    }

    // ===== helper =====
    private String loginAs(String email, String password) throws Exception {
        Map<String, String> body = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
            .get("token").asText();
    }
}

// 학습 질문:
// Q1. @Transactional 을 테스트 메서드에 달면 어떤 이점/단점이 있나?
//     A:
// Q2. @SpringBootTest 와 @WebMvcTest 의 차이는?
//     A:
// Q3. 통합 테스트에서 시간(LocalDateTime.now()) 같은 비결정성을 다루는 패턴은?
//     A:
