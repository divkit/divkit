// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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
