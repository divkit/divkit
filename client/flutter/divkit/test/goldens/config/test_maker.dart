import 'package:flutter/material.dart';

import '../framework/framework.dart';
import '../framework/screen_settings.dart';

void makeGoldenTest({
  required ValueGetter<Widget> builder,
  required String description,
  required String fileName,
  CustomPump? customPump,
}) {
  testGoldens(description, (tester) async {
    await tester.pumpAutoSizeWidgetBuilder(
      builder: builder,
      afterBuild: () => enhancedScreenMatchesGolden(
        tester,
        fileName,
        customPump: customPump,
      ),
      device: Devices.phone375x500,
      wrapper: (child) => MaterialApp(
        supportedLocales: const [Locale('en')],
        debugShowCheckedModeBanner: false,
        home: child,
      ),
    );
  });
}
