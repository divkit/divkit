import 'package:divkit/src/core/runtime/core.dart';

const durationFunctions = <String, RuntimeFunction>{
  'getIntervalSeconds': getIntervalSeconds,
  'getIntervalTotalSeconds': getIntervalTotalSeconds,
  'getIntervalMinutes': getIntervalMinutes,
  'getIntervalTotalMinutes': getIntervalTotalMinutes,
  'getIntervalHours': getIntervalHours,
  'getIntervalTotalHours': getIntervalTotalHours,
  'getIntervalTotalDays': getIntervalTotalDays,
  'getIntervalTotalWeeks': getIntervalTotalWeeks,
};

const msInSecond = 1000;
const secondsInMinute = 60;
const msInMinute = 1000 * 60;
const minutesInHour = 60;
const msInHour = 1000 * 60 * 60;
const hoursInDay = 24;
const msInDay = 1000 * 60 * 60 * 24;
const msInWeek = 1000 * 60 * 60 * 24 * 7;

int getDuration(int milliseconds, int delimiter, [int? whole]) {
  if (milliseconds.isNegative) {
    throw 'Expecting non-negative number of milliseconds';
  }
  var val = milliseconds ~/ delimiter;
  if (whole != null) {
    val = val % whole;
  }
  return trueInt(val);
}

int getIntervalSeconds(List args) {
  return getDuration(args[0], msInSecond, secondsInMinute);
}

int getIntervalTotalSeconds(List args) {
  return getDuration(args[0], msInSecond);
}

int getIntervalMinutes(List args) {
  return getDuration(args[0], msInMinute, minutesInHour);
}

int getIntervalTotalMinutes(List args) {
  return getDuration(args[0], msInMinute);
}

int getIntervalHours(List args) {
  return getDuration(args[0], msInHour, hoursInDay);
}

int getIntervalTotalHours(List args) {
  return getDuration(args[0], msInHour);
}

int getIntervalTotalDays(List args) {
  return getDuration(args[0], msInDay);
}

int getIntervalTotalWeeks(List args) {
  return getDuration(args[0], msInWeek);
}
