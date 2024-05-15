// Generated code. Do not modify.

enum DivVideoScale {
  fill('fill'),
  noScale('no_scale'),
  fit('fit');

  final String value;

  const DivVideoScale(this.value);

  T map<T>({
    required T Function() fill,
    required T Function() noScale,
    required T Function() fit,
  }) {
    switch (this) {
      case DivVideoScale.fill:
        return fill();
      case DivVideoScale.noScale:
        return noScale();
      case DivVideoScale.fit:
        return fit();
    }
  }

  T maybeMap<T>({
    T Function()? fill,
    T Function()? noScale,
    T Function()? fit,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivVideoScale.fill:
        return fill?.call() ?? orElse();
      case DivVideoScale.noScale:
        return noScale?.call() ?? orElse();
      case DivVideoScale.fit:
        return fit?.call() ?? orElse();
    }
  }

  static DivVideoScale? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'fill':
        return DivVideoScale.fill;
      case 'no_scale':
        return DivVideoScale.noScale;
      case 'fit':
        return DivVideoScale.fit;
    }
    return null;
  }
}
