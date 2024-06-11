import 'dart:convert';
import 'dart:ui' show Color;

import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/expression/expression.dart';

export 'dart:ui' show Color;

export 'package:divkit/src/core/expression/expression.dart';

List<T>? safeListMap<T>(dynamic list, T Function(dynamic) mapper) {
  if (list == null) {
    return null;
  }

  final result = <T>[];
  if (list is List<dynamic>) {
    for (final l in list) {
      try {
        result.add(mapper(l));
      } catch (e) {
        logger.warning(e);
      }
    }
  }

  return result;
}

dynamic Function(dynamic source) safeParseNamed(String? name) {
  switch (name) {
    case 'string':
      return safeParseStr;
    case 'boolean':
      return safeParseBool;
    case 'integer':
      return safeParseInt;
    case 'color':
      return safeParseColor;
    case 'number':
      return safeParseDouble;
    case 'url':
      return safeParseUri;
    case 'array':
      return safeParseList;
    case 'dict':
      return safeParseMap;
    case 'error':
      return safeParseStr;
    default:
      return safeParseObj;
  }
}

dynamic safeParseTyped(Type type) {
  switch (type) {
    case String:
      return safeParseStr;
    case bool:
      return safeParseBool;
    case int:
      return safeParseInt;
    case double:
      return safeParseDouble;
    case Color:
      return safeParseColor;
    case Uri:
      return safeParseUri;
    case List:
      return safeParseList;
    case Map<String, dynamic>:
      return safeParseMap;
    default:
      return safeParseObj;
  }
}

T? safeParseStrEnum<T>(
  String? source, {
  T? Function(String? source)? parse,
  T? fallback,
}) =>
    parse?.call(source) ?? fallback;

Expression<T>? safeParseStrEnumExpr<T>(
  String? source, {
  T? Function(String? source)? parse,
  T? fallback,
}) {
  final value = parse?.call(source);
  if (value != null) {
    return ValueExpression(value);
  }

  if (source?.contains("@{") ?? false) {
    return ResolvableExpression(
      source,
      parse: (obj) => parse?.call(obj.toString()),
      fallback: fallback,
    );
  }

  return fallback != null ? ValueExpression(fallback) : null;
}

Map<String, dynamic>? safeParseMap(
  Object? source, {
  Map<String, dynamic>? fallback,
}) {
  if (source is Map<String, dynamic>) {
    return source;
  }

  if (source is String) {
    final map = jsonDecode(source);
    if (map is Map<String, dynamic>) {
      return map;
    }
  }

  return fallback;
}

Expression<Map<String, dynamic>>? safeParseMapExpr(
  Object? source, {
  Object? fallback,
}) {
  if (source != null) {
    final map = safeParseMap(source);
    if (map is Map<String, dynamic>) {
      return ValueExpression(map);
    }
  } else {
    final map = safeParseMap(fallback);
    if (map is Map<String, dynamic>) {
      return ValueExpression(map);
    }
  }

  return null;
}

List? safeParseList(
  Object? source, {
  List? fallback,
}) {
  if (source is List) {
    return source;
  }

  if (source is String) {
    final list = jsonDecode(source);
    if (list is List) {
      return list;
    }
  }

  return fallback;
}

Expression<List>? safeParseListExpr(
  Object? source, {
  Object? fallback,
}) {
  if (source != null) {
    final list = safeParseList(source);
    if (list is List) {
      return ValueExpression(list);
    }
  } else {
    if (fallback != null) {
      final list = safeParseList(fallback);
      if (list is List) {
        return ValueExpression(list);
      }
    }
  }

  return null;
}

T? safeParseObj<T>(
  T? source, {
  T? fallback,
}) =>
    source ?? fallback;

Expression<T>? safeParseObjExpr<T>(
  T? source, {
  T? fallback,
}) {
  if (source != null) {
    return ValueExpression(source);
  } else {
    if (fallback != null) {
      return ValueExpression(fallback);
    }
  }

  return null;
}

String? safeParseStr(
  Object? source, {
  String? fallback,
}) =>
    source?.toString() ?? fallback;

Expression<String>? safeParseStrExpr(
  String? source, {
  String? fallback,
}) {
  if (source is String && source.contains("@{")) {
    return ResolvableExpression(
      source,
      parse: safeParseStr,
      fallback: fallback,
    );
  }

  final value = source;
  if (value != null) {
    return ValueExpression(value);
  }

  return fallback != null ? ValueExpression(fallback) : null;
}

Color? safeParseColor(Object? source) {
  if (source is Color) {
    return source;
  }

  if (source is String) {
    final colorValue = source.substring(1).toUpperCase();
    String? colorArgbHex;
    if (colorValue.length == 3) {
      final buf = StringBuffer('FF');
      for (int i = 0; i < colorValue.length; ++i) {
        buf.write(colorValue[i]);
        buf.write(colorValue[i]);
      }
      colorArgbHex = buf.toString();
    } else if (colorValue.length == 4) {
      final buf = StringBuffer();
      for (int i = 0; i < colorValue.length; ++i) {
        buf.write(colorValue[i]);
        buf.write(colorValue[i]);
      }
      colorArgbHex = buf.toString();
    } else if (colorValue.length == 6) {
      colorArgbHex = 'FF$colorValue';
    } else if (colorValue.length == 8) {
      colorArgbHex = colorValue;
    } else {
      return null;
    }

    final color = int.tryParse('0x$colorArgbHex');
    if (color != null) {
      return Color(color);
    }
  }

  return null;
}

Expression<Color>? safeParseColorExpr(
  Object? source, {
  Color? fallback,
}) {
  if (source is String && source.contains("@{")) {
    return ResolvableExpression(
      source,
      parse: safeParseColor,
      fallback: fallback,
    );
  }

  final value = safeParseColor(source);
  if (value != null) {
    return ValueExpression(value);
  }

  return fallback != null ? ValueExpression(fallback) : null;
}

int? safeParseInt(Object? source) {
  if (source is int) {
    return source;
  }
  if (source is double) {
    return source.round();
  }
  if (source is String) {
    return int.tryParse(source);
  }
  return null;
}

Expression<int>? safeParseIntExpr(
  Object? source, {
  int? fallback,
}) {
  if (source is String && source.contains("@{")) {
    return ResolvableExpression(
      source,
      parse: safeParseInt,
      fallback: fallback,
    );
  }

  final value = safeParseInt(source);
  if (value != null) {
    return ValueExpression(value);
  }

  return fallback != null ? ValueExpression(fallback) : null;
}

double? safeParseDouble(Object? source) {
  if (source is double) {
    return source;
  }
  if (source is int) {
    return source.toDouble();
  }
  if (source is String) {
    return double.tryParse(source);
  }
  return null;
}

Expression<double>? safeParseDoubleExpr(
  Object? source, {
  double? fallback,
}) {
  if (source is String && source.contains("@{")) {
    return ResolvableExpression(
      source,
      parse: safeParseDouble,
      fallback: fallback,
    );
  }

  final value = safeParseDouble(source);
  if (value != null) {
    return ValueExpression(value);
  }

  return fallback != null ? ValueExpression(fallback) : null;
}

Uri? safeParseUri(Object? source) {
  if (source is Uri) {
    return source;
  }

  if (source is String) {
    // The implementation of the parser replaces '+' with a ' ',
    // which is incorrect. Needs to replace the symbol in advance.
    return Uri.tryParse(source.replaceAll('+', '%2B'));
  }

  return null;
}

Expression<Uri>? safeParseUriExpr(
  Object? source, {
  Uri? fallback,
}) {
  if (source is String && source.contains("@{")) {
    return ResolvableExpression(
      source,
      parse: safeParseUri,
      fallback: fallback,
    );
  }

  final value = safeParseUri(source);
  if (value != null) {
    return ValueExpression(value);
  }

  return fallback != null ? ValueExpression(fallback) : null;
}

bool? safeParseBool(Object? source) {
  if (source is bool) {
    return source;
  }

  if (source is int) {
    switch (source) {
      case 1:
        return true;
      case 0:
        return false;
    }
  }

  if (source is double) {
    if (source == 1.0) {
      return true;
    } else if (source == 0.0) {
      return false;
    }
  }

  if (source is String) {
    switch (source.toLowerCase()) {
      case 'true':
        return true;
      case 'false':
        return false;
    }

    var value = int.tryParse(source);
    if (value != null) {
      switch (value) {
        case 1:
          return true;
        case 0:
          return false;
      }
    }
  }

  return null;
}

Expression<bool>? safeParseBoolExpr(
  Object? source, {
  bool? fallback,
}) {
  if (source is String && source.contains("@{")) {
    return ResolvableExpression(
      source,
      parse: safeParseBool,
      fallback: fallback,
    );
  }

  final value = safeParseBool(source);
  if (value != null) {
    return ValueExpression(value);
  }

  return fallback != null ? ValueExpression(fallback) : null;
}
