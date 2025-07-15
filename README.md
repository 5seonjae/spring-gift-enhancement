# spring-gift-enhancement

### Spring-gift-product README.md - https://github.com/5seonjae/spring-gift-product/blob/step3/README.md

### Spring-gift-wishlist README.md - https://github.com/5seonjae/spring-gift-wishlist/blob/step3/README.md

---

## 🚀 Step 1 – 엔티티 매핑

### 🎯 기능 요구사항 체크리스트

- [ ] **DDL 검토 & Migration**
    - 기존 테이블 스키마(MySQL / H2) 확인
    - `members`, `products`, `wish`, `approved_product` 테이블에 필요한 컬럼·제약조건 추가/수정 스크립트 작성
- [ ] **Entity 클래스 작성**
    - `Member`, `Product`, `Wish`, (`ApprovedProduct`) 엔티티 생성
    - JPA 어노테이션으로 컬럼·PK·외래키 매핑
    - 양방향 관계 매핑 시 `mappedBy`, `cascade`, `orphanRemoval` 설정
- [ ] **Repository 인터페이스 정의**
    - `MemberRepository extends JpaRepository<Member,Long>`
    - `ProductRepository`, `WishRepository`, `ApprovedProductRepository` 등
    - 커스텀 조회 메서드 선언(ex. `List<Wish> findAllByMemberId(Long)` )
- [ ] **application.yml(또는 .properties) 설정**
    - MySQL + H2(test) 데이터소스 분리
    - `spring.jpa.show-sql=true`
    - `spring.jpa.properties.hibernate.format_sql=true`
    - 테스트 시 `ddl-auto: create-drop`, 운영 시 `ddl-auto: none`
- [ ] **Service 레이어 리팩터링**
    - 기존 `JdbcClient` 구현체 → JPA Repository 주입으로 전환
    - 회원가입·로그인·상품등록·찜목록 기능 흐름 점검
- [ ] **학습 테스트 작성**
    - `@DataJpaTest`로 각 Repository의 save/find 동작 검증
- [ ] **불필요한 JdbcClient 코드 정리**
    - 마이그레이션 완료 후 보조 패키지로 이동 또는 삭제 

### 📐 DDL (MySQL 예시)

```sql
CREATE TABLE products
(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(255) NOT NULL,
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
email VARCHAR(255) NOT NULL,
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