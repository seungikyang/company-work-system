// 중학생 눈높이: 직원 정보
// Employee는 직원의 업무용 카드입니다. 로그인 계정과 부서, 사번 등을 연결합니다.
// 관계는 두 카드가 어떻게 연결되는지를 뜻합니다. 여러 직원이 한 부서에 속할 수 있습니다.
// 연결할 계정·부서 확인 → 직원 카드 만들기 → 재직 상태 변경 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Employee.java
// 목표: 직원 엔티티와 EmployeeStatus enum 을 채우세요.
//       TRD 3.3.2, 3.5.3, 3.6.2 참고.

// 초보자 안내 (03장): JPA 연관관계와 지연 로딩
// 개념: 연관관계는 직원과 계정·부서를 연결하며, LAZY는 관련 객체의 조회를 필요할 때까지 미루는 설정입니다.
// 사용 이유: 한 부서에 여러 직원이 속한다는 구조를 표현하고 불필요한 관련 데이터 조회를 줄입니다.
// 동작 흐름: Employee의 user_id와 department_id가 다른 테이블을 참조합니다. 관련 객체를 읽을 때 추가 SQL이 나갈 수 있으므로 LAZY만으로 N+1 문제가 해결되지는 않습니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// ===== EmployeeStatus enum =====
public enum EmployeeStatus {
    // TODO 01: 재직 / 비활성 / 퇴사 세 상태를 선언하세요.
    ____, ____, ____
}

// ===== Employee entity =====
@Entity
@Table(
    name = "employees",
    uniqueConstraints = {
        // TODO 02: 사번(employee_number)이 중복되지 않도록 unique 제약을 둡니다.
        @UniqueConstraint(name = "uk_employees_employee_number", columnNames = "____")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 03: User 와 1:1 관계를 매핑하세요.
    //          - fetch 전략은? 왜?
    //          - 외래키 컬럼명은 "user_id"
    @____(fetch = FetchType.____)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // TODO 04: Department 와 N:1 관계를 매핑하세요. (여러 직원이 한 부서에 속한다)
    @____(fetch = FetchType.____)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "employee_number", nullable = false, length = 30)
    private String employeeNumber;

    @Column(length = 50)
    private String position;

    @Column(length = 30)
    private String phone;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    // TODO 05: 상태 enum 을 어떻게 저장해야 enum 변경에 강할까요?
    @Enumerated(EnumType.____)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status;

    public static Employee create(User user, Department department, String employeeNumber,
                                  String position, String phone, LocalDate hireDate) {
        Employee e = new Employee();
        e.user = user;
        e.department = department;
        e.employeeNumber = employeeNumber;
        e.position = position;
        e.phone = phone;
        e.hireDate = hireDate;
        // TODO 06: 신규 직원의 기본 상태는?
        e.status = EmployeeStatus.____;
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return e;
    }

    // department는 부서 객체를 가리킵니다. 관리 중인 직원의 참조를 바꾸면 flush 때 FK 변경으로 이어질 수 있습니다.
    // 이 메서드는 새 부서를 만드는 동작이 아닙니다.
    public void changeDepartment(Department department) {
        this.department = department;
    }

    public void resign() {
        // TODO 07: 퇴사 처리. 데이터를 삭제하지 않고 상태만 바꾸는 이유는?
        this.status = EmployeeStatus.____;
    }

    // boolean은 참/거짓 결과입니다. 호출하는 Service는 이 결과로 휴가 신청을 계속할지 결정합니다.
    public boolean isActive() {
        // TODO 08: 휴가/결재 신청 가능 여부를 판단하는 도메인 규칙.
        return this.status == EmployeeStatus.____;
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-03
//
// Q1. 직원 관계를 EAGER 대신 LAZY로 설정할 때 함께 해야 할 일은? [chapter-v1-03-05]
// ① 불필요한 즉시 조회를 줄이고 실제 목록 쿼리 수를 확인해 fetch join·DTO 조회 등으로 N+1을 조정한다.
// ② LAZY 하나면 모든 N+1 문제가 사라진다고 가정한다.
// ③ EAGER는 언제나 한 개의 조인 SQL만 실행한다고 가정한다.
// ④ LAZY 객체는 트랜잭션 없이 언제나 로딩된다고 가정한다.
// 정답: ①
// 해설: fetch 전략만으로 쿼리 수를 보장할 수 없다. 접근 경로와 실제 SQL을 함께 검증한다.
//
// Q2. 퇴사를 RESIGNED 상태로 남기는 주된 업무 이유는? [chapter-v1-03-06]
// ① 퇴사자의 신규 휴가를 무제한 허용하기 위해서다.
// ② 과거 휴가·결재의 참조와 인사 이력을 보존하면서 신규 업무 허용 여부를 제한하기 위해서다.
// ③ 퇴사자의 계정 ID를 즉시 다른 사람에게 넘기기 위해서다.
// ④ 비밀번호를 자동 공개하기 위해서다.
// 정답: ②
// 해설: 이력 보존과 활성 사용자 정책을 함께 적용한다. 상태만 바꾸고 접근 제한을 빠뜨리지 않는다.
//
// Q3. setter 대신 resign·changeDepartment 같은 메서드를 두는 이점은? [chapter-v1-03-07]
// ① 필드를 언제나 검증 없이 바꾸게 한다.
// ② Service의 권한 검사와 트랜잭션을 전부 없앤다.
// ③ 변경의 업무 의도와 검사 규칙을 한곳에 모아 불변식 우회를 줄인다.
// ④ 메서드 이름만 바꾸면 동시성 제어도 자동 적용된다.
// 정답: ③
// 해설: 도메인 변경 메서드는 상태·입력 규칙을 지킨다. 인증·권한과 외부 작업 조정은 별도 책임이다.
//
// Q4. isActive 판단을 Entity 메서드로 모으는 이유는? [chapter-v1-03-08]
// ① Entity가 모든 HTTP 인증을 대신 처리하기 위해서다.
// ② 상태 필드 자체를 제거하기 위해서다.
// ③ isActive를 호출하면 상태가 항상 ACTIVE로 바뀌기 때문이다.
// ④ 활성 상태의 정의를 재사용하고 Service마다 다른 조건이 생기는 것을 줄이기 위해서다.
// 정답: ④
// 해설: 상태를 판단하는 규칙을 데이터 옆에 둔다. 판단 메서드와 상태 변경 메서드는 구분한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
