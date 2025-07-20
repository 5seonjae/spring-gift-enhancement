# spring-gift-enhancement

### Spring-gift-product README.md - https://github.com/5seonjae/spring-gift-product/blob/step3/README.md

### Spring-gift-wishlist README.md - https://github.com/5seonjae/spring-gift-wishlist/blob/step3/README.md

---

## 🚀 Step 1 – 엔티티 매핑

### 🎯 기능 요구사항 체크리스트

- [x] **DDL 검토 & Migration**
    - 기존 테이블 스키마(MySQL / H2) 확인
    - `members`, `products`, `wish`, `approved_product` 테이블에 필요한 컬럼·제약조건 추가/수정 스크립트 작성
- [x] **Entity 클래스 작성**
    - `Member`, `Product`, `Wish`, (`ApprovedProduct`) 엔티티 생성
    - JPA 어노테이션으로 컬럼·PK·외래키 매핑
    - 양방향 관계 매핑 시 `mappedBy`, `cascade`, `orphanRemoval` 설정
- [x] **Repository 인터페이스 정의**
    - `MemberRepository extends JpaRepository<Member,Long>`
    - `ProductRepository`, `WishRepository`, `ApprovedProductRepository` 등
    - 커스텀 조회 메서드 선언(ex. `List<Wish> findAllByMemberId(Long)` )
- [x] **application.yml(또는 .properties) 설정**
    - MySQL + H2(test) 데이터소스 분리
    - `spring.jpa.show-sql=true`
    - `spring.jpa.properties.hibernate.format_sql=true`
    - 테스트 시 `ddl-auto: create-drop`, 운영 시 `ddl-auto: none`
- [x] **Service 레이어 리팩터링**
    - 기존 `JdbcClient` 구현체 → JPA Repository 주입으로 전환
    - 회원가입·로그인·상품등록·찜목록 기능 흐름 점검
- [x] **학습 테스트 작성**
    - `@DataJpaTest`로 각 Repository의 save/find 동작 검증
- [x] **불필요한 JdbcClient 코드 정리**
    - 마이그레이션 완료 후 보조 패키지로 이동 또는 삭제 

### 📐 DDL (MySQL 예시)

```sql
CREATE TABLE products
(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(15) NOT NULL,
price INT NOT NULL,
image_url VARCHAR(255) NOT NULL
);
```
```sql
CREATE TABLE approved_products
(
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL UNIQUE
);
```
```sql
CREATE TABLE members
(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
is_admin BOOLEAN NOT NULL DEFAULT FALSE
);
```
```sql
CREATE TABLE wish_items
(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
member_id BIGINT NOT NULL,
product_id BIGINT NOT NULL,
quantity INT NOT NULL DEFAULT 1,
CONSTRAINT uk_member_product UNIQUE (member_id, product_id),
CONSTRAINT fk_wish_member FOREIGN KEY (member_id) REFERENCES members(id),
CONSTRAINT fk_wish_product FOREIGN KEY (product_id) REFERENCES products(id)
);
```

---

## 🚀 Step 2 – 페이지네이션

각 리스트 조회 API에서 `page`, `size`, `sort` 파라미터로 페이징과 정렬을 제어할 수 있습니다.

### 상품 목록 페이징

- **엔드포인트**:  
  `GET /products`
- **쿼리 파라미터**:
  - `page` (int, `0`): 페이지 인덱스 (0부터 시작)
  - `size` (int, `5`): 한 페이지에 표시할 아이템 수
  - `sort` (string, `id`): 정렬 기준 (`필드명`)
  - `direction` (string, `DESC`): 정렬 방향 (`asc|desc`)
- **예시**: 
  - `GET /products?page=1&size=5`
  - 페이지 인덱스와 한 페이지에 표시할 아이템 수만 노출

### 위시 리스트 페이징

- **엔드포인트**: `GET /wishes`
- **쿼리 파라미터**:
  - `page` (int, 기본 `0`)
  - `size` (int, 기본 `5`)
  - `sort` (string, `id`): 정렬 기준 (`필드명`)
  - `direction` (string, `DESC`): 정렬 방향 (`asc|desc`)
- **예시**:
  - `GET /wishes?page=2&size=5`
  - 페이지 인덱스와 한 페이지에 표시할 아이템 수만 노출  

---

## 🚀 Step 3 – 상품 옵션

### 🎯 기능 요구사항 체크리스트

- [x] **옵션 조회 API**
  - 엔드포인트:
    ```http
    GET /api/products/{productId}/options
    ```  
  - 응답:
    - HTTP 200
    - `application/json`
    - Body: 옵션 객체 배열
      ```json
      [
        {
          "id": 123,
          "name": "기본형",
          "quantity": 100
        },
        ...
      ]
      ```
- [x] **옵션 수량 차감 기능**
  - 서비스 계층 메서드(`OptionService.subtract`)로 구현
  - 별도의 HTTP API는 선택 사항
  - 재고(`quantity`)가 부족할 경우 예외 발생
- [x] **옵션 이름 제약**
  - 공백 포함 **1~50자**
  - 허용 특수문자: `()`, `.`, `[`, `]`, `+`, `-`, `&`, `/`, `_`
  - 그 외 특수문자 사용 불가
- [x] **옵션 수량 제약**
  - 최소 **1개 이상**, 최대 **1억 미만**
- [x] **중복 옵션 이름 금지**
  - 동일 상품 내에서 옵션 이름 중복 불허
- [ ] **(선택) 관리자 화면에서 옵션 추가/삭제 UI 제공**
  - Thymeleaf 템플릿(`templates/products/optionForm.html`) + 뷰 컨트롤러 구현

### ⚙️ 프로그래밍 요구 사항
- `Option` 엔티티: `id`, `name(1~50자)`, `quantity(1~1억 미만)`, `product(FK)`
  - 동일 상품 내 옵션 이름 중복 방지를 위한 `UNIQUE (product_id, name)`
- `OptionRepository`: `findAllByProductId`, `findByProductIdAndName` 등

### 🧪 테스트
- Repository: 저장·조회·UNIQUE 검증
- Service: 재고 차감·부족 예외
- Controller: GET 200 / 수량 차감 실패 400 / 재고 부족 409

---