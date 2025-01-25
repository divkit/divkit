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
    await loadAllNetworkImages(
      tester,
      builder: () async => tester.pumpAutoSizeWidgetBuilder(
        builder: builder,
        device: Devices.phone375x500,
        wrapper: (child) => MaterialApp(
          supportedLocales: const [Locale('en')],
          debugShowCheckedModeBanner: false,
          home: child,
        ),
      ),
    );
    await enhancedScreenMatchesGolden(
      tester,
      fileName,
      customPump: customPump,
    );
    await cleanUpNetworkImageMocks(tester);
  });
}
