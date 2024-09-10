// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivVideoSource extends Preloadable with EquatableMixin {
  const DivVideoSource({
    this.bitrate,
    required this.mimeType,
    this.resolution,
    required this.url,
  });

  static const type = "video_source";

  /// Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
  final Expression<int>? bitrate;

  /// MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
  final Expression<String> mimeType;

  /// Media file resolution.
  final DivVideoSourceResolution? resolution;

  /// Link to the media file available for playback or download.
  final Expression<Uri> url;

  @override
  List<Object?> get props => [
        bitrate,
        mimeType,
        resolution,
        url,
      ];

  DivVideoSource copyWith({
    Expression<int>? Function()? bitrate,
    Expression<String>? mimeType,
    DivVideoSourceResolution? Function()? resolution,
    Expression<Uri>? url,
  }) =>
      DivVideoSource(
        bitrate: bitrate != null ? bitrate.call() : this.bitrate,
        mimeType: mimeType ?? this.mimeType,
        resolution: resolution != null ? resolution.call() : this.resolution,
        url: url ?? this.url,
      );

  static DivVideoSource? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivVideoSource(
        bitrate: safeParseIntExpr(
          json['bitrate'],
        ),
        mimeType: safeParseStrExpr(
          json['mime_type']?.toString(),
        )!,
        resolution: safeParseObj(
          DivVideoSourceResolution.fromJson(json['resolution']),
        ),
        url: safeParseUriExpr(json['url'])!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivVideoSource?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivVideoSource(
        bitrate: await safeParseIntExprAsync(
          json['bitrate'],
        ),
        mimeType: (await safeParseStrExprAsync(
          json['mime_type']?.toString(),
        ))!,
        resolution: await safeParseObjAsync(
          DivVideoSourceResolution.fromJson(json['resolution']),
        ),
        url: (await safeParseUriExprAsync(json['url']))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await bitrate?.preload(context);
      await mimeType.preload(context);
      await resolution?.preload(context);
      await url.preload(context);
    } catch (e) {
      return;
    }
  }
}

/// Media file resolution.
class DivVideoSourceResolution extends Preloadable with EquatableMixin {
  const DivVideoSourceResolution({
    required this.height,
    required this.width,
  });

  static const type = "resolution";

  /// Media file frame height.
  // constraint: number > 0
  final Expression<int> height;

  /// Media file frame width.
  // constraint: number > 0
  final Expression<int> width;

  @override
  List<Object?> get props => [
        height,
        width,
      ];

  DivVideoSourceResolution copyWith({
    Expression<int>? height,
    Expression<int>? width,
  }) =>
      DivVideoSourceResolution(
        height: height ?? this.height,
        width: width ?? this.width,
      );

  static DivVideoSourceResolution? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivVideoSourceResolution(
        height: safeParseIntExpr(
          json['height'],
        )!,
        width: safeParseIntExpr(
          json['width'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivVideoSourceResolution?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivVideoSourceResolution(
        height: (await safeParseIntExprAsync(
          json['height'],
        ))!,
        width: (await safeParseIntExprAsync(
          json['width'],
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await height.preload(context);
      await width.preload(context);
    } catch (e) {
      return;
    }
  }
}
