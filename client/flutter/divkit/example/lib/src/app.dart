import 'package:example/src/pages/home.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'state.dart';

class PlaygroundApp extends ConsumerWidget {
  const PlaygroundApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final isNightModeEnabled = ref.watch(nightModeProvider);
    final isRtlEnabled = ref.watch(isRTLProvider);

    return MaterialApp(
      title: "DivKit Playground App",
      navigatorKey: navigatorKey,
      debugShowCheckedModeBanner: false,
      theme: isNightModeEnabled
          ? ThemeData.dark(useMaterial3: true)
          : ThemeData.light(useMaterial3: true),
      builder: (context, page) => Directionality(
        textDirection: isRtlEnabled ? TextDirection.rtl : TextDirection.ltr,
        child: page ?? const SizedBox.shrink(),
      ),
      home: const HomePage(),
    );
  }
}
