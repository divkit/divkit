import 'package:divkit/src/core/runtime/core.dart';

const collectionFunctions = <String, RuntimeFunction>{
  'getStringFromArray': getTFromArray<String>,
  'getOptStringFromArray': getOptTFromArray<String>,
  'getIntegerFromArray': getIntegerFromArray,
  'getOptIntegerFromArray': getOptIntegerFromArray,
  'getNumberFromArray': getNumberFromArray,
  'getOptNumberFromArray': getOptNumberFromArray,
  'getBooleanFromArray': getTFromArray<bool>,
  'getOptBooleanFromArray': getOptTFromArray<bool>,
  'getColorFromArray': getColorFromArray,
  'getOptColorFromArray': getOptColorFromArray,
  'getUrlFromArray': getUrlFromArray,
  'getOptUrlFromArray': getOptUrlFromArray,
  'getArrayFromArray': getArrayFromArray,
  'getOptArrayFromArray': getOptArrayFromArray,
  'getDictFromArray': getDictFromArray,
  'getOptDictFromArray': getOptDictFromArray,
  'getStringFromDict': getTFromDict<String>,
  'getOptStringFromDict': getOptTFromDict<String>,
  'getNumberFromDict': getNumberFromDict,
  'getOptNumberFromDict': getOptNumberFromDict,
  'getIntegerFromDict': getIntegerFromDict,
  'getOptIntegerFromDict': getOptIntegerFromDict,
  'getBooleanFromDict': getTFromDict<bool>,
  'getOptBooleanFromDict': getOptTFromDict<bool>,
  'getColorFromDict': getColorFromDict,
  'getOptColorFromDict': getOptColorFromDict,
  'getUrlFromDict': getUrlFromDict,
  'getOptUrlFromDict': getOptUrlFromDict,
  'getDictFromDict': getDictFromDict,
  'getOptDictFromDict': getOptDictFromDict,
  'getArrayFromDict': getArrayFromDict,
  'getOptArrayFromDict': getOptArrayFromDict,
};

T getTFromArray<T>(List args) {
  return cast<T>(cast<List>(args[0])[trueInt(args[1])]);
}

T getOptTFromArray<T>(List args) {
  return guard(
    () => cast<T>(cast<List>(args[0])[trueInt(args[1])]),
    cast<T>(args[2]),
  );
}

int getIntegerFromArray(List args) {
  return trueInt(cast<List>(args[0])[trueInt(args[1])]);
}

int getOptIntegerFromArray(List args) {
  return guard(
    () => trueInt(cast<List>(args[0])[trueInt(args[1])]),
    trueInt(args[2]),
  );
}

double getNumberFromArray(List args) {
  return trueNum(cast<List>(args[0])[trueInt(args[1])]);
}

double getOptNumberFromArray(List args) {
  return guard(
    () => trueNum(cast<List>(args[0])[trueInt(args[1])]),
    trueNum(args[2]),
  );
}

Color getColorFromArray(List args) {
  return parseColor(
    cast<List>(args[0])[trueInt(args[1])],
  );
}

Color getOptColorFromArray(List args) {
  return guard(
    () => parseColor(
      cast<List>(args[0])[trueInt(args[1])],
    ),
    parseColor(args[2]),
  );
}

Uri getUrlFromArray(List args) {
  return parseUrl(
    cast<List>(args[0])[trueInt(args[1])],
  );
}

Uri getOptUrlFromArray(List args) {
  return guard(
    () => parseUrl(
      cast<List>(args[0])[trueInt(args[1])],
    ),
    parseUrl(args[2]),
  );
}

List getArrayFromArray(List args) {
  return cast<List>(cast<List>(args[0])[trueInt(args[1])]);
}

List getOptArrayFromArray(List args) {
  return guard(
    () => cast<List>(cast<List>(args[0])[trueInt(args[1])]),
    const [],
  );
}

Map getDictFromArray(List args) {
  return cast<Map>(cast<List>(args[0])[trueInt(args[1])]);
}

Map getOptDictFromArray(List args) {
  return guard(
    () => cast<Map>(cast<List>(args[0])[trueInt(args[1])]),
    const {},
  );
}

T getTFromDict<T>(List args) {
  return cast<T>(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

T getOptTFromDict<T>(List args) {
  return guard(
    () => cast<T>(getMapProp(cast<Map>(args[1]), args.skip(2))),
    cast<T>(args[0]),
  );
}

int getIntegerFromDict<T>(List args) {
  return trueInt(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

int getOptIntegerFromDict<T>(List args) {
  return guard(
    () => trueInt(getMapProp(cast<Map>(args[1]), args.skip(2))),
    trueInt(args[0]),
  );
}

double getNumberFromDict<T>(List args) {
  return trueNum(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

double getOptNumberFromDict<T>(List args) {
  return guard(
    () => trueNum(getMapProp(cast<Map>(args[1]), args.skip(2))),
    trueNum(args[0]),
  );
}

Color getColorFromDict<T>(List args) {
  return parseColor(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

Color getOptColorFromDict<T>(List args) {
  return guard(
    () => parseColor(getMapProp(cast<Map>(args[1]), args.skip(2))),
    parseColor(args[0]),
  );
}

Uri getUrlFromDict<T>(List args) {
  return parseUrl(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

Uri getOptUrlFromDict<T>(List args) {
  return guard(
    () => parseUrl(getMapProp(cast<Map>(args[1]), args.skip(2))),
    parseUrl(args[0]),
  );
}

List getArrayFromDict<T>(List args) {
  return cast<List>(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

List getOptArrayFromDict<T>(List args) {
  if (args.length < 2) {
    throw 'At least 2 argument(s) expected';
  }
  return guard(
    () => cast<List>(getMapProp(cast<Map>(args[0]), args.skip(1))),
    const [],
  );
}

Map getDictFromDict<T>(List args) {
  return cast<Map>(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

Map getOptDictFromDict<T>(List args) {
  if (args.length < 2) {
    throw 'At least 2 argument(s) expected';
  }
  return guard(
    () => cast<Map>(getMapProp(cast<Map>(args[0]), args.skip(1))),
    const {},
  );
}
