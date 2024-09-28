import 'package:divkit/src/core/runtime/core.dart';

const formatFunctions = <String, RuntimeFunction>{
  'formatDateAsUTC': formatDateAsUTC,
  'formatDateAsUTCWithLocale': formatDateAsUTCWithLocale,
  'contains': contains,
  'substring': substring,
  'replaceAll': replaceAll,
  'index': index,
  'lastIndex': lastIndex,
  'toLowerCase': toLowerCase,
  'toUpperCase': toUpperCase,
  'encodeUri': encodeUri,
  'decodeUri': decodeUri,
  'trim': trim,
  'trimLeft': trimLeft,
  'trimRight': trimRight,
  'padStart': padStart,
  'padEnd': padEnd,
  'len': len,
  'testRegex': testRegex,
};

String _calcPad(String val, int len, String pad) {
  if (pad.isEmpty) {
    return '';
  }

  String part = '';
  while (part.length + val.length < len) {
    part += pad;
  }
  if (part.isNotEmpty && part.length + val.length > len) {
    part = part.substring(0, trueInt(len - val.length).toInt());
  }

  return part;
}

String _padStart(Object val, int len, String pad) {
  final str = val.toString();
  final prefix = _calcPad(str, len, pad);

  return prefix + str;
}

String _padEnd(Object val, int len, String pad) {
  final str = val.toString();
  final suffix = _calcPad(str, len, pad);

  return str + suffix;
}

bool _testRegex(String str, String regex) {
  try {
    return RegExp(regex).hasMatch(str);
  } catch (e) {
    throw 'Invalid regular expression';
  }
}

String formatDateAsUTC(List args) {
  // To implement needs rewrite java code to dart:
  // https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/text/SimpleDateFormat.java
  throw 'Not implemented `formatDateAsUTC`';
}

String formatDateAsUTCWithLocale(List args) {
  // To implement needs rewrite java code to dart:
  // https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/text/SimpleDateFormat.java
  throw 'Not implemented `formatDateAsUTCWithLocale`';
}

bool contains(List args) {
  return cast<String>(args[0]).contains(cast<String>(args[1]));
}

String substring(List args) {
  final start = cast<int>(args[1]);
  final end = cast<int>(args[2]);

  if (start > end) {
    throw 'Indexes should be in ascending order';
  }
  return cast<String>(args[0]).substring(start, end);
}

String replaceAll(List args) {
  final base = cast<String>(args[0]);
  final from = cast<String>(args[1]);
  if (from == '') {
    return base;
  }
  return base.replaceAll(
    from,
    cast<String>(args[2]),
  );
}

int index(List args) {
  return cast<String>(args[0]).indexOf(cast<String>(args[1]));
}

int lastIndex(List args) {
  return cast<String>(args[0]).lastIndexOf(cast<String>(args[1]));
}

String toLowerCase(List args) {
  return cast<String>(args[0]).toLowerCase();
}

String toUpperCase(List args) {
  return cast<String>(args[0]).toUpperCase();
}

String encodeUri(List args) {
  return Uri.encodeComponent(parseUrl(args[0]).toString())
      .replaceAll('%25', '%');
}

String decodeUri(List args) {
  return Uri.decodeComponent(parseUrl(args[0]).toString());
}

String trim(List args) {
  return cast<String>(args[0]).trim();
}

String trimLeft(List args) {
  return cast<String>(args[0]).trimLeft();
}

String trimRight(List args) {
  return cast<String>(args[0]).trimRight();
}

String padStart(List args) {
  return _padStart(args[0], trueInt(args[1]), cast<String>(args[2]));
}

String padEnd(List args) {
  return _padEnd(args[0], trueInt(args[1]), cast<String>(args[2]));
}

int len(List args) {
  return args[0].length;
}

bool testRegex(List args) {
  return _testRegex(cast<String>(args[0]), cast<String>(args[1]));
}
