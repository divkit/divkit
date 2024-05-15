import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

final navigatorKey =
    GlobalKey<NavigatorState>(debugLabel: "DivKit Playground App");
const demoInputVariable = 'input_variable';

final reloadNProvider = StateProvider((ref) => 0);
final nightModeProvider = StateProvider((ref) => false);
final isRTLProvider = StateProvider((ref) => false);
