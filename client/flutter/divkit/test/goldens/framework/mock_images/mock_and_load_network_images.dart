import 'dart:async';
import 'dart:io';
import 'dart:math';
import 'dart:ui' as ui;

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:mocktail_image_network/mocktail_image_network.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';

import 'enable_http.dart';
import 'mock_images_utils.dart';

/// Loads all network image ([CachedNetworkImage] as well).
///
/// Enables HTTP locally for this operation.
///
/// You must call [cleanUpNetworkImageMocks] in the very end of the test, if you use
/// this function.
Future<void> loadAllNetworkImages(
  WidgetTester tester, {
  required AsyncCallback builder,
}) =>
    _mockAllNetworkImages(
      tester,
      builder,
      _downloadImage,
    );

/// Must be called in the very end of the test, whenever
/// this test contained calls to [loadAllNetworkImages] or [mockAllNetworkImages].
///
/// This contract is needed, because this clean up has to occur
/// before the test ends and thus cannot be done in [tearDown].
Future<void> cleanUpNetworkImageMocks(WidgetTester tester) =>
    _CleanUpRegistry.instance.cleanUp(tester);

class _CleanUpRegistry {
  static final instance = _CleanUpRegistry();

  final _testerCleanedUpData = <WidgetTester, bool>{};

  void register(WidgetTester tester) {
    _testerCleanedUpData[tester] = false;
    addTearDown(() {
      _verifyCleanedUp(tester);
    });
  }

  Future<void> cleanUp(WidgetTester tester) async {
    final cleanedUp = _testerCleanedUpData[tester];
    if (cleanedUp != true) {
      await tester.runAsync<void>(pumpEventQueue);
      await tester.binding.delayed(const Duration(days: 999));

      _testerCleanedUpData[tester] = true;
    }
  }

  void _verifyCleanedUp(WidgetTester tester) {
    final cleanedUp = _testerCleanedUpData.remove(tester);
    assert(
      cleanedUp != null,
      'verifyCleanedUp called without register, or cleanUp has failed',
    );
    if (cleanedUp != true) {
      fail('cleanUpNetworkImageMocks must be called in the end of test');
    }
  }
}

Future<void> _mockAllNetworkImages(
  WidgetTester tester,
  AsyncCallback builder,
  _ImageBuilder imageBuilder,
) async {
  _CleanUpRegistry.instance.register(tester);

  _mockPlatformChannels(tester);
  // Additionally wrap into to mock requests from initial pump
  // and be sure they don't turn into errors.
  await mockNetworkImages(() async {
    await builder();
  });

  tester.binding.imageCache.clear();
  tester.binding.imageCache.clearLiveImages();

  await _mockAllNetworkImageWidgets(tester, imageBuilder);
  await _mockAllCachedNetworkImages(tester, imageBuilder);

  await tester.pumpWidget(const SizedBox());

  await builder();
}

void _mockPlatformChannels(WidgetTester tester) {
  final tempRelativePath = './temp${Random().nextInt(10000)}';
  final dir = Directory(tempRelativePath);

  tester.binding.defaultBinaryMessenger.setMockMethodCallHandler(
    const MethodChannel('plugins.flutter.io/path_provider'),
    (methodCall) async {
      if (methodCall.method == 'getTemporaryDirectory' ||
          methodCall.method == 'getApplicationSupportDirectory') {
        return tempRelativePath;
      }
      return <dynamic, dynamic>{};
    },
  );

  _mockSqflite();

  addTearDown(() {
    if (dir.existsSync()) {
      dir.deleteSync(recursive: true);
    }
  });
}

void _mockSqflite() {
  databaseFactoryOrNull = databaseFactoryFfi;
}

typedef _ImageBuilder = Future<ui.Image> Function(
  String url,
  Map<String, String>? headers,
  int width,
  int height,
);

Future<void> _mockAllCachedNetworkImages(
  WidgetTester tester,
  _ImageBuilder imageBuilder,
) async {
  final imageElements = find.byType(Image, skipOffstage: false).evaluate();
  final containerElements =
      find.byType(DecoratedBox, skipOffstage: false).evaluate();
  final cachedNetworkImageElements =
      find.byType(CachedNetworkImage, skipOffstage: false).evaluate().toList();

  await tester.runAsync(() async {
    for (final cachedNetworkImageElement in cachedNetworkImageElements) {
      final widget = cachedNetworkImageElement.widget;
      if (widget is CachedNetworkImage) {
        ImageProvider imageProvider = CachedNetworkImageProvider(
          widget.imageUrl,
          cacheKey: widget.cacheKey,
          maxWidth: widget.maxWidthDiskCache,
          maxHeight: widget.maxHeightDiskCache,
        );
        imageProvider = ResizeImage.resizeIfNeeded(
          widget.memCacheWidth,
          widget.memCacheHeight,
          imageProvider,
        );
        final imageConfig =
            await extractImageConfig<CachedNetworkImageProvider>(
          imageProvider,
          cachedNetworkImageElement,
        );
        if (imageConfig != null) {
          final image = await imageBuilder(
            widget.imageUrl,
            widget.httpHeaders,
            imageConfig.width,
            imageConfig.height,
          );
          // There is a problem here.
          // Actually CachedNetworkImageProvider hashCode looks like this:
          // ```
          // Object.hash(cacheKey ?? url, scale, maxHeight, maxWidth)
          // ```
          // But scale is not a parameter of the widget and we cannot get it here, so
          // theoretically this could break at some point in the future.
          _mockNetworkImage(tester, imageConfig.cacheKey, image);
        }
      }
    }
    for (final imageElement in imageElements) {
      final widget = imageElement.widget;
      if (widget is Image) {
        final imageConfig =
            await extractImageConfig<CachedNetworkImageProvider>(
          widget.image,
          imageElement,
        );
        if (imageConfig != null) {
          final image = await imageBuilder(
            imageConfig.image.url,
            imageConfig.image.headers,
            imageConfig.width,
            imageConfig.height,
          );
          _mockNetworkImage(tester, imageConfig.cacheKey, image);
        }
      }
    }
    for (final containerElement in containerElements) {
      final widget = containerElement.widget as DecoratedBox;
      final decoration = widget.decoration;
      if (decoration is BoxDecoration) {
        final imageConfig =
            await extractImageConfig<CachedNetworkImageProvider>(
          decoration.image?.image,
          containerElement,
        );
        if (imageConfig != null) {
          final image = await imageBuilder(
            imageConfig.image.url,
            imageConfig.image.headers,
            imageConfig.width,
            imageConfig.height,
          );
          _mockNetworkImage(tester, imageConfig.cacheKey, image);
        }
      }
    }
  });
}

Future<void> _mockAllNetworkImageWidgets(
  WidgetTester tester,
  _ImageBuilder imageBuilder,
) async {
  final imageElements = find.byType(Image, skipOffstage: false).evaluate();
  final containerElements =
      find.byType(DecoratedBox, skipOffstage: false).evaluate();

  await tester.runAsync(() async {
    for (final imageElement in imageElements) {
      final widget = imageElement.widget;
      if (widget is Image) {
        final imageConfig = await extractImageConfig<NetworkImage>(
          widget.image,
          imageElement,
        );
        if (imageConfig != null) {
          final image = await imageBuilder(
            imageConfig.image.url,
            imageConfig.image.headers,
            imageConfig.width,
            imageConfig.height,
          );
          _mockNetworkImage(tester, imageConfig.cacheKey, image);
        }
      }
    }
    for (final containerElement in containerElements) {
      final widget = containerElement.widget as DecoratedBox;
      final decoration = widget.decoration;
      if (decoration is BoxDecoration) {
        final imageConfig = await extractImageConfig<NetworkImage>(
          decoration.image?.image,
          containerElement,
        );
        if (imageConfig != null) {
          final image = await imageBuilder(
            imageConfig.image.url,
            imageConfig.image.headers,
            imageConfig.width,
            imageConfig.height,
          );
          _mockNetworkImage(tester, imageConfig.cacheKey, image);
        }
      }
    }
  });
}

final Map<int, Completer<Uint8List>> _downloadCache = {};

Future<ui.Image> _downloadImage(
  String url,
  Map<String, String>? headers,
  int width,
  int height,
) async {
  final int cacheKey = Object.hash(url, width, height);
  final cached = _downloadCache[cacheKey];
  if (cached != null) {
    final bytes8List = await cached.future;
    return _decodeImageFromListWithSize(bytes8List, width, height);
  }
  final completer = Completer<Uint8List>();
  _downloadCache[cacheKey] = completer;

  return enableHttpInTests(
    () async {
      final httpClient = http.Client();
      final req = http.Request('GET', Uri.parse(url));
      if (headers != null) {
        req.headers.addAll(headers);
      }
      final httpResponse = await httpClient.send(req);
      final response = await http.Response.fromStream(httpResponse);
      final bytes8List = Uint8List.fromList(response.bodyBytes);
      completer.complete(bytes8List);
      return _decodeImageFromListWithSize(bytes8List, width, height);
    },
  );
}

/// See also [decodeImageFromList].
Future<ui.Image> _decodeImageFromListWithSize(
  Uint8List bytes,
  int cacheWidth,
  int cacheHeight,
) async {
  final buffer = await ui.ImmutableBuffer.fromUint8List(bytes);

  final codec = await PaintingBinding.instance.instantiateImageCodecFromBuffer(
    buffer,
    cacheWidth: cacheWidth,
    cacheHeight: cacheHeight,
  );
  final ui.FrameInfo frameInfo = await codec.getNextFrame();
  return frameInfo.image;
}

void _mockNetworkImage(
  WidgetTester tester,
  Object key,
  ui.Image image,
) =>
    tester.binding.imageCache.putIfAbsent(
      key,
      () => OneFrameImageStreamCompleter(
        Future.value(ImageInfo(image: image)),
      ),
    );
