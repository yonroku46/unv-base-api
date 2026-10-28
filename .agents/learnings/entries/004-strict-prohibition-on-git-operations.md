---
id: 004-strict-prohibition-on-git-operations
title: Git 스테이징 및 커밋 조작의 AI 자율 실행 엄금
date: 2026-10-19
scope: [git, protocol]
status: active
---

### 배경 및 발생 문제 (Problem / Context)
AI 에이전트가 작업 완료 시 독단으로 `git add`나 `git commit`을 실행하여 사용자로부터 "커밋 및 스테이징 작업은 AI가 임의로 수행하지 않고, 사용자가 직접 관리 및 판단할 수 있도록 해야 한다"는 명확한 방침 지침을 받음.

### 근본 원인 (Root Cause)
Git 버전 관리, 스테이징 범위 및 커밋 단위 결정은 사용자의 고유 권한이며, AI가 임의로 커밋을 생성하면 히스토리 오염이나 의도치 않은 변경 사항이 포함될 위험이 있음.

### 해결책 및 향후 규칙 (Rule & Solution)
1. **커밋 및 스테이징 절대 금지**:
   - `git add`, `git commit`, `git push`, `git reset` 등 Git 상태 및 이력을 변경하는 조작은 사용자의 명시적 지시가 없는 한 **일절 실행해서는 안 된다(엄금)**.
2. **검증 및 읽기 전용 명령만 허용**:
   - 빌드/컴파일 검증(`./gradlew compileJava`, `./gradlew test`) 및 읽기 전용 상태 확인(`git status`)만 실행을 허용한다.
3. **커밋의 사용자 위임**:
   - 작업 완료 시에는 변경된 파일 목록과 변경 내용 요약만을 보고하고, 실제 커밋 및 푸시 작업은 사용자에게 위임한다.

