import 'dart:ui';

import 'package:divkit/src/core/runtime/core.dart';

const storageFunctions = <String, RuntimeContextFunction>{
  'getIntegerValue': getIntegerValue,
  'getStoredIntegerValue': getStoredIntegerValue,
  'getNumberValue': getNumberValue,
  'getStoredNumberValue': getStoredNumberValue,
  'getBooleanValue': getBooleanValue,
  'getStoredBooleanValue': getStoredBooleanValue,
  'getStringValue': getStringValue,
  'getStoredStringValue': getStoredStringValue,
  'getColorValue': getColorValue,
  'getStoredColorValue': getStoredColorValue,
  'getUrlValue': getUrlValue,
  'getStoredUrlValue': getStoredUrlValue,
};

int getIntegerValue(
  List args,
  Map<String, dynamic> context,
) {
  return guard(() => trueInt(context[args[0]]), trueInt(args[1]));
}

int getStoredIntegerValue(
  List args,
  Map<String, dynamic> context,
) {
  return trueInt(context[args[0]] ?? args[1]);
}

double getNumberValue(
  List args,
  Map<String, dynamic> context,
) {
  return guard(() => trueNum(cast<double>(context[args[0]])), trueNum(args[1]));
}

double getStoredNumberValue(
  List args,
  Map<String, dynamic> context,
) {
  return context[args[0]] as double? ?? args[1] as double;
}

bool getBooleanValue(
  List args,
  Map<String, dynamic> context,
) {
  return guard(() => context[args[0]] as bool, args[1] as bool);
}

bool getStoredBooleanValue(
  List args,
  Map<String, dynamic> context,
) {
  return context[args[0]] as bool? ?? args[1] as bool;
}

String getStringValue(
  List args,
  Map<String, dynamic> context,
) {
  return guard(() => context[args[0]] as String, args[1] as String);
}

String getStoredStringValue(
  List args,
  Map<String, dynamic> context,
) {
  return context[args[0]] as String? ?? args[1] as String;
}

Color getColorValue(
  List args,
  Map<String, dynamic> context,
) {
  return guard(
    () => parseColor(context[args[0]]),
    parseColor(args[1]),
  );
}

Color getStoredColorValue(
  List args,
  Map<String, dynamic> context,
) {
  return parseColor(context[args[0]] ?? args[1]);
}

Uri getUrlValue(
  List args,
  Map<String, dynamic> context,
) {
  return guard(() => parseUrl(context[args[0]]), parseUrl(args[1]));
}

Uri getStoredUrlValue(
  List args,
  Map<String, dynamic> context,
) {
  return guard(() => parseUrl(context[args[0]]), parseUrl(args[1]));
}
