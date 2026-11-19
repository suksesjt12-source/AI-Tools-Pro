# AI Tools Pro

AI Tools Pro is an Android application starter with a premium black-and-blue interface and a shared AI chat workflow for multiple tools.

## Included in this first build
- Kotlin + Jetpack Compose Android application
- Tool hub for Chat, Writer, Coding, Translator, Math, PDF, Image, Voice, Research, and Web Search
- OpenAI-compatible chat-completions integration, configured by the user
- API key encrypted with Android Keystore before it is stored on-device
- Indonesian and English interface labels
- GitHub Actions workflow that builds and uploads a debug APK

## Important service limits
The app does not bundle a paid API key and does not bypass provider limits. Configure a legitimate OpenAI-compatible provider and check its pricing, free-tier quota, model availability, and terms. A provider's free tier may be rate-limited or changed at any time. The generic chat adapter does not by itself implement dedicated image generation, speech recognition, PDF upload parsing, or live web search; those require their own provider adapters and permissions.

## Build APK
1. Open the Actions tab in this repository.
2. Select Build Android APK and run the workflow, or push a commit.
3. Download the AI-Tools-Pro-debug-apk artifact from the completed workflow run.

## Local build
Use JDK 17 and Gradle 8.7:
```bash
gradle :app:assembleDebug
```

The resulting APK is at app/build/outputs/apk/debug/app-debug.apk.

## Provider setup
Open Settings in the app and enter:
- Base URL of the provider's OpenAI-compatible API (for example, an endpoint ending in /v1)
- Model identifier supported by that provider
- Your own API key

Use HTTPS endpoints only. API keys are encrypted on-device with Android Keystore. Do not commit keys to this repository.

## Current scope
This is the first runnable MVP foundation. Chat requests use the configured provider. Tool tiles supply task-specific prompts to that chat workflow. Local history, account registration/cloud synchronization, and dedicated media/search adapters are follow-up development items and are not represented as completed features.
