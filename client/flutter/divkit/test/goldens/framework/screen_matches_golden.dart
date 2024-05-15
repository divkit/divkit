import 'package:flutter_test/flutter_test.dart';
import 'package:golden_toolkit/golden_toolkit.dart';
import 'package:path/path.dart' as path;

import 'ci_file_comparator.dart';

/// Matcher function for Golden tests on CI.
///
/// Runs [screenMatchesGolden] from golden_toolkit framework
/// with [CIFileComparator].
///
/// See their docs for more details.
Future<void> screenMatchesGoldenForCI(
  WidgetTester tester,
  String name, {
  bool? autoHeight,
  Finder? finder,
  CustomPump? customPump,
}) =>
    _screenMatchesGoldenForCI(
      tester,
      name,
      autoHeight: autoHeight,
      finder: finder,
      customPump: customPump,
    );

Future<void> _screenMatchesGoldenForCI(
  WidgetTester tester,
  String name, {
  bool? autoHeight,
  Finder? finder,
  CustomPump? customPump,
}) {
  goldenFileComparator = CIFileComparator(
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
