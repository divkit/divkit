import 'dart:ui';

import 'package:divkit/src/core/runtime/core.dart' as core;

const objectFunctions = <String, core.RuntimeFunction>{
  'toBoolean': toBoolean,
  'toNumber': toNumber,
  'toInteger': toInteger,
  'toString': toString,
  'toUrl': toUrl,
  'toColor': toColor,
};

bool toBoolean(List args) {
  if (args.length == 1) {
    return core.toBoolean(args[0]);
  }
  throw 'Function has no matching overload for given argument';
}

double toNumber(List args) {
  if (args[0] is double || args.length > 1) {
    throw 'Function has no matching overload for given argument';
  }
  return core.toNumber(args[0]);
}

int toInteger(List args) {
  if (args.length > 1) {
    throw 'Function has no matching overload for given argument';
  }
  return core.toInteger(args[0]);
}

String toString(List args) {
  if (args.length != 1) {
    throw 'Only 1 argument expected';
  }
  return core.toString(args[0]);
}

Color toColor(List args) {
  return core.parseColor(args[0]);
}

Uri toUrl(List args) {
  return core.parseUrl(args[0]);
}
