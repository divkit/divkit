import 'dart:convert';

import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/types.dart';

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
    default:
      return safeParseMap;
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
    case List<dynamic>:
      return safeParseList;
    case Map<String, dynamic>:
      return safeParseMap;
    default:
      return safeParseMap;
  }
}

List<T>? safeParseObjects<T>(
  Object? source,
  T Function(dynamic) mapper, {
  List<T>? fallback,
}) {
  if (source is List) {
    final result = <T>[];
    for (final l in source) {
      try {
        final res = mapper(l);
        result.add(res);
      } catch (e, st) {
        logger.warning("Error: ", error: e, stackTrace: st);
      }
    }
    return result;
  }

  return fallback;
}

Expression<List<T>>? safeParseObjectsExpr<T>(
  Object? source,
  T Function(dynamic) mapper, {
  List<T>? fallback,
}) {
  if (source is String && source.contains("@{")) {
    return ResolvableExpression(
      source,
      parse: (obj) => safeParseObjects(obj, mapper),
      fallback: fallback,
    );
  }

  final value = safeParseObjects(source, mapper);
  if (value != null) {
    return ValueExpression(value);
  }

  return fallback != null ? ValueExpression(fallback) : null;
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
  } else if (source?.contains("@{") ?? false) {
    return ResolvableExpression(
      source,
      parse: (obj) => parse?.call(obj.toString()),
      fallback: fallback,
    );
  }
  return fallback != null ? ValueExpression(fallback) : null;
}

T? safeParseObject<T>(
  Obj? source, {
  T? Function(Obj? source)? parse,
  T? fallback,
}) =>
    parse?.call(source) ?? fallback;

Expression<T>? safeParseObjectExpr<T>(
  Obj? source, {
  T? Function(Obj? source)? parse,
  T? fallback,
}) {
  final value = parse?.call(source);
  if (value != null) {
    return ValueExpression(value);
  } else {
    if (fallback != null) {
      return ValueExpression(fallback);
    }
  }
  return null;
}

Map<String, dynamic>? safeParseMap(
  Object? source, {
  Map<String, dynamic>? fallback,
}) {
  if (source is Map<String, dynamic>) {
    return source;
  } else if (source is String) {
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
  } else if (source is String) {
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

String? safeParseStr(
  Object? source, {
  String? fallback,
}) =>
    source?.toString() ?? fallback;

Expression<String>? safeParseStrExpr(
  Object? source, {
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
    return ValueExpression(value.toString());
  }

  return fallback != null ? ValueExpression(fallback) : null;
}

Color? safeParseColor(Object? source) {
  if (source is Color) {
    return source;
  } else if (source is String) {
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
  } else if (source is double) {
    return source.round();
  } else if (source is String) {
    return double.tryParse(source)?.toInt();
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
  } else if (source is int) {
    return source.toDouble();
  } else if (source is String) {
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
  } else if (source is String) {
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
  } else if (source is int) {
    switch (source) {
      case 1:
        return true;
      case 0:
        return false;
    }
  } else if (source is double) {
    if (source == 1.0) {
      return true;
    } else if (source == 0.0) {
      return false;
    }
  } else if (source is String) {
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
