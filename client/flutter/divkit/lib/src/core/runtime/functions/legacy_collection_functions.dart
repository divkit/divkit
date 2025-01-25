import 'package:divkit/src/core/runtime/core.dart';

const legacyCollectionFunctions = <String, RuntimeFunction>{
  'getArrayString': getArrayString,
  'getArrayOptString': getArrayOptString,
  'getArrayInteger': getArrayInteger,
  'getArrayOptInteger': getArrayOptInteger,
  'getArrayNumber': getArrayNumber,
  'getArrayOptNumber': getArrayOptNumber,
  'getArrayBoolean': getArrayBoolean,
  'getArrayOptBoolean': getArrayOptBoolean,
  'getArrayColor': getArrayColor,
  'getArrayOptColor': getArrayOptColor,
  'getArrayUrl': getArrayUrl,
  'getArrayOptUrl': getArrayOptUrl,
  'getDictString': getDictString,
  'getDictOptString': getDictOptString,
  'getDictNumber': getDictNumber,
  'getDictOptNumber': getDictOptNumber,
  'getDictInteger': getDictInteger,
  'getDictOptInteger': getDictOptInteger,
  'getDictBoolean': getDictBoolean,
  'getDictOptBoolean': getDictOptBoolean,
  'getDictColor': getDictColor,
  'getDictOptColor': getDictOptColor,
  'getDictUrl': getDictUrl,
  'getDictOptUrl': getDictOptUrl,
};

String getArrayString(List args) {
  return cast<String>(cast<List>(args[0])[trueInt(args[1])]);
}

String getArrayOptString(List args) {
  return guard(
    () => cast<String>(cast<List>(args[0])[trueInt(args[1])]),
    cast<String>(args[2]),
  );
}

int getArrayInteger(List args) {
  return trueInt(cast<List>(args[0])[trueInt(args[1])]);
}

int getArrayOptInteger(List args) {
  return guard(
    () => trueInt(cast<List>(args[0])[trueInt(args[1])]),
    trueInt(args[2]),
  );
}

double getArrayNumber(List args) {
  return trueNum(cast<List>(args[0])[trueInt(args[1])]);
}

double getArrayOptNumber(List args) {
  return guard(
    () => trueNum(cast<List>(args[0])[trueInt(args[1])]),
    trueNum(args[2]),
  );
}

bool getArrayBoolean(List args) {
  return cast<bool>(cast<List>(args[0])[trueInt(args[1])]);
}

bool getArrayOptBoolean(List args) {
  return guard(
    () => cast<bool>(cast<List>(args[0])[trueInt(args[1])]),
    cast<bool>(args[2]),
  );
}

Color getArrayColor(List args) {
  return parseColor(cast<List>(args[0])[trueInt(args[1])]);
}

Color getArrayOptColor(List args) {
  return guard(
    () => parseColor(cast<List>(args[0])[trueInt(args[1])]),
    parseColor(args[2]),
  );
}

Uri getArrayUrl(List args) {
  return parseUrl(cast<List>(args[0])[trueInt(args[1])]);
}

Uri getArrayOptUrl(List args) {
  return guard(
    () => parseUrl(cast<List>(args[0])[trueInt(args[1])]),
    parseUrl(args[2]),
  );
}

String getDictString(List args) {
  return cast<String>(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

String getDictOptString(List args) {
  if (args.length < 2) {
    throw 'At least 2 argument(s) expected';
  }
  return guard(
    () => cast<String>(getMapProp(cast<Map>(args[1]), args.skip(2))),
    cast<String>(args[0]),
  );
}

double getDictNumber(List args) {
  return trueNum(getMapProp(args[0], args.skip(1)));
}

double getDictOptNumber(List args) {
  return guard(
    () => trueNum(getMapProp(cast<Map>(args[1]), args.skip(2))),
    trueNum(args[0]),
  );
}

int getDictInteger(List args) {
  return trueInt(getMapProp(args[0], args.skip(1)));
}

int getDictOptInteger(List args) {
  return guard(
    () => trueInt(getMapProp(cast<Map>(args[1]), args.skip(2))),
    trueInt(args[0]),
  );
}

bool getDictBoolean(List args) {
  return cast<bool>(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

bool getDictOptBoolean(List args) {
  return guard(
    () => cast<bool>(getMapProp(cast<Map>(args[1]), args.skip(2))),
    cast<bool>(args[0]),
  );
}

Color getDictColor(List args) {
  return parseColor(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

Color getDictOptColor(List args) {
  return guard(
    () => parseColor(getMapProp(cast<Map>(args[1]), args.skip(2))),
    parseColor(args[0]),
  );
}

Uri getDictUrl(List args) {
  return parseUrl(getMapProp(cast<Map>(args[0]), args.skip(1)));
}

Uri getDictOptUrl(List args) {
  return guard(
    () => parseUrl(getMapProp(cast<Map>(args[1]), args.skip(2))),
    parseUrl(args[0]),
  );
}
