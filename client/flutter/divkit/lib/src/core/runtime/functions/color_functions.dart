import 'package:divkit/src/core/runtime/core.dart';

const colorFunctions = <String, RuntimeFunction>{
  'argb': argb,
  'rgb': rgb,
  'getColorAlpha': getColorAlpha,
  'getColorRed': getColorRed,
  'getColorGreen': getColorGreen,
  'getColorBlue': getColorBlue,
  'setColorAlpha': setColorAlpha,
  'setColorRed': setColorRed,
  'setColorGreen': setColorGreen,
  'setColorBlue': setColorBlue,
};

Color argb(List args) {
  final a = toNumber(args[0]);
  final r = toNumber(args[1]);
  final g = toNumber(args[2]);
  final b = toNumber(args[3]);

  if (a < 0 || a > 1 || r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) {
    throw 'Value out of range 0..1';
  }

  return Color.fromARGB(
    (a * 255).round(),
    (r * 255).round(),
    (g * 255).round(),
    (b * 255).round(),
  );
}

Color rgb(List args) {
  final r = toNumber(args[0]);
  final g = toNumber(args[1]);
  final b = toNumber(args[2]);

  if (r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) {
    throw 'Value out of range 0..1';
  }

  return Color.fromARGB(
    255,
    (r * 255).round(),
    (g * 255).round(),
    (b * 255).round(),
  );
}

double getColorAlpha(List args) {
  return parseColor(args[0]).alpha / 255;
}

double getColorRed(List args) {
  return parseColor(args[0]).red / 255;
}

double getColorGreen(List args) {
  return parseColor(args[0]).green / 255;
}

double getColorBlue(List args) {
  return parseColor(args[0]).blue / 255;
}

Color setColorAlpha(List args) {
  if (args[1] < 0 || args[1] > 1) {
    throw 'Value out of range 0..1';
  }
  return parseColor(args[0]).setAlpha((args[1] * 255).round());
}

Color setColorRed(List args) {
  if (args[1] < 0 || args[1] > 1) {
    throw 'Value out of range 0..1';
  }
  return parseColor(args[0]).setRed((args[1] * 255).round());
}

Color setColorGreen(List args) {
  if (args[1] < 0 || args[1] > 1) {
    throw 'Value out of range 0..1';
  }
  return parseColor(args[0]).setGreen((args[1] * 255).round());
}

Color setColorBlue(List args) {
  if (args[1] < 0 || args[1] > 1) {
    throw 'Value out of range 0..1';
  }
  return parseColor(args[0]).setBlue((args[1] * 255).round());
}
