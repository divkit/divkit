// Generated code. Do not modify.

enum DivTransitionTrigger {
  dataChange('data_change'),
  stateChange('state_change'),
  visibilityChange('visibility_change');

  final String value;

  const DivTransitionTrigger(this.value);

  T map<T>({
    required T Function() dataChange,
    required T Function() stateChange,
    required T Function() visibilityChange,
  }) {
    switch (this) {
      case DivTransitionTrigger.dataChange:
        return dataChange();
      case DivTransitionTrigger.stateChange:
        return stateChange();
      case DivTransitionTrigger.visibilityChange:
        return visibilityChange();
    }
  }

  T maybeMap<T>({
    T Function()? dataChange,
    T Function()? stateChange,
    T Function()? visibilityChange,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivTransitionTrigger.dataChange:
        return dataChange?.call() ?? orElse();
      case DivTransitionTrigger.stateChange:
        return stateChange?.call() ?? orElse();
      case DivTransitionTrigger.visibilityChange:
        return visibilityChange?.call() ?? orElse();
    }
  }

  static DivTransitionTrigger? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'data_change':
        return DivTransitionTrigger.dataChange;
      case 'state_change':
        return DivTransitionTrigger.stateChange;
      case 'visibility_change':
        return DivTransitionTrigger.visibilityChange;
    }
    return null;
  }
}
