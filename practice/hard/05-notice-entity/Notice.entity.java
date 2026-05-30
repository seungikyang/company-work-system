// 실제 구현 위치 예: src/main/java/com/example/companywork/domain/Notice.java
// 목표: 공지 엔티티를 거의 백지에서 작성. TRD 3.3.5, 3.5.5.
// 막히면 starter/05-notice-entity, answers.md 5장 참고.

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

    // TODO 01: 본문은 길어질 수 있다. VARCHAR 가 아닌 무엇으로?
    @____
    @Column(nullable = false)
    private ____ content;

    // TODO 02: 작성자 = User 의 ID (관리자만 작성).
    @Column(name = "writer_id", nullable = false)
    private ____ writerId;

    @Column(nullable = false)
    private boolean important;

    // TODO 03: 조회수 — 기본 0.
    @Column(name = "view_count", nullable = false)
    private ____ viewCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // TODO 04: 정적 팩토리 — viewCount 초기값.
    public static Notice create(Long writerId, String title, String content, boolean important) {
        ____ n = new ____();
        n.writerId = writerId;
        n.title = title;
        n.content = content;
        n.important = important;
        n.viewCount = ____;
        return n;
    }

    // TODO 05: null/blank 가드.
    public void update(String title, String content, boolean important) {
        if (title != null && !title.____()) this.title = title.____();
        if (content != null && !content.isBlank()) this.content = content;
        this.important = important;
    }

    // TODO 06: 단순 ++ 의 동시성 문제 — 학습 메모를 직접 적고, 운영 대안(원자적 UPDATE)을 생각.
    public void increaseViewCount() {
        this.viewCount = this.viewCount + 1;
    }
}

// 학습 질문 (직접 답):
// Q1. content 를 @Lob/TEXT 로 두는 기준은? VARCHAR(255) 면 언제 터지나?
//     A:
// Q2. GET 상세에서 조회수를 올리면 무엇이 문제고 대안은?
//     A:
// Q3. this.viewCount+1 이 동시 요청에서 카운트를 누락하는 과정을 단계로 설명.
//     A:

// 자가 채점:
// □ content @Lob  □ writerId Long  □ viewCount long, 초기 0  □ update blank 가드
// □ 중요공지 정렬: important DESC, createdAt DESC  □ 원자적 UPDATE 대안 인지
