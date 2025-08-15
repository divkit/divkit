import 'package:divkit/divkit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'src/app.dart';
import 'src/state.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // DivKit log output
  logger
    ..keepLog = kDebugMode
    ..onLog = print;

  debugPrintDivKitViewLifecycle = true;
  debugPrintDivExpressionResolve = true;
  debugPrintDivPerformLayout = false;

  runApp(ProviderScope(
    overrides: [
      prefsProvider.overrideWithValue(await SharedPreferences.getInstance()),
    ],
    child: const PlaygroundApp(),
  ));
}
