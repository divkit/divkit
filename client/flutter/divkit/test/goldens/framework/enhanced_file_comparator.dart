import 'dart:io';
import 'dart:ui';

import 'package:flutter/foundation.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:path/path.dart' as path;

/// Enhanced [LocalFileComparator].
///
/// Can be used for basic golden tests CI setup.
///
/// Differences:
///
///  1. Adds this Wiki page to error output
///     https://nda.ya.ru/t/sRTKjxUm74yiwQ
///
///  2. When adding new test (that didn't have expectations yet), instead of just failing,
///     will produce failure output, to be able to use them as new expectations.
///
class EnhancedLocalFileComparator extends LocalFileComparator {
  EnhancedLocalFileComparator(String testFile)
      : _path = _getPath(null),
        super(Uri.parse(testFile));

  /// Path context exists as an instance variable rather than just using the
  /// system path context in order to support testing, where we can spoof the
  /// platform to test behaviors with arbitrary path styles.
  final path.Context _path;

  static path.Context _getPath(path.Style? style) =>
      path.Context(style: style ?? path.Style.platform);

  static const _infoLink = 'https://nda.ya.ru/t/sRTKjxUm74yiwQ';

  @override
  Future<bool> compare(Uint8List imageBytes, Uri golden) async {
    final newTest = isNewTest(golden);

    if (newTest) {
      final testImageCodec =
          await instantiateImageCodec(Uint8List.fromList(imageBytes));
      final testImage = (await testImageCodec.getNextFrame()).image;
      final error = await generateFailureOutput(
        ComparisonResult(
          diffPercent: 100.0,
          passed: false,
          diffs: {
            'testImage': testImage,
          },
          error: 'No expectations provided. This may be a new test. '
              'If this is an expected result, you will need to update the expetations. '
              'Check $_infoLink',
        ),
        golden,
        basedir,
      );
      throw FlutterError(error);
    }

    final readImageBytes = await getGoldenBytes(golden);
    final result = await GoldenFileComparator.compareLists(
      imageBytes,
      readImageBytes,
    );

    if (!result.passed) {
      final error = await generateFailureOutput(
        ComparisonResult(
          diffPercent: result.diffPercent,
          passed: result.passed,
          diffs: result.diffs,
          error: '${result.error}\n'
              'Check $_infoLink',
        ),
        golden,
        basedir,
      );
      throw FlutterError(error);
    }
    return result.passed;
  }

  @protected
  bool isNewTest(Uri golden) {
    // Default behavior of [LocalFileComparator] is to fail here.
    // Instead we create the file to save the failures, and let fail the comparison itself.
    final goldenFile = _getGoldenFile(golden);
    return !goldenFile.existsSync();
  }

  File _getGoldenFile(Uri golden) => File(
        _path.join(
          _path.fromUri(basedir),
          _path.fromUri(golden.path),
        ),
      );
}
