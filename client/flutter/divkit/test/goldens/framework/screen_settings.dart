import 'dart:ui';

import 'framework.dart';

abstract class Devices {
  static const phone375x500 = Device(
    name: 'phone375x500',
    size: Size(
      375,
      500,
    ),
    devicePixelRatio: 2.0,
  );
}
