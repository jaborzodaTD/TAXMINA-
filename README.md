# TAXMINA Translator

TAXMINA is a native Android translator and language-learning app built with Kotlin and Jetpack Compose.

## Languages

- Tajik (Тоҷикӣ)
- Russian (Русский)
- English
- Kazakh
- Uzbek
- German
- French
- Spanish
- Arabic
- Turkish

Translation uses the MyMemory online translation service with local phrase/dictionary fallback for supported Tajik pairs. Online translation requires an internet connection and availability of the third-party service.

## Features

- Text translation with source/target language selection and swap
- Ten language choices
- Searchable Russian ↔ Tajik and English ↔ Tajik dictionary
- Copy translated text and dictionary entries
- Text-to-speech for supported Android voices
- Speech input through Android speech recognition (device/service support required)
- Quick phrases, language-learning lessons, practice quiz, XP and streak UI
- Russian and Tajik app interface
- Dark premium Jetpack Compose UI
- Android 7.0+ target baseline (minSdk 24)

## Build APK

1. Open the Actions tab.
2. Select TAXMINA Stable APK.
3. Run the workflow, or push a commit to main.
4. Download the TAXMINA-debug artifact from the completed workflow.

Package: com.takhmina.translator

## Notes

Translation quality, language availability, speech recognition, and voice output depend on the selected language and third-party/device services. The app does not claim to provide a fully offline machine-translation model.
