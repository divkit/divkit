// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivTransitionSelector implements Preloadable {
  none('none'),
  dataChange('data_change'),
  stateChange('state_change'),
  anyChange('any_change');

  final String value;

  const DivTransitionSelector(this.value);

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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivTransitionSelector?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}
