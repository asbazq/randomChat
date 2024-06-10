# Websocket 채팅 애플리케이션

## 개요
이 프로젝트는 Spring Boot와 Redis를 사용하여 구축된 Websocket 기반 채팅 애플리케이션입니다. JWT를 통해 인증과 인가를 처리하며, 여러 채팅방에서 실시간 통신을 할 수 있습니다.

## 주요 기능
- **사용자 인증**: JWT를 이용한 로그인 및 회원가입
- **실시간 채팅**: Websockets를 통한 즉시 메시지 교환
- **채팅방 관리**: 채팅방 생성, 조회 및 입장
- **역할 기반 접근 제어**: 사용자와 관리자별 기능 구분
- **Redis 통합**: 채팅 메시지와 사용자 세션 관리

## 사용된 기술
- **백엔드**: Spring Boot, Spring Security, Redis, JWT
- **웹소켓**: Spring WebSocket
- **데이터베이스**: Redis

## 설정 방법

### 사전 준비
- Java 11 이상
- Redis 서버

### 백엔드 설정
1. **레포지토리 클론**:
   ```sh
   git clone <repository_url>
   cd project_directory
   ```
2. **Redis 설정**: 로컬에서 Redis가 실행 중인지 확인하거나 `application.yml`에서 설정을 업데이트합니다.
3. **애플리케이션 빌드 및 실행**:
   ```sh
   ./gradlew bootRun
   ```

### 애플리케이션 실행
- 애플리케이션에 접속: `http://localhost:8080`
- 회원가입, 로그인 및 채팅방 관리를 위해 제공되는 엔드포인트 사용

## API 엔드포인트

### 인증
- **POST** `/api/join` - 회원가입
- **POST** `/login` - 로그인

### 채팅방
- **GET** `/admin/rooms` - 모든 채팅방 조회
- **POST** `/admin/room` - 채팅방 생성
- **GET** `/admin/room/enter/{roomId}` - 채팅방 입장

## 코드 구조
- **Controller**: HTTP 요청 및 응답 처리
- **Service**: 비즈니스 로직 처리
- **Repository**: Redis와의 상호작용
- **Security**: JWT 설정 및 필터

## 샘플 코드
### ChatAdminController.java
```java
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class ChatAdminController {
    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> findAllRooms() {
        return chatRoomService.findAllRoom();
    }

    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto createRoom(@RequestParam String name) {
        return chatRoomService.createChatRoom(name);
    }
}
```

## 결론
이 프로젝트는 실시간 메시징, 사용자 인증, 역할 기반 접근 제어를 지원하는 전체 스택 채팅 애플리케이션 구현을 보여줍니다. 백엔드로는 Spring Boot를, 데이터 저장소로는 Redis를 사용합니다.
