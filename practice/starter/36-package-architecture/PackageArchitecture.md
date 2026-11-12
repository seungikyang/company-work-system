# 패키지 구조와 계층 책임

이 챕터의 개념과 설계 판단을 4지선다로 확인합니다. [선택·채점 화면](../../../problems.html#problem-36)에서 풀고 실제 구현·검증 결과를 [통합 워크북](../../job-preparation-workbook.md)에 남깁니다.

## 챕터 계약

| 구분 | 완료 기준 |
|---|---|
| 학습 목표 | Controller·Service·Repository·Domain의 책임과 의존 방향을 설명한다. |
| 취업 결과물 | 실제 패키지 트리와 계층별 책임·예외를 기록한 구조 문서 |
| 완료 검증 | 실제 코드에서 Controller의 Repository 직접 호출과 역방향 의존이 없는지 검색한다. |

<!-- 객관식 시작 -->
## 4지선다 문제

### Q36-01. 계층 우선과 도메인 우선 패키지 전략을 선택하는 기준은?

- ① 도메인 우선이면 모든 Service·Repository 분리가 금지된다.
- ② 현재 규모와 변경 단위에 맞춰 책임 탐색·응집도·확장 비용을 비교한다.
- ③ 계층 우선이면 모든 테스트가 불필요해진다.
- ④ 폴더명만 바꾸면 코드 의존 문제가 모두 해결된다.

**정답:** ②

**해설:** 어느 전략이든 책임과 의존 방향을 실제 코드에서 유지해야 한다.

### Q36-02. 이 실습의 기본 호출 의존 방향으로 알맞은 것은?

- ① Controller → Service → Repository, 업무 상태와 규칙은 Domain에 둔다.
- ② Entity → Controller → HTML → Repository
- ③ Repository가 Controller를 호출하여 HTTP 응답을 만든다.
- ④ DTO가 Service와 세션을 직접 생성한다.

**정답:** ①

**해설:** HTTP·업무 조율·데이터 접근·도메인 규칙의 책임을 구분한다.

### Q36-03. Service와 Repository의 의존 정책으로 알맞은 것은?

- ① Repository가 HttpSession을 반드시 알아야 한다.
- ② Entity가 화면 Controller를 항상 주입받는다.
- ③ Service가 브라우저 DOM을 직접 조작한다.
- ④ Service는 Controller에 의존하지 않고 Repository는 HTTP 계층에 의존하지 않는다.

**정답:** ④

**해설:** 하위 업무·저장 계층을 특정 HTTP 진입점에 묶지 않아 재사용과 테스트를 쉽게 한다.

### Q36-04. 계층 책임을 가장 잘 지킨 구성은?

- ① Controller에 SQL·모든 업무 규칙·세션·응답 Entity를 한꺼번에 둔다.
- ② Repository가 HTML을 반환하고 DTO가 데이터를 삭제한다.
- ③ Controller는 바인딩, Service는 업무 조율, Repository는 접근, Entity는 상태 규칙, DTO는 통신 데이터를 맡는다.
- ④ Entity가 모든 Controller를 호출하여 흐름을 조율한다.

**정답:** ③

**해설:** 이름만 나누는 대신 실제 책임과 의존을 구분해야 구조의 효과가 있다.

## 7. 심화 노트 (면접 답변 포인트)

- **반드시 지켜야 할 화살표 방향**: 의존은 항상 상위→하위(Controller→Service→Repository→Domain). Domain 은 누구도 의존하지 않는 가장 안정적인 핵이어야 한다(자신은 Service/Repository 를 import 하지 않음). 역방향 의존이 생기면 순환 의존·테스트 불가로 이어진다.
- **가장 빠지기 쉬운 안티패턴**: Controller 가 Repository 를 직접 호출하는 것. "Service 가 한 줄 wrap 만 하니 생략하자" 는 핑계로 시작되지만, 트랜잭션 경계와 비즈니스 검증을 둘 곳이 사라져 로직이 Controller 로 샌다.
- **Service 가 HttpSession/HttpServletRequest 를 직접 다루지 않기**: 웹 기술이 Service 로 새면 단위 테스트가 어려워진다. 인증 사용자는 Controller 에서 꺼내 `Long currentUserId` 같은 순수 값으로 Service 에 넘긴다(34장 ArgumentResolver).
- **Layer-first → Feature-first 전환선**: 도메인이 6개를 넘어 한 폴더(controller/)에 클래스가 30개씩 쌓이면 도메인 우선으로 바꾼다. 학습 단계는 계층 우선이 책임을 익히기 쉽다.
- **Service 에서 트랜잭션을 여는 이유**: "하나의 비즈니스 작업 = 하나의 트랜잭션" 이라 비즈니스 경계인 Service 가 트랜잭션 경계와 일치한다. Controller(너무 넓음)나 Repository(너무 좁음)는 경계가 어긋난다.
