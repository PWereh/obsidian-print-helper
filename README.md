# Obsidian Print Helper

A simple Android utility to bridge the gap between Obsidian's mobile "Share" functionality and the Android Print service.

## The Problem

The Obsidian mobile app on Android does not have a built-in "Print" function. The standard way to get content out is to use the "Share" menu. This app registers itself as a target for shared plain text, converts the incoming Markdown to HTML, and sends it directly to the Android Print service for printing or saving as a PDF.

## How to Build from Source

This project is a standard Android application and can be built using Android Studio.

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/obsidian-print-helper.git](https://github.com/YOUR_USERNAME/obsidian-print-helper.git)
    cd obsidian-print-helper
    ```

2.  **Open in Android Studio:**
    * Launch Android Studio.
    * Select **File > Open** and choose the cloned `obsidian-print-helper` directory.

3.  **Build the APK:**
    * Wait for Android Studio to sync the project with Gradle.
    * Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
    * The compiled `app-debug.apk` will be located in `app/build/outputs/apk/debug/`.

## Automated CI/CD

This repository is configured with GitHub Actions to automatically build the `app-debug.apk` on every push to the `main` branch. You can download the latest compiled APK from the "Actions" tab of the repository under the "Artifacts" section of the latest successful workflow run.

## How to Use

1.  Install the `app-debug.apk` on your Android device.
2.  Open any note in your mobile Obsidian vault.
3.  Tap the three-dot menu and select "Share".
4.  In the Android Share Sheet, find and select "Obsidian Print Helper".
5.  The Android Print dialog will appear, allowing you to print the document or save it as a PDF.
