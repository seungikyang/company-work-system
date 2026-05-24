// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Employee.java
// 목표: 직원 엔티티와 EmployeeStatus enum 을 채우세요.
//       TRD 3.3.2, 3.5.3, 3.6.2 참고.

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
        return e;
    }

    public void changeDepartment(Department department) {
        this.department = department;
    }

    public void resign() {
        // TODO 07: 퇴사 처리. 데이터를 삭제하지 않고 상태만 바꾸는 이유는?
        this.status = EmployeeStatus.____;
    }

    public boolean isActive() {
        // TODO 08: 휴가/결재 신청 가능 여부를 판단하는 도메인 규칙.
        return this.status == EmployeeStatus.____;
    }
}
