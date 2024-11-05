// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivVideoSource extends Resolvable with EquatableMixin {
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

  @override
  DivVideoSource resolve(DivVariableContext context) {
    bitrate?.resolve(context);
    mimeType.resolve(context);
    resolution?.resolve(context);
    url.resolve(context);
    return this;
  }
}

/// Media file resolution.
class DivVideoSourceResolution extends Resolvable with EquatableMixin {
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

  @override
  DivVideoSourceResolution resolve(DivVariableContext context) {
    height.resolve(context);
    width.resolve(context);
    return this;
  }
}
