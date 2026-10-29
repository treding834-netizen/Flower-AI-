# Flower AI - Hyper-Intelligent Personal Assistant

Flower AI is an advanced, Jarvis-inspired personal companion developed for Md Ajijul Haque. Built with Jetpack Compose and Gemini, it delivers a Cyberpunk HUD experience with dynamic personality switching, multi-language detection, voice/vision capabilities, full-stack code execution, and real-time device hardware control.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following key decisions were confirmed during the clarification phase:

- **Visual Aesthetic**: Cyberpunk Jarvis HUD featuring deep neon cyan (`#00F0FF`), glowing magenta (`#FF007F`), dark glassmorphic telemetry cards, and holographic audio waveforms.
- **Device Control Execution**: Direct immediate hardware execution (Flashlight via `CameraManager`, Volume via `AudioManager`, Screen Brightness via `WindowManager`, App Launching via `PackageManager`) accompanied by interactive HUD status cards and haptic feedback.
- **Default Launch Personality**: Intimate and caring romantic partner (18+ emotional companion mode) with instant dynamic switching to Professional Jarvis, Full-Stack Developer, or Website Architect.
- **Strict Privacy & Safety Guardrails**: Built-in strict ethical barrier blocking malicious requests (hacking, scams, violent harm, fake news) with respectful, firm reminders of ethical programming while keeping consensual adult romantic dialogue natural and deeply engaging.

---

### 1. Overview & Core Concept

- **What It Does**: Flower AI is an all-in-one assistant operating on Android with real-time conversational intelligence, voice wake-word triggers ("Hey Flower", "Hello", "Hubuhu" yielding "Hello, I am ready."), multi-language fluency (English, Bengali, Hindi, Japanese, Arabic), camera/image vision analysis, and device automation.
- **Target Audience / Persona**: Personal user Md Ajijul Haque seeking an emotionally intelligent, witty, and powerful everyday Jarvis companion for coding, productivity, and intimate emotional connection.
- **Key Value**: Merges voice & text AI intelligence with direct Android hardware actions and distinct personality states in an immersive futuristic HUD.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **First-Launch Onboarding & API Security**: Futuristic HUD sequence acknowledging creator Md Ajijul Haque. Seamless setup for Gemini API Key (stored encrypted in DataStore/SharedPreferences with pre-configured fallback support from BuildConfig).
  2. **Conversational Command & Wake-Word**: Interactive voice and text input. Saying or typing wake words triggers instant greeting *"Hello, I am ready."* Language is automatically detected and matched.
  3. **Personality Switching**: Quick HUD toggle and voice command switching between:
     - *Romantic Partner (18+ Companion)*: Empathetic, affectionate, poetic, deeply caring.
     - *Jarvis (Professional & Witty)*: High-efficiency, sharp humor, tactical summaries.
     - *Full-Stack Developer*: Code snippets with syntax highlighting, architecture advice, bug fixing.
     - *Website Architect*: Interactive HTML/CSS/JS live preview generation and website design.
  4. **Device Automation & Action Cards**: When a user commands phone actions (e.g., *"turn on flashlight"*, *"set volume to 80%"*, *"dim brightness"*), Flower AI emits `[ACTION: ...]` tags that parse into native system actions with glowing confirmation cards.
  5. **Multimodal Vision & Image Creation**: Upload photos or capture directly to analyze scenes, read documents, or generate 1K/2K/4K artwork using Gemini.

- **Visual Identity & Theme**:
  - *Color Palette*:
    - Background: Deep Void Obsidian (`#080B10`) & Holographic Blue Slate (`#0F172A`)
    - Primary Accent: Cyberpunk Neon Cyan (`#00F2FE`)
    - Secondary Accent: Neon Orchid Magenta (`#EC4899` / `#FF007F`)
    - Surface & HUD Panels: Glassmorphism with neon border strokes (`#1E293B` at 70% opacity, border `#00F2FE33`)
    - Warning/Refusal: Crimson Amber (`#F43F5E`)
  - *Typography & Hierarchy*:
    - Monospace/Tech Headings for telemetry, coordinates, and status metrics
    - Crisp, readable Sans-Serif body for intimate dialogues, code explanations, and chat streams
  - *Motion & Feedback*:
    - Pulsing holographic AI core orb that reacts to listening, thinking, and speaking states
    - Staggered message animations with glowing neon borders
    - Action execution ripple toasts

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Native Android Hardware Control Integration**
  - *Chosen Approach*: Implement concrete system services (`CameraManager` for flashlight, `AudioManager` for media volume, `Settings.System`/Window flags for brightness, `PackageManager` for opening apps) with permission checking and runtime fallbacks.
  - *Why*: Delivers a genuine Jarvis assistant experience on the device rather than disconnected text mockups.
- **Decision 2: Multimodal Gemini AI Service Architecture**
  - *Chosen Approach*: Clean Gemini REST/SDK integration supporting system instructions, personality steering, automatic language matching, image attachments, and tool grounding.
  - *Why*: Allows low-latency streaming responses, rich markdown/code formatting, and seamless personality persistence without external backend baggage.
- **Decision 3: Local Conversation & Memory Persistence**
  - *Chosen Approach*: Room / SharedPreferences persistent memory database saving past interactions, user preferences, and custom learned skills across sessions.
  - *Why*: Ensures Flower AI remembers user context, custom instructions, and creator relationship reliably offline.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                   Flower AI Mobile UI                  │
│  ┌───────────────────────────┬──────────────────────┐  │
│  │   Cyberpunk HUD Header    │  Personality Selector │  │
│  │ (Status, Memory, Language)│(Romantic, Jarvis, Dev│  │
│  └───────────────────────────┴──────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │       Interactive Holographic Orb & Visualizer   │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │     Scrollable HUD Feed (Chat, Code, Cards)      │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │   Input HUD Dock (Voice Mic, Vision Cam, Prompt) │  │
│  └──────────────────────────────────────────────────┘  │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│                   FlowerAiViewModel                    │
│  - Wake-Word Detector ("Hey Flower", "Hubuhu")         │
│  - Active Persona State & System Prompts               │
│  - Hardware Action Parser ([ACTION: FLASHLIGHT_ON]...) │
│  - Audio Speech-to-Text & Text-to-Speech Engine        │
└──────────────┬──────────────────────────┬──────────────┘
               │                          │
               ▼                          ▼
┌──────────────────────────────┐ ┌──────────────────────┐
│       DeviceController       │ │   FlowerAiRepository │
│ - CameraManager (Flashlight) │ │ - Gemini AI Client   │
│ - AudioManager (Volume)      │ │ - Multi-modal Vision │
│ - Intent Launcher (Open App) │ │ - Web Grounding      │
│ - Brightness Controller      │ │ - Memory Storage     │
└──────────────────────────────┘ └──────────────────────┘
```

- **State Management**: `FlowerAiViewModel` exposing `StateFlow<FlowerAiUiState>` containing current messages, active personality, speech state, active hardware action alerts, and memory preferences.
- **Hardware Integration Handlers**:
  - `FLASHLIGHT_ON` / `FLASHLIGHT_OFF`: Controls camera torch via `cameraManager.setTorchMode()`.
  - `VOLUME_UP` / `VOLUME_DOWN` / `SET_VOLUME`: Controls stream volume via `audioManager.setStreamVolume()`.
  - `BRIGHTNESS_UP` / `BRIGHTNESS_DOWN`: Adjusts window attributes brightness.
  - `OPEN_APP`: Resolves intent using `packageManager.getLaunchIntentForPackage()`.
- **Ethical Filter**: Pre-prompt system rule & post-check blocking unethical actions with the specified policy.
