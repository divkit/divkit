// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';

enum DivVideoScale implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivVideoScale? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'fill':
          return DivVideoScale.fill;
        case 'no_scale':
          return DivVideoScale.noScale;
        case 'fit':
          return DivVideoScale.fit;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivVideoScale?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'fill':
          return DivVideoScale.fill;
        case 'no_scale':
          return DivVideoScale.noScale;
        case 'fit':
          return DivVideoScale.fit;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
