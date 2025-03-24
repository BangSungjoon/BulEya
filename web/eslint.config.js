// ESLint의 기본 JavaScript 규칙 세트 (@eslint/js에서 제공)
import js from '@eslint/js'

// 브라우저 환경에서 사용 가능한 전역 변수 정의 (e.g. window, document 등)
import globals from 'globals'

// React 훅 규칙 제공 플러그인 (e.g. useEffect 의존성 검사)
import reactHooks from 'eslint-plugin-react-hooks'

// React Fast Refresh 사용 시 필요한 규칙 제공 (e.g. HMR 안전성 검사)
import reactRefresh from 'eslint-plugin-react-refresh'

// JSX 접근성 관련 규칙 제공 (웹 접근성 향상)
import jsxA11y from 'eslint-plugin-jsx-a11y'

// Prettier와 ESLint의 포맷팅 규칙 충돌 방지 (Prettier가 포맷만 책임지고 ESLint는 품질만 검사하도록 함)
import eslintConfigPrettier from 'eslint-config-prettier'

export default [
  {
    // 린트 대상에서 제외할 디렉토리 목록
    ignores: ['dist', 'node_modules'],
  },
  {
    // 린트를 적용할 파일 확장자 지정 (JS, JSX, TS, TSX 모두 포함)
    files: ['**/*.{js,jsx,ts,tsx}'],

    // 언어 옵션 설정 (최신 ECMAScript, 브라우저 전역 변수 등)
    languageOptions: {
      ecmaVersion: 'latest', // 최신 ECMAScript 문법 지원
      globals: globals.browser, // 브라우저 전역 변수 사용 가능하게 설정
      parserOptions: {
        ecmaFeatures: { jsx: true }, // JSX 문법 활성화
        sourceType: 'module', // ES 모듈 사용 설정 (import/export)
      },
    },

    // 사용할 ESLint 플러그인 목록
    plugins: {
      'react-hooks': reactHooks, // React Hooks 규칙
      'react-refresh': reactRefresh, // React Fast Refresh 규칙
      'jsx-a11y': jsxA11y, // 웹 접근성 관련 규칙
    },

    // 개별 규칙 설정 (extends보다 우선 적용됨)
    rules: {
      // 사용되지 않는 변수 에러, 단 대문자나 언더스코어로 시작하는 변수는 무시
      'no-unused-vars': ['error', { varsIgnorePattern: '^[A-Z_]' }],

      // React 컴포넌트는 default export 대신 named export 권장 (Fast Refresh 관련)
      'react-refresh/only-export-components': ['warn', { allowConstantExport: true }],

      // a 태그의 유효성 검사 (접근성 향상)
      'jsx-a11y/anchor-is-valid': 'warn',
    },

    // React 버전 자동 감지 (rules 중 일부는 React 버전에 따라 다르게 동작하므로 필수)
    settings: {
      react: {
        version: 'detect',
      },
    },

    // 규칙 집합을 확장 (여기서 선언된 규칙이 위 `rules`보다 먼저 적용됨)
    extends: [
      js.configs.recommended, // ESLint의 기본 JS 권장 규칙
      'plugin:react-hooks/recommended', // React Hooks 권장 규칙
      'plugin:jsx-a11y/recommended', // 접근성 권장 규칙
      eslintConfigPrettier, // 포맷 관련 규칙 비활성화 (Prettier와 충돌 방지)
    ],
  },
]
