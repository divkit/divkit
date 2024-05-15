// Generated code. Do not modify.

enum DivImageScale {
  fill('fill'),
  noScale('no_scale'),
  fit('fit'),
  stretch('stretch');

  final String value;

  const DivImageScale(this.value);

  T map<T>({
    required T Function() fill,
    required T Function() noScale,
    required T Function() fit,
    required T Function() stretch,
  }) {
    switch (this) {
      case DivImageScale.fill:
        return fill();
      case DivImageScale.noScale:
        return noScale();
      case DivImageScale.fit:
        return fit();
      case DivImageScale.stretch:
        return stretch();
    }
  }

  T maybeMap<T>({
    T Function()? fill,
    T Function()? noScale,
    T Function()? fit,
    T Function()? stretch,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivImageScale.fill:
        return fill?.call() ?? orElse();
      case DivImageScale.noScale:
        return noScale?.call() ?? orElse();
      case DivImageScale.fit:
        return fit?.call() ?? orElse();
      case DivImageScale.stretch:
        return stretch?.call() ?? orElse();
    }
  }

  static DivImageScale? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'fill':
        return DivImageScale.fill;
      case 'no_scale':
        return DivImageScale.noScale;
      case 'fit':
        return DivImageScale.fit;
      case 'stretch':
        return DivImageScale.stretch;
    }
    return null;
  }
}
