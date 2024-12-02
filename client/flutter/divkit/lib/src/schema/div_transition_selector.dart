// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';

enum DivTransitionSelector implements Resolvable {
  none('none'),
  dataChange('data_change'),
  stateChange('state_change'),
  anyChange('any_change');

  final String value;

  const DivTransitionSelector(this.value);
  bool get isNone => this == none;

  bool get isDataChange => this == dataChange;

  bool get isStateChange => this == stateChange;

  bool get isAnyChange => this == anyChange;

  T map<T>({
    required T Function() none,
    required T Function() dataChange,
    required T Function() stateChange,
    required T Function() anyChange,
  }) {
    switch (this) {
      case DivTransitionSelector.none:
        return none();
      case DivTransitionSelector.dataChange:
        return dataChange();
      case DivTransitionSelector.stateChange:
        return stateChange();
      case DivTransitionSelector.anyChange:
        return anyChange();
    }
  }

  T maybeMap<T>({
    T Function()? none,
    T Function()? dataChange,
    T Function()? stateChange,
    T Function()? anyChange,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivTransitionSelector.none:
        return none?.call() ?? orElse();
      case DivTransitionSelector.dataChange:
        return dataChange?.call() ?? orElse();
      case DivTransitionSelector.stateChange:
        return stateChange?.call() ?? orElse();
      case DivTransitionSelector.anyChange:
        return anyChange?.call() ?? orElse();
    }
  }

  static DivTransitionSelector? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivTransitionSelector.none;
        case 'data_change':
          return DivTransitionSelector.dataChange;
        case 'state_change':
          return DivTransitionSelector.stateChange;
        case 'any_change':
          return DivTransitionSelector.anyChange;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivTransitionSelector: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivTransitionSelector resolve(DivVariableContext context) => this;
}
