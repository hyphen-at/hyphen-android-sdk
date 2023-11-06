# hyphen-android-sdk

Hyphen aims for Firebase Auth for Web3: non-custodial mobile wallet SDK utilizing user's mobile devices as cold-wallet.

## Quick Start
### Step 1. Get your API keys

Your API requests are authenticated using API keys. Any request that doesn't include an API key will return an error.

### Step 2. Install the SDK using Gradle

We recommend Maven (Gradle) to install our SDK.

```kotlin
implementation("at.hyphen:android-sdk-core:1.0.0-alpha06")
implementation("at.hyphen:android-sdk-authenticate:1.0.0-alpha06")
implementation("at.hyphen:android-sdk-networking:1.0.0-alpha06")
implementation("at.hyphen:android-sdk-ui:1.0.0-alpha06")
implementation("at.hyphen:android-sdk-flow:1.0.0-alpha06")
```

## Demo app

The [HyphenSampleApp](https://github.com/hyphen-at/hyphen-android-sdk/tree/main/sample) folder contains a sample app that allows you to test all the features of the Android SDK. Open Android Studio project and run your real device.

Your Firebase `google-services.kson` file is required to build the project. Download config file from the Firebase Console and place the file it in the folder where the app module.

## Documentation

See the [Official Hyphen Documentation](https://docs.hyphen.at/android/quick-start) for more information.
