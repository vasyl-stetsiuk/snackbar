# Snackbar

[![Maven Central](https://img.shields.io/maven-central/v/dev.stetsiuk/compose-snackbar.svg)](https://central.sonatype.com/artifact/dev.stetsiuk/compose-snackbar)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

A flexible and customizable snackbar library for Compose Multiplatform with support for **Android**, **iOS**, **Desktop (JVM)**, **Web (JS)**, and **WebAssembly**.

<img src="media/preview.gif" width="300" alt="SnackBar Preview"/>

## Platforms

| Platform | Supported | Minimum Version |
|----------|-----------|----------------|
| Android  | ✅        | API 24         |
| iOS      | ✅        | iOS 15.0       |
| Desktop  | ✅        | JVM 11         |
| Web JS   | ✅        | -              |
| Wasm     | ✅        | -              |


## Features

- **Multiplatform Support**: Works seamlessly across Android, iOS, Desktop, Web (JS), and WebAssembly
- **Swipe-to-Dismiss**: Built-in gesture support with `AnchoredDraggableState`
- **Stack Visualization**: Beautiful stacking effect with geometric progression for scale and alpha
- **Customizable Animations**: Configure enter/exit transitions and stack parameters
- **Material Design 3**: Follows Material Design principles with full customization options
- **Flexible API**: Easy integration with simple and advanced use cases

## Installation

Add the dependency to your `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("dev.stetsiuk:compose-snackbar:1.0.0")
        }
    }
}
```

## Quick Start

### 1. Basic Setup

Wrap your app content with `ProvideSnackBarHost`:

```kotlin
@Composable
fun App() {
    val snackbarState = rememberSnackBarHostState()

    ProvideSnackBarHost(state = snackbarState) {
        MaterialTheme {
            // Your app content
            YourScreen()
        }
    }
}
```

### 2. Show a Snackbar

Access the `SnackBarHostState` and show a snackbar:

```kotlin
@Composable
fun YourScreen() {
    val snackbarState = LocalSnackBarHostState.current

    Button(onClick = {
        val state = SnackBarState()
        val data = SnackBarData(state) {
            BasicSnackBar(
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Request timeout", modifier = Modifier.padding(16.dp))
            }
        }
        snackbarState.show(data)
    }) {
        Text("Show Snackbar")
    }
}
```

### 3. Custom Snackbar with Swipe-to-Dismiss

Create a custom snackbar using `BasicDraggableSnackBar`:

```kotlin
fun SnackBarHostState.show(text: String) {
    val state = SnackBarState()
    val data = SnackBarData(state) {
        BasicDraggableSnackBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Black,
            onDismissed = { state.hide() }
        ) {
            Row(
                modifier = Modifier.padding(16.dp, 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = text,
                    color = Color.White
                )
                IconButton(onClick = { state.hide() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
    show(data)
}
```

## Advanced Configuration

### Stack Parameters

Customize the stacking effect with `SnackBarStackParams`:

```kotlin
ProvideSnackBarHost(
    state = snackbarState,
    stackParams = SnackBarStackParams(
        scaleRatio = 0.95f,
        alphaRatio = 0.85f,
        offsetStep = (-8).dp,
        maxVisibleItems = 3
    )
) {
    // Your content
}
```

### Custom Animations

Configure enter/exit transitions:

```kotlin
ProvideSnackBarHost(
    state = snackbarState,
    enter = fadeIn() + scaleIn(initialScale = 0.9f),
    exit = fadeOut() + shrinkOut()
) {
    // Your content
}
```

### Content Alignment

Position snackbars anywhere on the screen:

```kotlin
ProvideSnackBarHost(
    state = snackbarState,
    contentAlignment = Alignment.TopCenter
) {
    // Your content
}
```

### Duration Control

Set custom display duration:

```kotlin
val data = SnackBarData(
    state = SnackBarState(),
    duration = SnackBarData.Duration.Long  // Short (2s), Long (3.5s), or Custom
) {
    // Your snackbar content
}
snackbarState.show(data)
```

## API Reference

### Core Components

#### `ProvideSnackBarHost`
Main composable that manages snackbar lifecycle and rendering.

**Parameters:**
- `modifier: Modifier` - Modifier for the host container
- `state: SnackBarHostState` - State holder for snackbar management
- `contentPadding: PaddingValues` - Padding around snackbar area
- `contentAlignment: Alignment` - Position of snackbars on screen
- `enter: EnterTransition` - Animation for showing snackbars
- `exit: ExitTransition` - Animation for hiding snackbars
- `stackParams: SnackBarStackParams` - Configuration for stack visualization
- `content: @Composable () -> Unit` - Your app content

#### `BasicDraggableSnackBar`
A snackbar with built-in swipe-to-dismiss functionality.

**Parameters:**
- `color: Color` - Background color
- `modifier: Modifier` - Modifier for customization
- `shape: Shape` - Shape of the snackbar (default: RoundedCornerShape(16.dp))
- `contentColor: Color` - Color for content (default: White)
- `border: BorderStroke?` - Optional border
- `onDismissed: () -> Unit` - Callback when dismissed via swipe
- `content: @Composable () -> Unit` - Snackbar content

#### `BasicSnackBar`
A simple snackbar without gesture handling.

**Parameters:**
- `color: Color` - Background color
- `modifier: Modifier` - Modifier for customization
- `shape: Shape` - Shape of the snackbar
- `contentColor: Color` - Color for content
- `border: BorderStroke?` - Optional border
- `content: @Composable () -> Unit` - Snackbar content

### State Management

#### `SnackBarHostState`
State holder for managing snackbars.

**Methods:**
- `show(data: SnackBarData)` - Show a snackbar
- `hide(id: String)` - Hide a specific snackbar by ID

#### `SnackBarState`
Individual snackbar state.

**Methods:**
- `show()` - Show this snackbar
- `hide()` - Hide this snackbar

### Data Classes

#### `SnackBarData`
Represents a single snackbar.

**Parameters:**
- `state: SnackBarState` - State for this snackbar
- `id: String` - Unique identifier (auto-generated)
- `duration: Duration` - Display duration
- `content: @Composable () -> Unit` - Snackbar content

#### `SnackBarStackParams`
Configuration for stack visualization.

**Parameters:**
- `scaleRatio: Float` - Base ratio for geometric scale progression (default: 0.95)
- `alphaRatio: Float` - Base ratio for geometric alpha progression (default: 0.85)
- `offsetStep: Dp` - Vertical offset between items (default: -8.dp)
- `maxVisibleItems: Int` - Maximum visible snackbars (default: 3)

## Sample App

Check out the [composeApp](./composeApp) module for a complete working example with different customization options.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

```
Copyright 2025 Vasyl Stetsiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Author

**Vasyl Stetsiuk**
- GitHub: [@vasyl-stetsiuk](https://github.com/vasyl-stetsiuk)
- Email: stecyuk.vasil@gmail.com

## Acknowledgments

Built with [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) by JetBrains.
