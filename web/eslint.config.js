import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import prettier from "eslint-plugin-prettier";
// import eslintConfigPrettier from 'eslint-config-prettier'
import jsxA11y from "eslint-plugin-jsx-a11y";

export default [
  { ignores: ["dist", "node_modules"] }, // node_modules도 무시하도록 추가
  {
    files: ["**/*.{js,jsx,ts,tsx}"], // TypeScript 파일도 린트하도록 추가
    languageOptions: {
      ecmaVersion: "latest",
      globals: globals.browser,
      parserOptions: {
        ecmaFeatures: { jsx: true },
        sourceType: "module",
      },
    },
    plugins: {
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
      prettier: prettier,
      "jsx-a11y": jsxA11y,
    },
    rules: {
      ...js.configs.recommended.rules,
      ...reactHooks.configs.recommended.rules,
      "no-unused-vars": ["error", { varsIgnorePattern: "^[A-Z_]" }],
      "react-refresh/only-export-components": [
        "warn",
        { allowConstantExport: true },
      ],
      "prettier/prettier": "error", // Prettier 오류를 ESLint가 감지하도록 설정
      "jsx-a11y/anchor-is-valid": "warn", // 접근성 관련 경고 추가
    },
    settings: {
      react: {
        version: "detect", // React 버전을 자동 감지
      },
    },
  },
];
