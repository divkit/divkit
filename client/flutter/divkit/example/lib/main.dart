import 'package:divkit/divkit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'src/app.dart';

void main() {
  // DivKit log output
  logger
    ..keepLog = kDebugMode
    ..onLog = print;

  debugPrintDivKitViewLifecycle = true;
  debugPrintDivExpressionResolve = true;
  debugPrintDivPerformLayout = false;

  runApp(const ProviderScope(child: PlaygroundApp()));
}
