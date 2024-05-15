// Generated code. Do not modify.

enum DivAnimationInterpolator {
  linear('linear'),
  ease('ease'),
  easeIn('ease_in'),
  easeOut('ease_out'),
  easeInOut('ease_in_out'),
  spring('spring');

  final String value;

  const DivAnimationInterpolator(this.value);

  T map<T>({
    required T Function() linear,
    required T Function() ease,
    required T Function() easeIn,
    required T Function() easeOut,
    required T Function() easeInOut,
    required T Function() spring,
  }) {
    switch (this) {
      case DivAnimationInterpolator.linear:
        return linear();
      case DivAnimationInterpolator.ease:
        return ease();
      case DivAnimationInterpolator.easeIn:
        return easeIn();
      case DivAnimationInterpolator.easeOut:
        return easeOut();
      case DivAnimationInterpolator.easeInOut:
        return easeInOut();
      case DivAnimationInterpolator.spring:
        return spring();
    }
  }

  T maybeMap<T>({
    T Function()? linear,
    T Function()? ease,
    T Function()? easeIn,
    T Function()? easeOut,
    T Function()? easeInOut,
    T Function()? spring,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivAnimationInterpolator.linear:
        return linear?.call() ?? orElse();
      case DivAnimationInterpolator.ease:
        return ease?.call() ?? orElse();
      case DivAnimationInterpolator.easeIn:
        return easeIn?.call() ?? orElse();
      case DivAnimationInterpolator.easeOut:
        return easeOut?.call() ?? orElse();
      case DivAnimationInterpolator.easeInOut:
        return easeInOut?.call() ?? orElse();
      case DivAnimationInterpolator.spring:
        return spring?.call() ?? orElse();
    }
  }

  static DivAnimationInterpolator? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'linear':
        return DivAnimationInterpolator.linear;
      case 'ease':
        return DivAnimationInterpolator.ease;
      case 'ease_in':
        return DivAnimationInterpolator.easeIn;
      case 'ease_out':
        return DivAnimationInterpolator.easeOut;
      case 'ease_in_out':
        return DivAnimationInterpolator.easeInOut;
      case 'spring':
        return DivAnimationInterpolator.spring;
    }
    return null;
  }
}
