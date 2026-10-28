---
id: 003-korean-instructions-japanese-code-comments-constraint
title: 타겟 로케일 한국어 전환 (한국어 코드 주석 및 메시지 작성 철저)
date: 2026-10-28
scope: [locale, i18n, documentation, comments]
status: active
---

### 배경 및 변경 사유 (Context)
기존 일본어 타겟 웹 서비스 보일러플레이트에서 한국어 전용 웹 애플리케이션을 구축하기 위해 타겟 로케일(`Asia/Seoul`, `ko`) 및 전체 응답 메시지, 유효성 검사 메시지, JavaDoc 및 주석을 한국어로 전환함.

### 원칙 및 향후 규칙 (Rule & Solution)
1. **타겟 로케일**: 대한민국 (`Asia/Seoul`, `ko`).
2. **코드 및 메시지 작성**:
   - 리포지토리 내에 생성/변경하는 모든 파일(JavaDoc, 코드 내 주석, Markdown 문서, 메시지 프로퍼티 파일 `ValidationMessages.properties`, 예외 및 로그 문구)은 **100% 자연스러운 한국어(해요체/하십시오체)**로 작성한다.
3. **설정 정합성**:
   - `LocaleAspect.java`: 기본 로케일 `Locale.KOREA`, 기본 언어 `ko`
   - `application-dev.yml`: `serverTimezone=Asia/Seoul`
