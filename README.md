# Android PDF Viewer Implementation Guide

This guide explains how to implement a PDF viewer in your Android application using Jetpack Compose and the eka-android-pdf library.

## Prerequisites
- Android Studio Arctic Fox or newer
- Minimum SDK version 21 or higher
- Kotlin version 1.8.0 or higher
- Jetpack Compose dependencies

## Setup

### 1. Add JitPack Repository
Add the JitPack repository to your project's `settings.gradle` file:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add Dependency
Add the PDF viewer dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.eka-care:eka-android-pdf:0.0.1'
}
```

## Implementation

### 1. Create MainActivity
Create your `MainActivity.kt` file and implement the basic structure:

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var pdfReaderManager: PdfReaderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pdfReaderManager = PdfReaderManager(this)

        setContent {
            ComposePdfReaderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PdfReaderScreen(
                        modifier = Modifier.padding(innerPadding),
                        pdfReaderManager = pdfReaderManager
                    )
                }
            }
        }
    }
}
```

### 2. Implement PDF Reader Screen
Create a composable function for the PDF reader screen:

```kotlin
@Composable
fun PdfReaderScreen(
    modifier: Modifier = Modifier,
    pdfReaderManager: PdfReaderManager
) {
    var pdfSource by remember { mutableStateOf<PdfSource?>(null) }
    var isImage by remember { mutableStateOf(false) }

    // Activity launchers for file picking
    val pickPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            pdfSource = PdfSource.Uri(it)
            isImage = false
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            pdfSource = PdfSource.Uri(it)
            isImage = true
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // UI buttons and viewer implementation
        // ... (rest of the UI code)
    }
}
```

## Usage

### Loading PDFs
The viewer supports multiple ways to load PDFs:

1. **From Local URI:**
```kotlin
Button(onClick = {
    pickPdfLauncher.launch(arrayOf("application/pdf"))
}) {
    Text("Load PDF from URI")
}
```

2. **From URL:**
```kotlin
Button(onClick = {
    pdfSource = PdfSource.Url("https://example.com/sample.pdf")
    isImage = false
}) {
    Text("Load PDF from URL")
}
```

### Loading Images
The viewer also supports image viewing:

1. **From Local URI:**
```kotlin
Button(onClick = {
    pickImageLauncher.launch(arrayOf("image/*"))
}) {
    Text("Load Image from URI")
}
```

2. **From URL:**
```kotlin
Button(onClick = {
    pdfSource = PdfSource.Url("https://example.com/image.jpg")
    isImage = true
}) {
    Text("Load Image from URL")
}
```

## Permissions
Make sure to add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## Notes
- The viewer automatically handles both PDF and image files
- Use `isImage` flag to specify whether the source is an image or PDF
- The viewer supports edge-to-edge display for a more immersive experience
- Make sure to handle potential errors when loading files from URLs

## Troubleshooting

### Common Issues
1. If files don't load, check if you've granted the necessary permissions
2. For URL loading, ensure proper internet connectivity
3. Verify that the file URIs are accessible and have proper permissions

### Error Handling
Implement proper error handling for both local and remote file loading:
- Check for null URIs
- Handle network errors for URL loading
- Verify file types before loading

