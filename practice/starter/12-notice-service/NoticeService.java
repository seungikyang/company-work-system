// 중학생 눈높이: 공지 관리
// 공지를 만들고, 찾아보고, 고치고, 지우는 작업을 맡습니다.
// CRUD는 만들기·읽기·수정·삭제의 네 작업입니다. 정렬은 어떤 글을 먼저 보여 줄지 정하는 일입니다.
// 작업 종류 읽기 → 관리자 권한 확인 → 공지 처리 → 응답 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/NoticeService.java
// 목표: 공지사항 CRUD + 중요 공지 우선 정렬. TRD 3.7.5, 3.8.3 참고.

// 초보자 안내 (12장): CRUD와 정렬
// 개념: CRUD는 생성·조회·수정·삭제의 네 작업입니다.
// 사용 이유: 공지 관리의 기본 작업을 모으고 수정 권한과 정렬 규칙을 일관되게 적용합니다.
// 동작 흐름: 생성·수정·삭제는 역할을 검사하고 처리하며 목록에는 중요 공지 우선 정렬을 적용합니다. 상세 보기는 조회수를 바꾸므로 읽기만 하는 작업과 구분합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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

        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return NoticeResponse.from(noticeRepository.save(notice));
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponse> list(Pageable pageable) {
        // TODO 02: 중요 공지를 항상 상단에 노출하기 위한 정렬을 채우세요.
        //          힌트: Sort.by(Sort.Order.desc("important"), Sort.Order.desc("createdAt"))
        // 사용자가 보낸 페이지 번호·크기는 유지하고 정렬은 업무 규칙으로 다시 만듭니다.
        // 중요 공지 우선, 그 안에서 작성 시각 순서라는 두 기준을 Sort.Order로 조합합니다.
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
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (!noticeRepository.existsById(noticeId)) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new BusinessException(ErrorCode.____);
        }
        noticeRepository.deleteById(noticeId);
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-12
//
// Q1. 공지 작성의 ADMIN 검사를 Controller에만 두었을 때의 약점은? [chapter-v1-12-04]
// ① 다른 호출 경로가 Service를 사용하면 역할 검사가 누락될 수 있다.
// ② Controller 검사가 Service의 모든 직접 호출에도 자동 적용된다.
// ③ @PreAuthorize를 쓰면 자료 소유권도 자동 검증된다.
// ④ 로그인 여부만 확인하면 공지 관리 권한은 모두 같아진다.
// 정답: ①
// 해설: HTTP 경로의 인가와 업무 진입점의 역할 검사를 함께 설계한다. 계층별 검사는 서로 다른 우회를 방어한다.
//
// Q2. 중요 공지 우선·최신순 조회의 인덱스 설계로 알맞은 것은? [chapter-v1-12-05]
// ① important 단일 인덱스가 어떤 데이터 분포에서도 최적이다.
// ② 인덱스는 공지의 본문 색상에 맞춰 선택한다.
// ③ 인덱스만 추가하면 ORDER BY 없이도 정렬 순서가 보장된다.
// ④ important와 createdAt 등 실제 정렬·필터 조합의 인덱스를 실행 계획으로 검토한다.
// 정답: ④
// 해설: 선택도가 낮은 boolean 단일 인덱스의 효과를 단정하지 않는다. 쿼리·DB·분포에 맞는 복합 인덱스를 측정한다.
//
// Q3. 조회수 변경을 호출자와 독립된 트랜잭션으로 실행할 때 검토할 옵션은? [chapter-v1-12-06]
// ① readOnly=true만 붙이면 항상 새 쓰기 트랜잭션이 생긴다.
// ② 같은 객체 안의 직접 호출에도 프록시 규칙이 무조건 적용된다.
// ③ 프록시를 거치는 별도 메서드의 @Transactional(propagation = REQUIRES_NEW)를 사용한다.
// ④ 트랜잭션이 없는 메서드에 @GetMapping만 붙인다.
// 정답: ③
// 해설: REQUIRES_NEW는 호출자의 트랜잭션과 독립되므로 롤백·연결 자원·자가 호출 문제도 함께 검토한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - 권한 다층: Controller @PreAuthorize(1차, HTTP 경계) + Service accessGuard.requireAdmin(2차, 내부호출 우회 방지).
//   Controller 에만 두면 Service 재사용 경로가 무방비.
// - 중요공지 정렬은 (important DESC, created_at DESC) 복합 인덱스로 비용↓.
// - 조회수 분리 3안: REQUIRES_NEW 별도 트랜잭션 / 원자적 UPDATE 쿼리 / PATCH 분리(GET 멱등성 유지).
