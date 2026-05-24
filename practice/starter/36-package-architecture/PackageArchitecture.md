# 패키지 구조와 계층 책임

PRD/TRD 3.2.2 의 패키지 구조를 직접 채워 봅니다. 신입 면접에서 “계층 분리 왜 했나요?” 질문에 답할 수 있도록 책임을 한 줄씩 적어 두세요.

---

## 1. 도식 채우기 (top-down)

```text
com.example.companywork
├── ____            # 진입점 / @SpringBootApplication / main()
├── ____            # 전역 설정 (JPA Auditing, Security, WebMvc, PasswordEncoder)
├── ____            # @RestController / @Controller — HTTP 진입점
├── ____            # @Service — 비즈니스 로직 / 트랜잭션 경계
├── ____            # JpaRepository — 영속성
├── ____            # @Entity — 도메인 모델 / 비즈니스 규칙 캡슐화
├── ____            # 요청/응답 DTO, record/class
├── ____            # ErrorCode, BusinessException, GlobalExceptionHandler
└── ____            # @CurrentUser, AuthCheckInterceptor, SecurityConfig
```

---

## 2. 계층별 책임 표

| 계층 | 의존 가능 | 의존 금지 | 주된 어노테이션 |
|---|---|---|---|
| Controller | Service, DTO | Repository, Entity 직접 반환 | `@RestController`, `@RequestMapping` |
| Service | Repository, Domain | ____ | `@Service`, `@Transactional` |
| Repository | Domain | ____ | `extends JpaRepository` |
| Domain (Entity) | (가능한 한) 없음 | Service, Repository | `@Entity`, `@Embeddable` |
| DTO | Domain (정적 팩토리만) | Repository | `record`, `class` |
| Exception | ErrorCode, ResponseEntity | 비즈니스 로직 직접 호출 | `@RestControllerAdvice` |
| Config / Security | 전 영역 | 비즈니스 로직 | `@Configuration`, `@Bean` |

---

## 3. 두 가지 패키지 전략

### A. 계층 우선 (Layer-first) — 현재 PRD 3.2.2 방식

```text
controller/
service/
repository/
domain/
dto/
```

장점:
- 단순. 신입이 책임을 빨리 익힌다.

단점:
- 도메인이 커질수록 한 폴더가 비대해진다. (controller 아래에 30개 컨트롤러)

### B. 도메인 우선 (Feature-first)

```text
employee/
  ├── EmployeeController.java
  ├── EmployeeService.java
  ├── EmployeeRepository.java
  ├── Employee.java
  └── dto/
leave/
  ├── LeaveController.java
  ├── LeaveService.java
  └── ...
```

장점:
- 한 기능을 한 폴더에서 끝낸다.

단점:
- 신입 단계에서는 “계층이 무엇인지” 학습이 어려울 수 있다.

> 학습 단계에서는 ____ 로 시작 → 도메인이 6개 이상 되면 ____ 로 자연스럽게 전환.

---

## 4. 의존 방향 그래프 (직접 그려보세요)

```text
Controller  ──▶  Service  ──▶  Repository  ──▶  Domain
                  │                              ▲
                  ▼                              │
                  DTO ◀──────── (변환만 의존)─────┘
```

위 그래프에서 **반드시 지켜야 할 화살표 방향**을 한 줄로 적어 보세요.

> A:

---

## 5. 안티 패턴 모음

- [ ] Controller 에서 Repository 를 직접 호출
- [ ] Service 에서 HttpServletRequest / HttpSession 을 직접 다룸
- [ ] Entity 에 `@JsonIgnore` 가 5개 이상 붙어 있음
- [ ] DTO 가 Entity 의 setter 를 호출
- [ ] Exception 패키지가 어떤 도메인 클래스를 import

위 5개 중 가장 빠지기 쉬운 것은 무엇이고, 왜 그런지 한 줄로 적어 보세요.

> A:

---

## 6. 학습 질문

- Q1. Service 에서 트랜잭션을 시작하는 것이 자연스러운 이유는?
- Q2. Controller 가 Repository 를 직접 호출하면 어떤 단점이 있을까?
- Q3. Entity 가 Service 를 의존하면 어떤 문제가 생길까? (순환 의존)
- Q4. DTO 를 별도 패키지가 아니라 각 도메인 폴더의 `dto/` 서브패키지로 두면 어떤 장단점이 있을까?
