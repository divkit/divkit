# DivKit Hot Reload

A development tool that enables real-time editing of DivKit layouts through live reload functionality.
The server creates a bidirectional communication channel between editor on host machine and DivKit clients,
allowing instant preview of layout changes without rebuilding or restarting the application.

![](./hot_reload.gif)

## Quick Start
- Ensure debug panel is enabled for under `Div2Context`:
```kotlin
Div2Context(
    baseContext = activity,
    configuration = DivConfiguration.Builder(myImageLoader)
        .enablePermanentDebugPanel(true)
        .build()
)
```

- Open your application at screen with `Div2View` under `Div2Context` with debug panel.
- Access the debug panel (tap the error/info indicator on left top corner).
- Enable "Hot Reload" toggle in the debug interface
- Start server (Configure server address if needed, by default we're using 10.0.2.2 as host machined address on emulators)
```sh
./run.sh
```

This creates a temporary div-json file, starts the server, and opens the file in your default editor.
The server will receive the actual layout from your client application for editing.

Alternatively you can start server with:
```bash
./run.sh path/to/your/layout.json
```
This serves an existing div-json file without accepting updates from clients. Useful when you
want to update div-json that is build-in into your app but do not want to rebuild app.

## Testing
Run the test suite to verify server functionality:
```bash
python3 test.py
```
