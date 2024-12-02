export 'dart:ui' show Color;

typedef Obj<T> = Map<String, T>;
typedef Arr<T> = List<T>;

/// Error handling with explicit fallback.
T guard<T>(T Function() tst, T alt) {
  try {
    return tst();
  } catch (_) {
    return alt;
  }
}
