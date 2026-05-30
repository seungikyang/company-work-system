// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Employee.java
// 목표: 직원 엔티티 + EmployeeStatus enum 을 거의 백지에서 작성. TRD 3.3.2, 3.5.3, 3.6.2.
// 막히면 starter/03-employee-entity, answers.md 3장 참고.

// TODO 01: 재직 / 비활성 / 퇴사.
public enum EmployeeStatus {
    ____, ____, ____
}

// TODO 02: 엔티티 + 테이블 + 사번 unique.
@____
@____(
    name = "____",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_employees_employee_number", columnNames = "____")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 03: User 와 1:1. fetch 전략은? 외래키 user_id, unique.
    @____(fetch = FetchType.____)
    @____(name = "user_id", nullable = false, unique = ____)
    private ____ user;

    // TODO 04: Department 와 N:1. (여러 직원이 한 부서)
    @____(fetch = FetchType.____)
    @JoinColumn(name = "department_id", nullable = false)
    private ____ department;

    @Column(name = "employee_number", nullable = false, length = 30)
    private String employeeNumber;

    @Column(length = 50)
    private String position;

    @Column(length = 30)
    private String phone;

    @Column(name = "hire_date")
    private ____ hireDate;

    // TODO 05: 상태 enum 저장 방식.
    @____(EnumType.____)
    @Column(nullable = false, length = 20)
    private ____ status;

    // TODO 06: 정적 팩토리 — 신규 직원 기본 상태는?
    public static Employee create(User user, Department department, String employeeNumber,
                                  String position, String phone, LocalDate hireDate) {
        ____ e = new ____();
        e.user = user;
        e.department = department;
        e.employeeNumber = employeeNumber;
        e.position = position;
        e.phone = phone;
        e.hireDate = hireDate;
        e.status = EmployeeStatus.____;
        return e;
    }

    public void changeDepartment(Department department) {
        this.____ = department;
    }

    // TODO 07: 퇴사 처리 — 삭제하지 않고 상태만 바꾼다.
    public void resign() {
        this.status = EmployeeStatus.____;
    }

    // TODO 08: 휴가/결재 신청 가능 여부.
    public boolean isActive() {
        return this.status == EmployeeStatus.____;
    }
}

// 학습 질문 (직접 답):
// Q1. @ManyToOne 기본값 EAGER 를 LAZY 로 바꾸는 이유는? (목록 100건 + N+1)
//     A:
// Q2. 퇴사를 DELETE 가 아니라 RESIGNED 로 두는 이유는? (과거 휴가/결재 참조)
//     A:
// Q3. Service 가 setter 대신 resign()/changeDepartment() 도메인 메서드를 쓰는 이유는?
//     A:

// 자가 채점:
// □ EmployeeStatus 3값  □ user_number unique  □ @OneToOne(LAZY)+@JoinColumn(user_id,unique)
// □ @ManyToOne(LAZY) department  □ @Enumerated(STRING)  □ create→ACTIVE  □ resign→RESIGNED  □ isActive→ACTIVE
