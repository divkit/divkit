import 'package:divkit/src/core/runtime/core.dart';

const datetimeFunctions = <String, RuntimeFunction>{
  'parseUnixTime': parseUnixTime,
  'nowLocal': nowLocal,
  'addMillis': addMillis,
  'setYear': setYear,
  'setMonth': setMonth,
  'setDay': setDay,
  'setHours': setHours,
  'setMinutes': setMinutes,
  'setSeconds': setSeconds,
  'setMillis': setMillis,
  'getYear': getYear,
  'getMonth': getMonth,
  'getDayOfWeek': getDayOfWeek,
  'getDay': getDay,
  'getHours': getHours,
  'getMinutes': getMinutes,
  'getSeconds': getSeconds,
  'getMillis': getMillis,
};

DateTime parseUnixTime(List args) {
  return DateTime.fromMillisecondsSinceEpoch(args[0] * 1000, isUtc: true);
}

DateTime nowLocal(List args) {
  return DateTime.now();
}

DateTime addMillis(List args) {
  return parseDate(args[0]).add(Duration(milliseconds: args[1]));
}

DateTime setYear(List args) {
  return parseDate(args[0]).makeCopyWith(year: args[1]);
}

DateTime setMonth(List args) {
  if (args[1] < 1 || args[1] > 12) {
    throw "Expecting month in [1..12], instead got ${args[1]}";
  }
  return parseDate(args[0]).makeCopyWith(month: args[1]);
}

DateTime setDay(List args) {
  final date = parseDate(args[0]);
  final maxDay = date.lastMonthDay;

  if (args[1] < 1 || args[1] > maxDay) {
    throw "Expecting day in [1..$maxDay], instead got ${args[1]}";
  }
  return date.makeCopyWith(day: args[1]);
}

DateTime setHours(List args) {
  if (args[1] < 0 || args[1] > 23) {
    throw "Expecting hours [0..23], instead got ${args[1]}";
  }
  return parseDate(args[0]).makeCopyWith(hour: args[1]);
}

DateTime setMinutes(List args) {
  if (args[1] < 0 || args[1] > 59) {
    throw "Expecting minutes [0..59], instead got ${args[1]}";
  }
  return parseDate(args[0]).makeCopyWith(minute: args[1]);
}

DateTime setSeconds(List args) {
  if (args[1] < 0 || args[1] > 59) {
    throw "Expecting seconds [0..59], instead got ${args[1]}";
  }
  return parseDate(args[0]).makeCopyWith(second: args[1]);
}

DateTime setMillis(List args) {
  if (args[1] < 0 || args[1] > 999) {
    throw "Expecting seconds [0..999], instead got ${args[1]}";
  }
  return parseDate(args[0]).makeCopyWith(millisecond: args[1]);
}

int getYear(List args) {
  return parseDate(args[0]).year;
}

int getMonth(List args) {
  return parseDate(args[0]).month;
}

int getDayOfWeek(List args) {
  return parseDate(args[0]).weekday;
}

int getDay(List args) {
  return parseDate(args[0]).day;
}

int getHours(List args) {
  return parseDate(args[0]).hour;
}

int getMinutes(List args) {
  return parseDate(args[0]).minute;
}

int getSeconds(List args) {
  return parseDate(args[0]).second;
}

int getMillis(List args) {
  return parseDate(args[0]).millisecond;
}
