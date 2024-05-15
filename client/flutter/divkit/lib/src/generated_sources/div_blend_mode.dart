// Generated code. Do not modify.

enum DivBlendMode {
  sourceIn('source_in'),
  sourceAtop('source_atop'),
  darken('darken'),
  lighten('lighten'),
  multiply('multiply'),
  screen('screen');

  final String value;

  const DivBlendMode(this.value);

  T map<T>({
    required T Function() sourceIn,
    required T Function() sourceAtop,
    required T Function() darken,
    required T Function() lighten,
    required T Function() multiply,
    required T Function() screen,
  }) {
    switch (this) {
      case DivBlendMode.sourceIn:
        return sourceIn();
      case DivBlendMode.sourceAtop:
        return sourceAtop();
      case DivBlendMode.darken:
        return darken();
      case DivBlendMode.lighten:
        return lighten();
      case DivBlendMode.multiply:
        return multiply();
      case DivBlendMode.screen:
        return screen();
    }
  }

  T maybeMap<T>({
    T Function()? sourceIn,
    T Function()? sourceAtop,
    T Function()? darken,
    T Function()? lighten,
    T Function()? multiply,
    T Function()? screen,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivBlendMode.sourceIn:
        return sourceIn?.call() ?? orElse();
      case DivBlendMode.sourceAtop:
        return sourceAtop?.call() ?? orElse();
      case DivBlendMode.darken:
        return darken?.call() ?? orElse();
      case DivBlendMode.lighten:
        return lighten?.call() ?? orElse();
      case DivBlendMode.multiply:
        return multiply?.call() ?? orElse();
      case DivBlendMode.screen:
        return screen?.call() ?? orElse();
    }
  }

  static DivBlendMode? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'source_in':
        return DivBlendMode.sourceIn;
      case 'source_atop':
        return DivBlendMode.sourceAtop;
      case 'darken':
        return DivBlendMode.darken;
      case 'lighten':
        return DivBlendMode.lighten;
      case 'multiply':
        return DivBlendMode.multiply;
      case 'screen':
        return DivBlendMode.screen;
    }
    return null;
  }
}
