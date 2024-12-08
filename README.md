# 📋 매장 테이블 예약 서비스 구현
### 📌 간략 소개
#### 매장의 점장은 매장 정보를 등록하고 회원은 원하는 매장을 예약하며, 매장 이용 후 리뷰를 작성할 수 있는 서비스를 제공하는 api 설계 프로젝트입니다.

## ⚙Tech Stack
- **Framework** : Spring Boot 3.3.5
- **Language** : Java
- **Build** : Gradle
- **JDK** : JDK 17
- **Database** : MariaDB
- **Library** : Lombok, Spring-web, SpringSecurity, JPA, Jwt

## 💡 ERD
![ERD](https://github.com/user-attachments/assets/8d3ac48b-0027-411f-8be5-f4c9b15f0b26)

## 💡 구현 기능
- JWT 토큰을 통한 회원 인증/인가
- 회원 권한 부여(점장 : MANAGER, 회원 : USER)
- 점장이 매장 정보를 등록하는 기능 -> 매장 CRUD
- 회원이 매장을 검색하고 상세 정보를 조회하는 기능
- 회원이 예약하고자 하는 매장에 예약을 진행하는 기능 -> 예약 CRD
- 예약 후 점장이 승인을 완료해야 예약이 완료되는 기능
- 예약 완료 후 예약 시간 10분 전까지 매장에 방문하여 방문 인증을 진행하는 기능
- 매장 이용 후 리뷰를 작성하는 기능 -> 리뷰 CRUD


### ✅ 회원 API
#### 1) POST - users/auth/signup
- 회원가입 API
- 중복 ID 는 허용하지 않음
- 패스워드는 암호화된 형태로 저장

#### 2) POST - users/auth/login
- 로그인 API
- 회원가입이 되어있고, 아이디/패스워드 정보가 옳은 경우 JWT 발급

#### 3) GET - users/{userId}
- 회원 정보 조회 API

#### 4) PUT - users/update
- 회원 정보 수정 API

#### 5) PUT - users/delete
- 회원 정보 삭제 API
  
### ✅ 매장 API 
#### 1) POST - stores/create (MANAGER 권한)
- 매장 등록 - MANAGER 권한을 가진 사용자만 등록 가능
- 매장 정보를 인풋으로 받아서 해당 매장 정보를 반환

#### 2) PUT - stores/{storeId} (MANAGER 권한)
- 매장 정보 수정 - 해당 매장의 점주만 수정 가능

#### 3) DELETE /stores/{storeId} (MANAGER 권한)
- 매장 삭제 - 해당 매장의 점주만 삭제 가능

#### 4) GET /stores/my/{userId} (MANAGER 권한)
- 내 매장 목록 조회 - 해당 매장의 점주만 수정 가능

#### 5) GET /stores/search?keyword=키워드&page=0&size=10
- 매장 키워드 검색 - 모든 사용자
- keyword: 검색할 키워드
- page: 페이지 번호 (기본값 0)
- size: 한 페이지당 항목 수 (기본값 10)
- 반환 결과는 Page 인터페이스 형태

#### 6) GET /stores/list?sortBy=name&page=0&size=10
- sortBy: 정렬 기준 ("name" 또는 "rating")
- page: 페이지 번호
- size: 한 페이지당 항목 수

#### 7) GET /stores/{storeId}
- 매장 상세 정보 조회 (모든 사용자)

### ✅ 예약 API
#### 1) POST /reservations (USER 권한)
- 예약 생성 - USER 권한 필요
- 예약 정보를 인풋으로 받아서 해당 예약 정보를 반환

#### 2) PUT /reservations/{reservationId}/cancel (USER 권한)
- 예약 취소 - 예약한 USER만 가능

#### 3) PUT /reservations/{reservationId}/status?status=CONFIRMED (MANAGER 권한)
- 예약 상태 변경 - 해당 매장 점주 가능
- status 값으로 가능한 옵션:
  - PENDING (대기중)
  - CONFIRMED (확인됨)
  - COMPLETED (완료)
  - CANCELLED (취소됨)

#### 4) GET /reservations/my?page=0&size=10 (USER 권한)
- 내 예약 목록 조회 
- 페이징 처리:
  - page: 페이지 번호 (기본값 0)
  - size: 페이지당 항목 수 (기본값 10)

#### 5) GET /reservations/store/{storeId}?status=PENDING&page=0&size=10 (MANAGER 권한)
- 매장의 예약 목록 조회 
- status: 예약 상태 필터 (선택사항)
- page, size: 페이징 정보

#### 6) GET /reservations/{reservationId} (USER 또는 MANAGER 권한)
- 예약 상세 조회 
- sortBy: 정렬 기준 ("name" 또는 "rating")
- page: 페이지 번호
- size: 한 페이지당 항목 수

#### 7) PUT /reservations/arrival
- 키오스크 도착 확인 (인증 불필요)

### ✅ 리뷰 API
#### 1) POST /reviews (USER 권한)
- 리뷰 작성
- 예약이 완료(COMPLETED) 상태인 경우에만 리뷰를 작성
- rating은 1-5점 사이이며, 내용은 10자에서 500자 사이

#### 2) PUT /reviews/{reviewId} (USER 권한)
- 리뷰 수정 
- 자신이 작성한 리뷰만 수정할 수 있으며, 평점과 내용을 변경

#### 3) DELETE /reviews/{reviewId} (USER, MANAGER 권한)
- 리뷰 삭제 
- 리뷰 작성자나 해당 매장의 관리자만 삭제가 가능

#### 4) GET /reviews/my?page=0&size=10 (USER, MANAGER 권한)
- 자신이 작성한 모든 리뷰 or 매장에 작성된 리뷰를 최신순으로 조회
- 페이징 처리:
  - page: 페이지 번호 (기본값 0)
  - size: 페이지당 항목 수 (기본값 10)

#### 5) GET /reviews/store/{storeId}?page=0&size=10
- 매장별 리뷰 목록 조회 - 모든 사용자
- 특정 매장의 모든 리뷰를 조회

#### 6) GET /reviews/{reviewId}
- 리뷰 상세 조회 - 모든 사용자
- 특정 리뷰의 상세 정보를 조회

