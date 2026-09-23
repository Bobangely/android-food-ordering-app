# FlyFood — Android Food Ordering App

FlyFood is a native Android food-ordering demo built with Kotlin and XML layouts. It demonstrates a complete local ordering flow: browse food, add items to a cart, adjust quantities, view promotions, and confirm an order.

> Portfolio project: this app is an offline prototype. Authentication, payment, and order submission are simulated locally and are not connected to a production backend.

## Screenshots

| Login | Home | Promotion |
| --- | --- | --- |
| <img src="docs/screenshots/login.png" width="240" alt="FlyFood login screen"> | <img src="docs/screenshots/home.png" width="240" alt="FlyFood home screen"> | <img src="docs/screenshots/promotion.png" width="240" alt="FlyFood promotion screen"> |

| Takeaway | Cart | Order confirmation |
| --- | --- | --- |
| <img src="docs/screenshots/takeaway.png" width="240" alt="FlyFood takeaway screen"> | <img src="docs/screenshots/cart.png" width="240" alt="FlyFood cart screen"> | <img src="docs/screenshots/order-confirmation.png" width="240" alt="FlyFood order confirmation screen"> |

## Features

- Login, guest access, and registration screens
- Food menu and category browsing
- Promotional menu selection
- Add-to-cart flow with persistent local storage
- Increase and decrease item quantities
- Automatic subtotal, 5% tax, and total calculation
- Clear-cart and order-confirmation dialogs
- Screen transitions and interaction animations
- Thai-language ordering experience

## Tech stack

- Kotlin
- Android SDK (min SDK 24, target SDK 36)
- XML layouts and Android Views
- SQLite with `SQLiteOpenHelper`
- Material Components and AndroidX
- Gradle Kotlin DSL

## Project structure

```text
app/src/main/
├── java/com/example/flyfood/   # Activities, cart logic, SQLite helper
├── res/layout/                 # Screen and list-item layouts
├── res/drawable/               # Images and custom drawable resources
├── res/anim/                   # UI animations
└── AndroidManifest.xml
```

## Run locally

1. Clone this repository.
2. Open the project in Android Studio.
3. Let Gradle sync the dependencies.
4. Run the `app` configuration on an Android emulator or device running Android 7.0 (API 24) or newer.

Command-line build on Windows:

```powershell
.\gradlew.bat assembleDebug
```

## What I learned

- Building multi-screen Android applications with Activities and Intents
- Designing responsive interfaces with XML layouts
- Persisting and updating cart data in SQLite
- Implementing reusable list adapters and user interaction feedback
- Managing Android resources, animations, and Gradle dependencies

## Future improvements

- Replace the local login prototype with secure authentication
- Add a REST API and cloud-backed order history
- Integrate a real payment provider
- Migrate UI state to ViewModel/StateFlow and adopt a layered architecture
- Add unit, database, and UI tests for the ordering flow
