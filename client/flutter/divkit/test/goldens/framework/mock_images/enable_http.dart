import 'dart:io';

/// Enables network calls in tests with the [body].
///
/// Will not work if [body] itself creates other overrides.
R enableHttpInTests<R>(R Function() body) => HttpOverrides.runWithHttpOverrides(
      body,
      _MyHttpOverrides(),
    );

class _MyHttpOverrides extends HttpOverrides {}
