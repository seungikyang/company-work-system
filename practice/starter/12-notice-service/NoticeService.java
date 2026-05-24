// 실제 구현 위치 예: src/main/java/com/example/companywork/service/NoticeService.java
// 목표: 공지사항 CRUD + 중요 공지 우선 정렬. TRD 3.7.5, 3.8.3 참고.

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AccessGuard accessGuard; // requireAdmin, requireLogin 같은 권한 가드

    @Transactional
    public NoticeResponse create(Long currentUserId, UserRole currentRole, NoticeCreateRequest request) {
        // TODO 01: 공지는 ADMIN 만 등록할 수 있습니다.
        accessGuard.____(currentRole);

        Notice notice = Notice.create(
            currentUserId,
            request.getTitle().trim(),
            request.getContent(),
            request.isImportant()
        );

        return NoticeResponse.from(noticeRepository.save(notice));
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponse> list(Pageable pageable) {
        // TODO 02: 중요 공지를 항상 상단에 노출하기 위한 정렬을 채우세요.
        //          힌트: Sort.by(Sort.Order.desc("important"), Sort.Order.desc("createdAt"))
        Pageable effective = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Order.____("important"), Sort.Order.____("createdAt"))
        );

        return noticeRepository.findAll(effective).map(NoticeResponse::from);
    }

    @Transactional
    public NoticeResponse view(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));

        // TODO 03: 상세 조회 시 조회수를 올릴지, 별도 PATCH 로 분리할지 결정해 보세요.
        //          학습용으로는 일단 같이 둡니다.
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

        // TODO 04: 존재하지 않는 공지를 삭제하면 어떤 응답을 줘야 할까요?
        if (!noticeRepository.existsById(noticeId)) {
            throw new BusinessException(ErrorCode.____);
        }
        noticeRepository.deleteById(noticeId);
    }
}

// 학습 질문:
// Q1. ADMIN 검사를 Controller(@PreAuthorize)에만 두는 것의 약점은?
//     A:
// Q2. important=true 공지를 위로 끌어올리기 위해 인덱스가 필요한 컬럼은?
//     A:
// Q3. 조회수 증가를 별도 트랜잭션으로 분리하려면 어떤 어노테이션 옵션을 쓰면 좋을까요?
//     A:
