// 중학생 눈높이: 이 파일은 10개 기술 단원의 학습 자료입니다. 문제를 만드는 재료라고 생각하세요.
// ## 줄은 단원 정보이고, 그 아래 줄은 주제별 설명입니다. |로 칸을 나눠 quiz-core.js가 읽습니다.
// String.raw는 자료 속 역슬래시를 적은 대로 유지합니다. 괄호 안의 긴 글은 실행 명령이 아닌 학습 자료입니다.
// 기술 단원: 번호 | 제목 | 사용 범위 | 기술 | 근거 파일
// 학습 항목: 이름 | 파일에서 확인할 표현 | 개념 | 적용 상황 | 구현 | 오류 | 검증
// 실제 화면 기술과 백엔드 실습 기술은 scope로 구분합니다.
globalThis.FUNDAMENTALS_SOURCES = [];
globalThis.FUNDAMENTALS_SOURCES.push(String.raw`
## 00|HTML 문서 구조|workbook|HTML|index.html
HTML 문서 언어|<html lang="ko">|문서의 주된 언어를 기계가 이해하도록 지정하는 속성|한국어 화면을 보조 기술이 알맞게 읽어야 하는 상황|html 요소에 lang="ko"를 지정한다|화면의 주 언어를 보조 기술이 잘못 판단할 수 있다|최상위 html의 lang 값이 ko인지 확인한다
UTF-8 문자 인코딩|charset="utf-8"|HTML의 문자 해석 방식을 UTF-8로 선언하는 설정|한글 학습 자료를 브라우저에서 표시하는 상황|head 안의 meta에 charset="utf-8"을 지정한다|파일 인코딩과 해석이 어긋나 한글이 깨질 수 있다|한글 제목이 원문 그대로 렌더링되는지 확인한다
HTML 문서 제목|<title>|브라우저 탭에 표시할 문서 이름을 정의하는 요소|문제집과 읽기 화면의 탭을 구별하는 상황|head 안에 의미 있는 title을 작성한다|탭 목록에서 어떤 문서인지 구분하기 어렵다|브라우저 탭 제목과 document.title을 대조한다
HTML 제목 계층|<h1 id="hero-title">|문서의 제목과 하위 구획을 구조적으로 표현하는 방법|긴 워크북의 대주제와 하위 섹션을 구분하는 상황|대표 제목과 하위 제목을 h1·h2 등으로 배치한다|화면 크기만으로 제목을 표현해 문서 구조가 불명확해진다|보조 기술의 제목 목록에서 학습 구획을 확인한다
HTML 앵커 링크|href="#curriculum"|같은 문서의 ID 위치로 이동하는 링크|목차에서 학습 목록으로 바로 이동하는 상황|href의 # 뒤에 대상 요소의 id를 연결한다|링크가 존재하지 않는 ID를 가리켜 이동하지 못한다|목차 링크를 눌러 해당 ID의 섹션으로 이동하는지 확인한다

## 01|HTML 입력과 접근성|workbook|HTML·접근성|index.html
label과 입력 연결|<label|입력 요소의 목적을 텍스트로 알려 주는 구조|여러 목표 입력창의 용도를 구분하는 상황|label과 대응 입력 요소를 연결한다|입력창의 이름을 보조 기술이나 사용자가 파악하기 어렵다|입력 요소의 접근 가능한 이름이 안내 문구와 일치하는지 확인한다
select와 option|<option|주어진 후보 중 값을 선택하게 하는 HTML 요소|오늘 학습할 챕터를 목록에서 고르는 상황|select 안에 값과 표시 이름을 가진 option을 둔다|사용자가 지정된 후보를 안정적으로 선택할 수 없다|선택 후보의 표시명과 실제 value를 함께 확인한다
checkbox의 checked|type="checkbox"|체크 항목의 선택 여부를 boolean 상태로 다루는 방식|완료 표시의 체크 상태를 읽는 상황|checkbox의 checked 속성을 읽어 완료 여부로 사용한다|입력 value를 체크 여부로 착각해 완료 상태가 잘못 저장된다|선택·해제 때 checked가 true·false로 바뀌는지 확인한다
일반 버튼의 type|type="button"|폼 제출을 의도하지 않는 버튼의 동작을 지정하는 속성|힌트 토글을 눌러도 폼을 제출하지 않아야 하는 상황|보조 동작 버튼에 type="button"을 지정한다|폼 안의 보조 버튼이 예기치 않게 제출을 일으킬 수 있다|보조 버튼을 눌러 제출이나 페이지 이동이 없는지 확인한다
aria-expanded|aria-expanded|연결된 내용을 펼쳤는지 보조 기술에 알리는 상태|힌트 영역의 열림 상태를 전달하는 상황|실제 펼침 상태와 aria-expanded를 함께 갱신한다|화면 상태와 보조 기술이 읽는 펼침 상태가 달라진다|토글 전후 표시 상태와 속성값이 일치하는지 확인한다

## 02|CSS 배치와 크기|workbook|CSS|index.html
CSS border-box|box-sizing: border-box|설정한 크기에 테두리와 안쪽 여백을 포함하는 박스 계산|정해진 폭의 카드에 padding을 넣는 상황|box-sizing을 border-box로 설정한다|폭에 여백이 더해져 예상보다 카드가 커질 수 있다|padding을 넣어도 지정한 박스 폭이 유지되는지 확인한다
CSS Flexbox|display: flex|한 축을 중심으로 요소를 정렬하는 배치 방식|헤더의 브랜드와 메뉴를 나란히 놓는 상황|부모에 display:flex를 주고 정렬 속성을 정한다|메뉴 요소의 가로 배치와 정렬이 의도와 달라진다|헤더 자식들의 방향과 정렬 위치를 확인한다
CSS Grid|display: grid|행과 열의 격자로 요소를 배치하는 방식|학습 카드 여러 개를 일정한 열로 배치하는 상황|부모에 display:grid와 열 구성을 지정한다|카드의 열 너비와 행 배치가 고르게 맞지 않는다|같은 행 카드들의 열 너비와 다음 행 배치를 확인한다
CSS gap|gap:|Flex나 Grid 항목 사이 간격을 지정하는 속성|카드 사이 여백을 일정하게 두는 상황|레이아웃 컨테이너에 gap을 지정한다|항목마다 개별 여백이 달라 간격이 불규칙해질 수 있다|이웃 카드 사이 거리가 일정한지 측정한다
CSS 사용자 정의 속성|--green:|여러 스타일에서 재사용하는 이름 있는 CSS 값|버튼과 링크의 대표색을 한곳에서 관리하는 상황|공통 값을 --green으로 선언하고 var(--green)으로 참조한다|같은 의미의 색상이 여러 곳에서 서로 달라진다|공통 변수 변경이 이를 참조한 요소들에 반영되는지 확인한다

## 03|CSS 반응형과 상호작용|workbook|CSS·반응형|index.html
CSS 미디어 쿼리|@media (max-width: 680px)|화면 조건에 따라 다른 스타일을 적용하는 규칙|좁은 모바일 화면에서 카드 열 수를 줄이는 상황|max-width 조건의 @media 안에 모바일 배치를 둔다|데스크톱 배치가 좁은 화면에서 넘칠 수 있다|기준 너비 양쪽에서 레이아웃 변화를 확인한다
CSS focus-visible|button:focus-visible|키보드 탐색 때 현재 포커스를 알아보기 쉽게 꾸미는 선택자|Tab으로 버튼을 이동하며 조작하는 상황|focus-visible에 뚜렷한 outline을 지정한다|키보드 사용자가 현재 조작 대상을 놓친다|Tab 이동 시 버튼 테두리 표시가 보이는지 확인한다
동작 줄이기 설정|prefers-reduced-motion: reduce|운영체제의 움직임 축소 선호를 반영하는 조건|사용자가 화면 전환 움직임을 줄이도록 설정한 상황|해당 미디어 조건에서 애니메이션과 부드러운 이동을 줄인다|사용자가 원하지 않는 움직임이 계속 재생된다|동작 줄이기 설정을 켜고 전환 효과를 확인한다
sticky 헤더|position: sticky|스크롤 중 정해진 경계에서 요소를 붙잡아 두는 배치|목차 메뉴를 스크롤 중에도 보이게 하는 상황|헤더에 sticky와 top 기준을 설정한다|스크롤할 때 메뉴가 예상대로 유지되지 않는다|페이지를 내려도 헤더가 지정 위치에 머무는지 확인한다
앵커의 scroll-margin|scroll-margin-top:|요소로 스크롤할 때 위쪽 여유 공간을 두는 속성|고정 헤더가 이동한 섹션 제목을 가리는 상황|대상 섹션에 헤더 높이를 고려한 scroll-margin-top을 둔다|목차 이동 후 제목이 헤더 뒤에 가려진다|앵커 이동 후 제목 전체가 헤더 아래에 보이는지 확인한다

## 04|JavaScript 값과 조건|workbook|JavaScript|index.html
const 바인딩|const storageKey|변수 식별자의 재할당을 막는 JavaScript 선언|저장 키처럼 바뀌지 않을 참조를 선언하는 상황|재할당하지 않는 참조를 const로 선언한다|상수 역할의 이름에 다른 값을 잘못 대입할 수 있다|const 재할당은 실패하며 객체 속성 불변까지 뜻하지 않음을 구분한다
let 재할당|let storageEnabled|같은 변수에 새 값을 대입할 수 있는 선언|저장 실패 뒤 저장 가능 표시를 바꾸는 상황|변경할 상태 변수는 let으로 선언한다|변경해야 할 상태를 const로 두면 재할당이 실패한다|오류 처리 후 storageEnabled 값이 바뀌는지 확인한다
객체의 속성 구조|profile: {|관련된 여러 값을 이름으로 묶는 JavaScript 자료구조|목표 직무와 마감일을 프로필로 묶는 상황|profile 객체의 속성에 각 입력값을 저장한다|관련 값의 소속이 흐려져 다른 상태와 혼동된다|저장 객체에서 profile 아래 원하는 필드가 있는지 확인한다
선택적 연결 연산자|activeLink?.scrollIntoView|중간 값이 null 또는 undefined이면 접근을 멈추는 문법|현재 메뉴에 해당하는 링크를 찾지 못한 상황|선택적인 중간 객체 접근에 ?.를 사용한다|없는 객체의 속성을 읽다가 TypeError가 발생한다|현재 메뉴 링크가 없어도 화면 갱신이 계속되는지 확인한다
Array.isArray 판정|Array.isArray(saved.stages)|값이 실제 배열인지 검사하는 JavaScript 기능|외부 저장값의 stages 자료형을 확인하는 상황|배열이어야 할 값을 Array.isArray로 확인한다|문자열이나 객체를 배열로 믿고 순회하다 오류가 난다|배열·문자열·null 입력의 판정 결과를 비교한다

## 05|JavaScript 함수와 배열|workbook|JavaScript|index.html
화살표 함수|const byId = (id) =>|입력을 받아 작업 결과를 반환하는 간결한 함수 표현|DOM ID 조회 동작을 반복해서 사용하는 상황|id 인자를 받는 byId 함수로 조회를 묶는다|동일 조회 코드가 여러 곳에 복제된다|다른 ID 인자를 넣었을 때 대응 요소가 반환되는지 확인한다
배열 forEach|chapterRows.forEach|배열의 각 원소에 순서대로 작업을 적용하는 메서드|모든 챕터 행에 초기 설정을 적용하는 상황|forEach 콜백에서 각 행에 필요한 처리를 수행한다|첫 행에만 설정이 적용되고 나머지 행은 빠질 수 있다|서로 다른 챕터 행 모두에 초기 설정이 적용되는지 확인한다
배열 find|chapterRows.find(|조건을 만족하는 첫 원소를 찾는 메서드|선택한 번호의 챕터 행 하나를 찾는 상황|find 콜백에서 dataset.chapter를 선택 번호와 비교한다|배열 전체와 단일 선택 행을 혼동할 수 있다|존재하는 번호는 행을 찾고 없는 번호는 undefined인지 확인한다
배열 includes|state.stages.includes|배열 안에 지정 값이 있는지 확인하는 메서드|단계 ID가 완료 목록에 포함되었는지 판정하는 상황|완료 ID 배열에 includes를 적용한다|완료 여부를 잘못 판정해 체크 표시가 어긋난다|완료 목록에 ID를 추가·제거해 판정 변화를 확인한다
객체 펼침 문법|...emptyState.today|다른 객체의 열거 가능한 속성을 새 객체에 펼치는 문법|오늘 기록만 기본값을 가진 새 객체로 초기화하는 상황|기본 속성을 새 객체에 펼쳐 오늘 기록에 대입한다|기본 객체를 직접 수정하면 이후 초기화 기준도 달라진다|초기화 후 오늘 기록을 바꿔도 기본 객체는 유지되는지 확인한다

## 06|DOM 요소 생성과 표시|workbook|DOM·JavaScript|problems.html
document.createElement|document.createElement("article")|새 HTML 요소 객체를 메모리에 만드는 DOM 기능|불러온 문제마다 카드 요소를 만드는 상황|createElement로 article을 만들고 자식 요소를 구성한다|생성할 요소 없이 텍스트만 이어 붙여 구조가 불명확해진다|문항마다 실제 article 요소가 만들어지는지 확인한다
textContent 출력|title.textContent|값을 HTML이 아닌 텍스트로 표시하는 DOM 속성|문제 제목에 태그처럼 보이는 문자열이 포함된 상황|문자열을 표시할 요소의 textContent에 대입한다|HTML로 해석해 입력한 태그가 실행될 수 있다|태그 모양의 입력이 요소로 변하지 않고 문자로 보이는지 확인한다
querySelectorAll|querySelectorAll("[data-chapter]")|CSS 선택자에 맞는 모든 요소를 모으는 DOM 기능|목차 문서에서 챕터 행 전체를 찾는 상황|data-chapter 선택자로 모든 행을 조회한다|단일 요소 조회를 사용해 첫 챕터만 처리할 수 있다|조회된 행 수가 실제 챕터 수와 같은지 확인한다
DOMParser|new DOMParser()|HTML 문자열을 탐색할 수 있는 문서 객체로 파싱하는 기능|fetch로 받은 index.html에서 챕터 정보를 읽는 상황|parseFromString에 HTML 문자열과 text/html을 전달한다|원문 문자열을 DOM처럼 조회하려다가 실패한다|파싱한 문서에서 챕터 선택자로 요소를 찾을 수 있는지 확인한다
dataset 읽기|row.dataset.chapter|data-* 속성값을 JavaScript에서 읽는 접근 방식|목차 행에 적힌 챕터 번호를 읽는 상황|data-chapter의 값을 dataset.chapter로 읽는다|화면 표시 문구를 식별자로 써 제목 변경에 취약해진다|제목이 달라도 data-chapter 값으로 같은 항목을 찾는지 확인한다

## 07|브라우저 이벤트|workbook|JavaScript 이벤트|index.html
input 이벤트|field.addEventListener("input"|입력값이 편집될 때 실행할 동작을 연결하는 이벤트|타이핑한 목표 내용을 즉시 상태에 반영하는 상황|input 이벤트에서 event.target.value를 읽는다|입력 후 다른 동작 전까지 상태가 최신값을 반영하지 못한다|타이핑 직후 상태와 저장값이 갱신되는지 확인한다
change 이벤트|checkbox.addEventListener("change"|입력의 선택 변경에 반응하는 이벤트|체크박스 완료 상태가 바뀌는 상황|change 이벤트에서 event.target.checked를 읽는다|체크 변경이 완료 목록에 반영되지 않는다|체크와 해제 각각에 변경 처리가 실행되는지 확인한다
click 이벤트|toggleChapterHint.addEventListener("click"|요소의 클릭 동작에 처리 함수를 연결하는 이벤트|힌트 버튼을 눌러 영역을 여닫는 상황|버튼의 click 리스너에 토글 함수를 연결한다|버튼이 보여도 눌렀을 때 동작하지 않는다|같은 버튼을 두 번 눌러 열림과 닫힘을 확인한다
preventDefault|event.preventDefault()|이벤트가 가진 브라우저 기본 동작을 취소하는 메서드|폼 제출을 JavaScript에서 처리하고 현재 화면을 유지하는 상황|submit 리스너에서 preventDefault를 호출한다|기본 제출 이동이 일어나 편집 흐름이 끊길 수 있다|제출 버튼을 눌러도 의도하지 않은 페이지 이동이 없는지 확인한다
키보드 포커스 이동|selectedChapterTitle.focus|현재 키보드 조작 위치를 지정 요소로 옮기는 기능|선택한 학습 내용으로 이동한 뒤 제목을 안내하는 상황|이동 대상 제목에 프로그래밍 가능한 포커스를 준다|화면은 이동했는데 키보드 위치는 이전 버튼에 남는다|이동 뒤 document.activeElement가 대상 제목인지 확인한다

## 08|JSON과 브라우저 저장|workbook|JSON·localStorage|index.html
localStorage 저장 단위|localStorage.setItem|브라우저 저장소에 키와 문자열 값을 보관하는 API|학습 답안을 새로고침 후 다시 읽는 상황|이름 있는 저장 키에 직렬화한 문자열을 setItem으로 기록한다|메모리의 상태만 바꾸어 새로고침 시 답안이 사라진다|저장 후 새로고침해 입력한 답안이 복원되는지 확인한다
JSON.stringify|JSON.stringify(state)|JavaScript 값을 JSON 문자열로 직렬화하는 기능|객체 상태를 문자열 저장소에 넣는 상황|상태 객체를 stringify한 결과를 저장한다|객체를 직접 저장해 [object Object] 같은 문자열만 남을 수 있다|저장된 문자열이 유효 JSON이고 필요한 필드를 포함하는지 확인한다
JSON.parse|JSON.parse(localStorage.getItem|JSON 문자열을 JavaScript 값으로 복원하는 기능|이전 세션의 저장된 학습 상태를 불러오는 상황|가져온 문자열을 parse하여 객체로 복원한다|문자열을 객체처럼 다뤄 속성을 읽지 못한다|복원 결과의 자료형과 프로필 필드를 확인한다
저장 예외의 try·catch|storageEnabled = false|파싱이나 저장 실패를 잡아 대체 흐름으로 진행하는 처리|저장 데이터가 손상되었거나 저장소 접근이 거절된 상황|저장 접근을 try·catch로 감싸고 기본 상태와 안내를 제공한다|한 번의 저장 오류가 전체 화면 초기화를 멈춘다|깨진 JSON을 넣어도 기본 화면과 안내가 나타나는지 확인한다
브라우저 저장의 origin 경계|window.location.origin|프로토콜·호스트·포트가 같은 출처 안에서 저장을 공유하는 경계|로컬 서버의 포트를 바꾸어 워크북을 여는 상황|기록을 이어 쓰려면 같은 브라우저 프로필과 출처를 사용한다|다른 주소의 저장소를 같은 기록으로 오해한다|동일 출처 새로고침과 다른 포트 접속의 저장값을 비교한다

## 09|fetch와 비동기 처리|workbook|Fetch·Promise|problems.html
fetch 요청|await fetch(url)|브라우저에서 URL의 응답을 비동기로 요청하는 API|Markdown 문제 파일을 서버에서 받아오는 상황|파일 URL로 fetch를 호출해 응답을 받는다|문제 파일을 읽지 못해 카드에 넣을 데이터가 없다|네트워크 목록에서 요청 URL과 응답을 확인한다
async·await|async function fetchText|Promise 완료를 기다린 뒤 해당 함수의 처리를 이어가는 문법|파일을 읽은 다음에만 문제를 파싱해야 하는 상황|async 함수에서 응답 Promise를 await한다|아직 완료되지 않은 Promise를 문자열처럼 처리한다|응답이 늦어도 도착 후에만 문제 파싱이 실행되는지 확인한다
response.ok 검사|if (!response.ok)|HTTP 성공 상태인지 확인하는 응답 속성 검사|서버가 404 응답을 정상적으로 전송한 상황|fetch 완료 뒤 ok가 false이면 오류 흐름으로 보낸다|404 페이지를 문제 원문으로 잘못 해석할 수 있다|없는 파일 요청에서 정상 카드 대신 오류 안내가 나오는지 확인한다
response.text 변환|return response.text()|응답 본문을 문자열로 읽는 비동기 메서드|Markdown 본문을 제목별로 나누려는 상황|응답의 text 결과를 받아 문자열 파서에 전달한다|Response 객체를 본문 문자열로 착각한다|파서에 전달된 값이 실제 파일 내용의 문자열인지 확인한다
Promise.all|await Promise.all([|독립적인 여러 Promise의 완료를 함께 기다리는 기능|문제·정답·목차 파일이 모두 필요한 상황|파일 요청들을 Promise.all로 묶고 결과를 대응시킨다|필요한 자료가 준비되기 전에 화면 조합을 시작할 수 있다|한 요청을 늦춰도 자료가 모두 준비된 후 조합되는지 확인한다
`);
