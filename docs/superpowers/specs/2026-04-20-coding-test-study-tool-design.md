# Coding Test Study Tool - Design Spec

## Overview

알고리즘/자료구조 코딩테스트 준비를 위한 개인 학습 도구. 알고리즘 카테고리별로 문제를 등록하고, 풀이 코드와 해설을 관리하며, 풀이 상태를 추적한다.

## Goals

- 문제를 카테고리(DP, 그래프, 정렬 등)별로 정리하고 관리
- 문제별 풀이 코드와 해설/메모를 기록
- 풀이 상태(미시도/풀었음/못풀었음/다시풀기)를 추적
- 대시보드에서 전체 진행 상황을 한눈에 파악

## Tech Stack

| Area | Technology |
|------|------------|
| Backend | Spring Boot 4.0.5, Java 25 |
| Template Engine | Thymeleaf |
| ORM | Spring Data JPA |
| Database | H2 (embedded, file mode) |
| CSS | Bootstrap 5 (CDN) |
| Code Highlighting | highlight.js (CDN) |
| Build | Gradle |

## Data Model

### Category

| Field | Type | Description |
|-------|------|-------------|
| id | Long (PK, auto) | 고유 식별자 |
| name | String (unique, not null) | 카테고리명 (DP, 그래프 등) |
| description | String | 카테고리 설명 |

### Problem

| Field | Type | Description |
|-------|------|-------------|
| id | Long (PK, auto) | 고유 식별자 |
| title | String (not null) | 문제 제목 |
| sourceUrl | String | 출처 링크 (백준/프로그래머스 URL) |
| difficulty | Enum: EASY, MEDIUM, HARD | 난이도 |
| status | Enum: NOT_ATTEMPTED, SOLVED, FAILED, RETRY | 풀이 상태 |
| categories | Set\<Category\> (ManyToMany) | 연결된 카테고리 목록 |
| createdAt | LocalDateTime | 생성일시 |
| updatedAt | LocalDateTime | 수정일시 |

### Solution

| Field | Type | Description |
|-------|------|-------------|
| id | Long (PK, auto) | 고유 식별자 |
| problem | Problem (ManyToOne, not null) | 연결된 문제 |
| code | String (Lob, not null) | 풀이 코드 |
| language | Enum: JAVA, PYTHON, CPP, JAVASCRIPT | 프로그래밍 언어 |
| note | String (Lob) | 해설/메모 (접근법, 핵심 아이디어) |
| createdAt | LocalDateTime | 생성일시 |
| updatedAt | LocalDateTime | 수정일시 |

### Relationships

- Problem ↔ Category: ManyToMany (join table: problem_category)
- Problem → Solution: OneToMany (한 문제에 여러 풀이)

## Package Structure

```
lemuel.com.codingtest
├── category
│   ├── Category.java
│   ├── CategoryRepository.java
│   ├── CategoryService.java
│   └── CategoryController.java
├── problem
│   ├── Problem.java
│   ├── Difficulty.java
│   ├── SolveStatus.java
│   ├── ProblemRepository.java
│   ├── ProblemService.java
│   └── ProblemController.java
├── solution
│   ├── Solution.java
│   ├── Language.java
│   ├── SolutionRepository.java
│   ├── SolutionService.java
│   └── SolutionController.java
└── CodingtestApplication.java
```

## Pages & Routes

### Dashboard (`GET /`)

- 상태별 문제 수 카드 (전체 / SOLVED / FAILED / RETRY / NOT_ATTEMPTED)
- 카테고리별 진행률 바
- 최근 등록/수정된 문제 5개

### Problem List (`GET /problems`)

- 테이블: 제목, 난이도 뱃지, 카테고리 태그, 상태 뱃지, 출처 링크
- 필터: 카테고리 드롭다운, 난이도 셀렉트, 상태 셀렉트, 검색어 입력
- 문제 등록 버튼

### Problem Create (`GET /problems/new`, `POST /problems`)

- 입력: 제목, 출처 URL, 난이도 선택, 카테고리 다중 선택
- 저장 후 문제 상세로 리다이렉트

### Problem Edit (`GET /problems/{id}/edit`, `POST /problems/{id}`)

- 기존 정보 수정 폼
- 저장 후 문제 상세로 리다이렉트

### Problem Detail (`GET /problems/{id}`)

- 문제 정보 표시 (제목, 출처 링크, 난이도, 카테고리, 상태)
- 상태 변경 드롭다운 (`POST /problems/{id}/status`)
- 풀이 목록 (작성일 역순, highlight.js로 코드 구문 강조)
- 각 풀이: 코드 + 해설/메모 + 수정/삭제 버튼
- 풀이 추가 버튼

### Solution Create (`GET /problems/{id}/solutions/new`, `POST /problems/{id}/solutions`)

- 입력: 언어 선택, 코드 textarea, 해설/메모 textarea
- 저장 후 문제 상세로 리다이렉트

### Solution Edit (`GET /problems/{id}/solutions/{sid}/edit`, `POST /problems/{id}/solutions/{sid}`)

- 기존 풀이 수정 폼

### Category Management (`GET /categories`)

- 카테고리 목록 (이름, 설명, 소속 문제 수)
- 인라인 추가 (`POST /categories`)
- 인라인 수정 (`POST /categories/{id}`)
- 삭제 (`POST /categories/{id}/delete`)

### Problem Delete (`POST /problems/{id}/delete`)

- 연결된 풀이도 함께 삭제 (cascade)

## Key Design Decisions

1. **도메인형 패키지 구조**: 계층형(controller/service/repository) 대신 도메인별(category/problem/solution) 패키지로 관련 코드를 한곳에 모음
2. **H2 파일 모드**: 애플리케이션 재시작 시에도 데이터 유지. 나중에 다른 DB로 전환 용이
3. **DTO 최소화**: 개인 학습 도구이므로 초기에는 Entity 직접 사용, 필요 시 추가
4. **Bootstrap + CDN**: 별도 프론트엔드 빌드 없이 깔끔한 UI 구성
5. **카테고리 다대다**: 하나의 문제가 여러 카테고리에 속할 수 있도록 ManyToMany 관계
6. **풀이 히스토리**: 한 문제에 여러 풀이를 남겨 성장 과정을 추적

## Non-Goals (Out of Scope)

- 사용자 인증/인가 (개인 도구)
- 자동 채점/코드 실행
- 외부 API 연동 (백준/프로그래머스 크롤링)
- 모바일 최적화
