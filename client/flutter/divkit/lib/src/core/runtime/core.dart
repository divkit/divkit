import 'dart:convert';
import 'dart:ui';

import 'package:divkit/src/core/runtime/compatible/integer.dart';

export 'package:divkit/src/core/types.dart';

typedef RuntimeFunction = Object Function(List);
typedef RuntimeContextFunction = Object Function(List, Map<String, dynamic>);

bool equals(Object left, Object right) {
  if (left is bool && right is bool) {
    return left == right;
  } else if (left is String && right is String) {
    return left.compareTo(right) == 0;
  } else if (left is Color && right is Color) {
    return left == right;
  } else if (left is Uri && right is Uri) {
    return left.toString().compareTo(right.toString()) == 0;
  } else if (left is DateTime && right is DateTime) {
    return left.compareTo(right) == 0;
  }
  return compare(left, right, '==') == 0;
}

String toString(Object object) {
  if (object is Map || object is List) {
    return jsonEncode(object);
  } else if (object is double) {
    var res = object.toString();
    if (!res.contains('.')) {
      if (res.contains('e')) {
        res = res.replaceAll('e', '.0E');
        res = res.replaceAll('E+', 'E');
      } else {
        res += '.0';
      }
    }
    return res;
  } else if (object is Color) {
    return object.formattedString();
  } else if (object is DateTime) {
    return object.toString().substring(0, 19);
  }
  return object.toString();
}

/// Mapping of DivKit types to Dart types.
const typeToName = {
  "Map": 'Dict',
  "Map<dynamic, dynamic>": 'Dict',
  "_Map<String, dynamic>": 'Dict',
  "List": 'Array',
  "List<dynamic>": 'Array',
  "Uri": 'Url',
  "bool": 'Boolean',
  "int": 'Integer',
  "_BigIntImpl": 'Integer',
  "double": 'Number',
};

@pragma('vm:prefer-inline')
String typeName(String type) => typeToName[type] ?? type.toString();

/// More controlled type conversion.
T cast<T>(Object? obj, [Type? resType]) {
  if (obj is T) {
    return obj;
  } else {
    return throw 'Incorrect value type: expected ${typeName((resType ?? T).toString())}, got ${typeName((obj.runtimeType).toString())}';
  }
}

/// Safe Date parsing.
DateTime parseDate(Object source) {
  if (source is DateTime) {
    return source;
  } else if (source is String) {
    return DateTime.parse(source).makeCopyWith(isUtc: true);
  }
  throw 'Unable to convert value to DateTime';
}

extension DateTimeExt on DateTime {
  int get lastMonthDay {
    // Move to the next month
    var tester = DateTime(year, month + 1);

    // Move back to the last day of the previous month
    tester = DateTime(tester.year, tester.month, 0);

    return tester.day;
  }

  // ToDo: `copyWith` appeared since version 2.19!
  /// Creates a new [DateTime] from this one by updating individual properties.
  DateTime makeCopyWith({
    int? year,
    int? month,
    int? day,
    int? hour,
    int? minute,
    int? second,
    int? millisecond,
    int? microsecond,
    bool? isUtc,
  }) {
    return ((isUtc ?? this.isUtc) ? DateTime.utc : DateTime.new)(
      year ?? this.year,
      month ?? this.month,
      day ?? this.day,
      hour ?? this.hour,
      minute ?? this.minute,
      second ?? this.second,
      millisecond ?? this.millisecond,
      microsecond ?? this.microsecond,
    );
  }
}

/// Checking for the correctness of an Number.
double trueNum(Object src) {
  if (src is double && src == double.infinity ||
      src == double.negativeInfinity) {
    throw 'Unable to convert value to Number';
  } else if (src is num) {
    return src.toDouble();
  }
  return cast<double>(src);
}

/// Rounding the math error of double.
double nearlyNum(double value, [double epsilon = 0.00000000000001]) {
  return trueNum((value / epsilon).round() * epsilon);
}

/// Checking for Integer overflow.
bool isIntOverflow(BigInt it) {
  return it > BigInt.from(maxIntegerValue) || it < BigInt.from(minIntegerValue);
}

/// Checking for the correctness of an Integer.
int trueInt(Object src) {
  if (src is int) {
    return src;
  } else if (src is double) {
    throw 'Cannot convert value to Integer';
  } else if (src is BigInt) {
    if (isIntOverflow(src)) {
      throw 'Integer overflow';
    }
    return src.toInt();
  }
  return cast<int>(src);
}

num divOp(left, right) {
  if (right == 0) {
    throw 'Division by zero is not supported';
  }
  if (left is int && right is int) {
    return trueInt(BigInt.from(left) ~/ BigInt.from(right));
  } else if (left is num && right is num) {
    return nearlyNum(left.toDouble() / right.toDouble());
  }
  throw "Operator '/' cannot be applied";
}

num modOp(left, right) {
  if (right == 0) {
    throw 'Division by zero is not supported';
  }
  if (left is int && right is int) {
    return trueInt(
      BigInt.from(left) % BigInt.from(right) * BigInt.from(left.sign),
    );
  } else if (left is num && right is num) {
    return nearlyNum(left.toDouble() % right.toDouble() * left.toDouble().sign);
  }
  throw "Operator '%' cannot be applied";
}

num mulOp(left, right) {
  if (left is int && right is int) {
    return trueInt(BigInt.from(left) * BigInt.from(right));
  } else if (left is num && right is num) {
    return nearlyNum(left.toDouble() * right.toDouble());
  }
  throw "Operator '*' cannot be applied";
}

num subOp(left, right) {
  if (left is int && right is int) {
    return trueInt(BigInt.from(left) - BigInt.from(right));
  } else if (left is num && right is num) {
    return nearlyNum(left.toDouble() - right.toDouble());
  }
  throw "Operator '-' cannot be applied";
}

Object sumOp(left, right) {
  if (left is int && right is int) {
    return trueInt(BigInt.from(left) + BigInt.from(right));
  } else if (left is num && right is num) {
    return nearlyNum(left.toDouble() + right.toDouble());
  } else if (left is String && right is String) {
    return left + right;
  }
  throw "Operator '+' cannot be applied";
}

int compare(left, right, [String op = '?!']) {
  if (diffTypes(left, right)) {
    throw "Operator '$op' cannot be applied to different types: ${typeName((left.runtimeType).toString())} and ${typeName((right.runtimeType).toString())}";
  } else if (left is DateTime && right is DateTime) {
    return left.compareTo(right);
  } else if (left is num && right is num) {
    return left.compareTo(right);
  }
  throw "Operator '$op' cannot be applied to ${typeName((left.runtimeType).toString())} type";
}

bool diffTypes(left, right) {
  if ((left is double && right is double) ||
      (left is int && right is double) ||
      (left is double && right is int)) {
    return false;
  }
  return left.runtimeType != right.runtimeType;
}

Object getMapProp(Map src, Iterable keys) {
  final key = cast<String>(keys.first);
  if (keys.length == 1) {
    if (src.containsKey(key)) {
      return src[key];
    }
    throw 'Missing property "$key" in the dict';
  } else if (src.containsKey(key)) {
    return getMapProp(cast<Map>(src[key]), keys.skip(1));
  }
  throw 'Missing property "$key" in the dict';
}

/// Explicit processing of places where a custom error value is required.
T expectResult<T>({
  T? res,
  required Object onThrow,
}) {
  if (res != null) {
    return res;
  }
  throw onThrow;
}

bool toBoolean(Object src) {
  if (src == 0 || src == 'false') {
    return false;
  }
  if (src == 1 || src == 'true') {
    return true;
  }
  throw 'Unable to convert value to Boolean';
}

double toNumber(Object src) {
  if (src is num) {
    return trueNum(src.toDouble());
  } else if (src is String) {
    return trueNum(
      double.tryParse(src) ?? (throw 'Unable to convert value to Number'),
    );
  }

  throw 'Unable to convert value to Number';
}

int toInteger(Object src) {
  if (src is String) {
    src = int.tryParse(src) ?? cast<int>(src);
  } else if (src is bool) {
    if (src) {
      return 1;
    }
    return 0;
  }

  final it = src is BigInt
      ? src
      : src is num
          ? BigInt.from(src)
          : cast<BigInt>(src, int);

  if (isIntOverflow(it)) {
    throw 'Unable to convert value to Integer';
  }

  return it.toInt();
}

double copySignNum(double it, double sign) {
  if ((sign.isNegative && it > 0.0) || (!sign.isNegative && it < 0.0)) {
    return -it;
  }
  return it;
}

BigInt copySignInt(int it, int sign) {
  if ((sign > 0 && it.isNegative) || (sign.isNegative && it > 0)) {
    return -BigInt.from(it);
  }
  return BigInt.from(it);
}

/// Safe Url parsing.
Uri parseUrl(Object source) {
  if (source is Uri) {
    return source;
  } else if (source is String) {
    // The implementation of the parser replaces '+' with a ' ',
    // which is incorrect. Needs to replace the symbol in advance.
    return Uri.tryParse(source.replaceAll('+', '%2B')) ?? cast<Uri>(source);
  }

  return cast<Uri>(source);
}

/// Safe Color parsing.
Color parseColor(Object source) {
  if (source is String) {
    final colorValue = source.substring(1).toUpperCase();
    String? colorArgbHex;
    if (!source.startsWith('#')) {
      throw 'Unable to convert value to Color, expected format #AARRGGBB';
    } else {
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
        throw 'Unable to convert value to Color, expected format #AARRGGBB';
      }

      final value = int.tryParse(colorArgbHex, radix: 16);
      if (value != null) {
        return Color(value);
      }
    }
  }

  return cast<Color>(source);
}

extension ColorExt on Color {
  /// DivKit color string format.
  String formattedString() => '#${alpha.toRadixString(16).padLeft(2, '0')}'
          '${red.toRadixString(16).padLeft(2, '0')}'
          '${green.toRadixString(16).padLeft(2, '0')}'
          '${blue.toRadixString(16).padLeft(2, '0')}'
      .toUpperCase();

  Color setAlpha(int a) {
    return Color.fromARGB(a, red, green, blue);
  }

  Color setRed(int r) {
    return Color.fromARGB(alpha, r, green, blue);
  }

  Color setGreen(int g) {
    return Color.fromARGB(alpha, red, g, blue);
  }

  Color setBlue(int b) {
    return Color.fromARGB(alpha, red, green, b);
  }
}
