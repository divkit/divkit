// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivVisibility implements Preloadable {
  visible('visible'),
  invisible('invisible'),
  gone('gone');

  final String value;

  const DivVisibility(this.value);

  T map<T>({
    required T Function() visible,
    required T Function() invisible,
    required T Function() gone,
  }) {
    switch (this) {
      case DivVisibility.visible:
        return visible();
      case DivVisibility.invisible:
        return invisible();
      case DivVisibility.gone:
        return gone();
    }
  }

  T maybeMap<T>({
    T Function()? visible,
    T Function()? invisible,
    T Function()? gone,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivVisibility.visible:
        return visible?.call() ?? orElse();
      case DivVisibility.invisible:
        return invisible?.call() ?? orElse();
      case DivVisibility.gone:
        return gone?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivVisibility? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'visible':
          return DivVisibility.visible;
        case 'invisible':
          return DivVisibility.invisible;
        case 'gone':
          return DivVisibility.gone;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivVisibility?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'visible':
          return DivVisibility.visible;
        case 'invisible':
          return DivVisibility.invisible;
        case 'gone':
          return DivVisibility.gone;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
