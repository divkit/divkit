// Generated code. Do not modify.

enum DivContentAlignmentVertical {
  top('top'),
  center('center'),
  bottom('bottom'),
  baseline('baseline'),
  spaceBetween('space-between'),
  spaceAround('space-around'),
  spaceEvenly('space-evenly');

  final String value;

  const DivContentAlignmentVertical(this.value);

  T map<T>({
    required T Function() top,
    required T Function() center,
    required T Function() bottom,
    required T Function() baseline,
    required T Function() spaceBetween,
    required T Function() spaceAround,
    required T Function() spaceEvenly,
  }) {
    switch (this) {
      case DivContentAlignmentVertical.top:
        return top();
      case DivContentAlignmentVertical.center:
        return center();
      case DivContentAlignmentVertical.bottom:
        return bottom();
      case DivContentAlignmentVertical.baseline:
        return baseline();
      case DivContentAlignmentVertical.spaceBetween:
        return spaceBetween();
      case DivContentAlignmentVertical.spaceAround:
        return spaceAround();
      case DivContentAlignmentVertical.spaceEvenly:
        return spaceEvenly();
    }
  }

  T maybeMap<T>({
    T Function()? top,
    T Function()? center,
    T Function()? bottom,
    T Function()? baseline,
    T Function()? spaceBetween,
    T Function()? spaceAround,
    T Function()? spaceEvenly,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivContentAlignmentVertical.top:
        return top?.call() ?? orElse();
      case DivContentAlignmentVertical.center:
        return center?.call() ?? orElse();
      case DivContentAlignmentVertical.bottom:
        return bottom?.call() ?? orElse();
      case DivContentAlignmentVertical.baseline:
        return baseline?.call() ?? orElse();
      case DivContentAlignmentVertical.spaceBetween:
        return spaceBetween?.call() ?? orElse();
      case DivContentAlignmentVertical.spaceAround:
        return spaceAround?.call() ?? orElse();
      case DivContentAlignmentVertical.spaceEvenly:
        return spaceEvenly?.call() ?? orElse();
    }
  }

  static DivContentAlignmentVertical? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'top':
        return DivContentAlignmentVertical.top;
      case 'center':
        return DivContentAlignmentVertical.center;
      case 'bottom':
        return DivContentAlignmentVertical.bottom;
      case 'baseline':
        return DivContentAlignmentVertical.baseline;
      case 'space-between':
        return DivContentAlignmentVertical.spaceBetween;
      case 'space-around':
        return DivContentAlignmentVertical.spaceAround;
      case 'space-evenly':
        return DivContentAlignmentVertical.spaceEvenly;
    }
    return null;
  }
}
