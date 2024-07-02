// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivVideoSource with EquatableMixin {
  const DivVideoSource({
    this.bitrate,
    required this.mimeType,
    this.resolution,
    required this.url,
  });

  static const type = "video_source";

  final Expression<int>? bitrate;

  final Expression<String> mimeType;

  final DivVideoSourceResolution? resolution;

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

  static DivVideoSource? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}

class DivVideoSourceResolution with EquatableMixin {
  const DivVideoSourceResolution({
    required this.height,
    required this.width,
  });

  static const type = "resolution";
  // constraint: number > 0
  final Expression<int> height;
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

  static DivVideoSourceResolution? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivVideoSourceResolution(
      height: safeParseIntExpr(
        json['height'],
      )!,
      width: safeParseIntExpr(
        json['width'],
      )!,
    );
  }
}
