import 'package:flutter_test/flutter_test.dart';
import 'package:golden_toolkit/golden_toolkit.dart';
import 'package:path/path.dart' as path;

import 'enhanced_file_comparator.dart';

/// Enhanced matcher function for Golden tests.
///
/// Can be used for basic golden tests CI setup.
///
/// Runs [screenMatchesGolden] from golden_toolkit framework
/// with [EnhancedLocalFileComparator].
///
/// See their docs for more details.
Future<void> enhancedScreenMatchesGolden(
  WidgetTester tester,
  String name, {
  bool? autoHeight,
  Finder? finder,
  CustomPump? customPump,
}) =>
    _enhancedScreenMatchesGolden(
      tester,
      name,
      autoHeight: autoHeight,
      finder: finder,
      customPump: customPump,
    );

Future<void> _enhancedScreenMatchesGolden(
  WidgetTester tester,
  String name, {
  bool? autoHeight,
  Finder? finder,
  CustomPump? customPump,
}) {
  goldenFileComparator = EnhancedLocalFileComparator(
    path.join(
      (goldenFileComparator as LocalFileComparator).basedir.toString(),
      name,
    ),
  );
  return screenMatchesGolden(
    tester,
    name,
    autoHeight: autoHeight,
    finder: finder,
    customPump: customPump,
  );
}
