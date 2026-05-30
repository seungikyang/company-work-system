// 실제 구현 위치 예: src/main/java/com/example/companywork/service/NoticeService.java
// 목표: 공지 CRUD + 중요공지 정렬을 거의 백지에서. TRD 3.7.5, 3.8.3.
// 막히면 starter/12-notice-service, answers.md 12장 참고.

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AccessGuard accessGuard;

    @Transactional
    public NoticeResponse create(Long currentUserId, UserRole currentRole, NoticeCreateRequest request) {
        // TODO 01: ADMIN 만 등록.
        accessGuard.____(currentRole);

        Notice notice = Notice.create(
            currentUserId, request.getTitle().trim(),
            request.getContent(), request.isImportant()
        );
        return NoticeResponse.from(noticeRepository.save(notice));
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponse> list(Pageable pageable) {
        // TODO 02: 중요공지 우선 + 최신순 정렬.
        Pageable effective = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(),
            Sort.by(Sort.Order.____("important"), Sort.Order.____("createdAt"))
        );
        return noticeRepository.findAll(effective).map(NoticeResponse::from);
    }

    @Transactional
    public NoticeResponse view(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));
        // TODO 03: 조회수 증가(도메인 메서드).
        notice.____();
        return NoticeResponse.from(notice);
    }

    @Transactional
    public NoticeResponse update(UserRole currentRole, Long noticeId, NoticeUpdateRequest request) {
        accessGuard.requireAdmin(currentRole);
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
        notice.update(request.getTitle(), request.getContent(), request.isImportant());
        return NoticeResponse.from(notice);
    }

    @Transactional
    public void delete(UserRole currentRole, Long noticeId) {
        accessGuard.requireAdmin(currentRole);
        // TODO 04: 없는 공지 삭제 시 응답?
        if (!noticeRepository.existsById(noticeId)) {
            throw new BusinessException(ErrorCode.____);
        }
        noticeRepository.deleteById(noticeId);
    }
}

// 학습 질문 (직접 답):
// Q1. ADMIN 검사를 Controller(@PreAuthorize)에만 두는 약점은?
//     A:
// Q2. important 공지를 상단에 두려면 어떤 컬럼에 인덱스?
//     A:
// Q3. 조회수 증가를 별도 트랜잭션으로 분리하려면 어떤 옵션?
//     A:

// 자가 채점:
// □ requireAdmin  □ Sort desc(important)/desc(createdAt)  □ NOTICE_NOT_FOUND  □ increaseViewCount()
