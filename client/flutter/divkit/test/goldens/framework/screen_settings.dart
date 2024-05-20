import 'dart:ui';

import 'framework.dart';

abstract class Devices {
  static const iphone3 = Device(
    name: 'iphone3',
    size: Size(
      320,
      480,
    ),
    devicePixelRatio: 2.0,
  );
  static const phone375x500 = Device(
    name: 'phone375x500',
    size: Size(
      375,
      500,
    ),
    devicePixelRatio: 2.0,
  );
}
