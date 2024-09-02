// Generated code. Do not modify.

import 'package:divkit/src/schema/content_text.dart';
import 'package:divkit/src/schema/content_url.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivActionCopyToClipboardContent extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(ContentText) contentText,
    required T Function(ContentUrl) contentUrl,
  }) {
    switch (_index) {
      case 0:
        return contentText(
          value as ContentText,
        );
      case 1:
        return contentUrl(
          value as ContentUrl,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivActionCopyToClipboardContent",
    );
  }

  T maybeMap<T>({
    T Function(ContentText)? contentText,
    T Function(ContentUrl)? contentUrl,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (contentText != null) {
          return contentText(
            value as ContentText,
          );
        }
        break;
      case 1:
        if (contentUrl != null) {
          return contentUrl(
            value as ContentUrl,
          );
        }
        break;
    }
    return orElse();
  }

  const DivActionCopyToClipboardContent.contentText(
    ContentText obj,
  )   : value = obj,
        _index = 0;

  const DivActionCopyToClipboardContent.contentUrl(
    ContentUrl obj,
  )   : value = obj,
        _index = 1;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivActionCopyToClipboardContent? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case ContentText.type:
          return DivActionCopyToClipboardContent.contentText(
            ContentText.fromJson(json)!,
          );
        case ContentUrl.type:
          return DivActionCopyToClipboardContent.contentUrl(
            ContentUrl.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionCopyToClipboardContent?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case ContentText.type:
          return DivActionCopyToClipboardContent.contentText(
            (await ContentText.parse(json))!,
          );
        case ContentUrl.type:
          return DivActionCopyToClipboardContent.contentUrl(
            (await ContentUrl.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
