import 'package:divkit/divkit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'src/app.dart';

void main() {
  // DivKit log output
  logger
    ..keepLog = kDebugMode
    ..onLog = print;

  runApp(const ProviderScope(child: PlaygroundApp()));
}
