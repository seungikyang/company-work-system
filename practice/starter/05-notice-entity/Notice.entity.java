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
        return n;
    }

    public void update(String title, String content, boolean important) {
        // TODO 05: 제목/내용이 null/blank 인 경우를 어떻게 처리할지 정해 보세요.
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

// 학습 질문 (면접 대비):
// Q1. content 를 VARCHAR 가 아니라 @Lob / TEXT 로 두는 기준은? VARCHAR(255) 로 두면 언제 터지나요?
//     A:
// Q2. 조회수 증가를 GET 상세 조회 안에서 처리하면 검색봇/링크 미리보기로 viewCount 가 부풀 수 있습니다.
//     대안(별도 PATCH /view, 중복 집계 방지)을 한 가지 제시해 보세요.
//     A:
// Q3. read 100개 동시 요청에서 this.viewCount + 1 방식이 카운트를 누락하는 과정을 단계로 설명하고,
//     원자적 UPDATE 쿼리가 왜 안전한지 말해 보세요. (31장 @Modifying 과 연결)
//     A:
// Q4. 중요 공지를 상단에 고정하려면 정렬을 어떻게 줘야 하나요?
//     important 가 boolean 인데 true 를 먼저 두려면 어떤 방향 정렬이 필요한가요?
//     A:
