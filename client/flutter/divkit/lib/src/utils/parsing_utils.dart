import 'dart:convert';

import 'package:divkit/divkit.dart';

export 'dart:ui' show Color;

export 'package:divkit/src/core/expression/expression.dart';
export 'package:divkit/src/core/variable/variable_context.dart';

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

Future<void> safeFuturesWait(
  dynamic list,
  Function(dynamic) waiter,
) async {
  if (list == null) {
    return;
  }

  if (list is List<dynamic>) {
    for (final l in list) {
      try {
        await waiter(l);
      } catch (e, st) {
        logger.warning("Not wait on $l", error: e, stackTrace: st);
      }
    }
  }
}

void safeListResolve(
  dynamic list,
  Function(dynamic) resolve,
) async {
  if (list == null) {
    return;
  }

  if (list is List<dynamic>) {
    for (final l in list) {
      try {
        resolve(l);
      } catch (e, st) {
        logger.warning("Not resolve $l", error: e, stackTrace: st);
      }
    }
  }
}

Future<List<T>?> safeListMapAsync<T>(
  dynamic list,
  T Function(dynamic) mapper,
) async =>
    safeListMap(
      list,
      mapper,
    );

List<T>? safeListMap<T>(
  dynamic list,
  T Function(dynamic) mapper,
) {
  if (list == null) {
    return null;
  }

  final result = <T>[];
  if (list is List<dynamic>) {
    for (final l in list) {
      try {
        result.add(mapper(l));
      } catch (e, st) {
        logger.warning("Error in ${l['type']}", error: e, stackTrace: st);
      }
    }
  }

  return result;
}

Future<T?> safeParseStrEnumAsync<T>(
  String? source, {
  T? Function(String? source)? parse,
  T? fallback,
}) async =>
    safeParseStrEnum(
      source,
      parse: parse,
      fallback: fallback,
    );

T? safeParseStrEnum<T>(
  String? source, {
  T? Function(String? source)? parse,
  T? fallback,
}) =>
    parse?.call(source) ?? fallback;

Future<Expression<T>?> safeParseStrEnumExprAsync<T>(
  String? source, {
  T? Function(String? source)? parse,
  T? fallback,
}) async =>
    safeParseStrEnumExpr(
      source,
      parse: parse,
      fallback: fallback,
    );

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

Future<Map<String, dynamic>?> safeParseMapAsync(
  Object? source, {
  Map<String, dynamic>? fallback,
}) async =>
    safeParseMap(
      source,
      fallback: fallback,
    );

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

Future<Expression<Map<String, dynamic>>?> safeParseMapExprAsync(
  Object? source, {
  Object? fallback,
}) async =>
    safeParseMapExpr(source, fallback: fallback);

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

Future<List?> safeParseListAsync(
  Object? source, {
  List? fallback,
}) async =>
    safeParseList(
      source,
      fallback: fallback,
    );

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

Future<Expression<List>?> safeParseListExprAsync(
  Object? source, {
  Object? fallback,
}) async =>
    safeParseListExpr(
      source,
      fallback: fallback,
    );

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

Future<T?> safeParseObjAsync<T>(
  T? source, {
  T? fallback,
}) async =>
    safeParseObj(
      source,
      fallback: fallback,
    );

T? safeParseObj<T>(
  T? source, {
  T? fallback,
}) =>
    source ?? fallback;

Future<Expression<T>?> safeParseObjExprAsync<T>(
  T? source, {
  T? fallback,
}) async =>
    safeParseObjExpr(
      source,
      fallback: fallback,
    );

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

Future<String?> safeParseStrAsync(
  Object? source, {
  String? fallback,
}) async =>
    safeParseStr(
      source,
      fallback: fallback,
    );

String? safeParseStr(
  Object? source, {
  String? fallback,
}) =>
    source?.toString() ?? fallback;

Future<Expression<String>?> safeParseStrExprAsync(
  String? source, {
  String? fallback,
}) async =>
    safeParseStrExpr(
      source,
      fallback: fallback,
    );

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

Future<Color?> safeParseColorAsync(
  Object? source,
) async =>
    safeParseColor(
      source,
    );

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

Future<Expression<Color>?> safeParseColorExprAsync(
  Object? source, {
  Color? fallback,
}) async =>
    safeParseColorExpr(
      source,
      fallback: fallback,
    );

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

Future<int?> safeParseIntAsync(
  Object? source,
) async =>
    safeParseInt(
      source,
    );

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

Future<Expression<int>?> safeParseIntExprAsync(
  Object? source, {
  int? fallback,
}) async =>
    safeParseIntExpr(
      source,
      fallback: fallback,
    );

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

Future<double?> safeParseDoubleAsync(
  Object? source,
) async =>
    safeParseDouble(
      source,
    );

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

Future<Expression<double>?> safeParseDoubleExprAsync(
  Object? source, {
  double? fallback,
}) async =>
    safeParseDoubleExpr(
      source,
      fallback: fallback,
    );

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

Future<Uri?> safeParseUriAsync(
  Object? source,
) async =>
    safeParseUri(
      source,
    );

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

Future<Expression<Uri>?> safeParseUriExprAsync(
  Object? source, {
  Uri? fallback,
}) async =>
    safeParseUriExpr(
      source,
      fallback: fallback,
    );

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

Future<bool?> safeParseBoolAsync(
  Object? source,
) async =>
    safeParseBool(
      source,
    );

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

Future<Expression<bool>?> safeParseBoolExprAsync(
  Object? source, {
  bool? fallback,
}) async =>
    safeParseBoolExpr(
      source,
      fallback: fallback,
    );

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
