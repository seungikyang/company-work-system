// 중학생 눈높이: 공지사항
// Notice는 직원들에게 알릴 글입니다. 제목·본문·작성자·조회수를 담습니다.
// 조회수는 읽은 횟수이고, 중요 표시는 먼저 보여 줄 공지를 구분하는 값입니다.
// 공지 만들기 → 내용 수정하기 → 조회수 바꾸기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Notice.java
// 목표: 공지사항 엔티티를 채우세요. TRD 3.3.5, 3.5.5 참고.

// 초보자 안내 (05장): 본문 저장과 조회수
// 개념: 긴 본문용 컬럼은 공지 내용을 저장하고, important와 viewCount는 표시 순서와 열람 횟수에 쓰입니다.
// 사용 이유: 제목보다 긴 본문을 담고 중요한 공지를 먼저 보여주기 위해 분리합니다.
// 동작 흐름: 객체에는 본문·중요 여부·조회수를 담고 목록 쿼리에서 정렬합니다. 두 요청의 조회수 증가가 겹치면 값이 유실될 수 있어 별도 동시성 처리가 필요합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    // TODO 01: 공지 본문은 길어질 수 있습니다. VARCHAR 가 아닌 어떤 타입으로 저장할까요?
    //          힌트: @Lob 또는 columnDefinition = "TEXT"
    @____
    @Column(nullable = false)
    private String content;

    // TODO 02: 작성자는 User 의 ID 입니다. (관리자만 작성할 수 있다는 비즈니스 규칙)
    @Column(name = "writer_id", nullable = false)
    private Long ____;

    @Column(nullable = false)
    private boolean important;

    // TODO 03: 조회수 컬럼 기본값을 0 으로 시작합니다.
    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Notice create(Long writerId, String title, String content, boolean important) {
        Notice n = new Notice();
        n.writerId = writerId;
        n.title = title;
        n.content = content;
        n.important = important;
        // TODO 04: 새 공지의 viewCount 기본값은?
        n.viewCount = ____;
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return n;
    }

    public void update(String title, String content, boolean important) {
        // TODO 05: 제목/내용이 null/blank 인 경우를 어떻게 처리할지 정해 보세요.
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (title != null && !title.isBlank()) this.title = title.trim();
        if (content != null && !content.isBlank()) this.content = content;
        this.important = important;
    }

    // 이 코드는 현재 객체에서 읽은 수에 1을 더합니다. 동시에 같은 수를 읽으면 증가분을 잃을 수 있습니다.
    // 31장의 DB UPDATE 방식과 비교하며 어느 계층에서 증가시키는지 확인하세요.
    public void increaseViewCount() {
        // 학습 메모:
        // - 단순 ++ 는 동시성 경합에서 카운트가 누락될 수 있습니다.
        // - 운영에서는 JPQL `UPDATE notice SET view_count = view_count + 1 WHERE id = :id` 같은
        //   원자적 UPDATE 를 Repository 에 두는 방식을 고려합니다.
        this.viewCount = this.viewCount + 1;
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-05
//
// Q1. 공지 본문에 긴 문자열 저장 형식을 선택하는 기준은? [chapter-v1-05-04]
// ① 허용 본문 길이와 DB 매핑을 맞추고 255자를 넘을 수 있으면 TEXT·LOB 등을 검토한다.
// ② 모든 공지 본문은 반드시 255자 이하여야 한다.
// ③ @Lob를 붙이면 DB 종류와 무관하게 정확히 같은 컬럼 타입이 된다.
// ④ 본문 길이는 조회수의 숫자로 정한다.
// 정답: ①
// 해설: 업무 입력 제한과 실제 DB 자료형을 함께 확인한다. @Lob의 구체적 매핑은 DB·JPA 구현에 따라 점검한다.
//
// Q2. GET 상세 조회에 따른 조회수 부풀림을 줄이는 설계는? [chapter-v1-05-05]
// ① GET마다 무조건 증가시켜야 사람의 조회수만 남는다.
// ② 링크 미리보기는 서버에 절대로 요청하지 않는다.
// ③ HTTP 메서드 이름만 바꾸면 중복 집계가 자동 제거된다.
// ④ 조회와 조회수 기록을 분리하고 중복·봇 집계 정책을 정한다.
// 정답: ④
// 해설: GET에는 봇·미리보기·재시도가 포함될 수 있다. 별도 이벤트나 변경 요청과 집계 정책을 함께 설계한다.
//
// Q3. 동시 조회에서 viewCount + 1의 증가가 누락되는 과정은? [chapter-v1-05-06]
// ① 두 요청이 같은 값을 읽어도 자바가 자동으로 12를 저장한다.
// ② Long 대신 String을 사용하면 증가 누락이 사라진다.
// ③ 두 요청이 같은 값 10을 읽고 각각 11을 저장하므로 원자적 증가 UPDATE 등으로 경쟁을 제어해야 한다.
// ④ getter를 private로 바꾸면 DB 경쟁이 해결된다.
// 정답: ③
// 해설: 읽기·증가·쓰기의 경쟁을 구분한다. SET view_count = view_count + 1처럼 DB에서 원자적으로 증가시키는 방식을 검토한다.
//
// Q4. 중요 공지를 먼저 두고 최신 공지를 그다음에 정렬하는 방식은? [chapter-v1-05-07]
// ① important ASC만 적용해 false를 먼저 둔다.
// ② important DESC, createdAt DESC와 안정적인 ID 정렬을 적용한다.
// ③ 조회수 오름차순만으로 중요 공지가 항상 먼저 나온다.
// ④ 정렬 없이 DB 반환 순서에 의존한다.
// 정답: ②
// 해설: true를 먼저 두는 방향과 최신순을 조합하고 동률일 때도 페이지 순서를 안정적으로 만든다.
//
// 4지선다 학습 문제 (자동 생성 끝)
