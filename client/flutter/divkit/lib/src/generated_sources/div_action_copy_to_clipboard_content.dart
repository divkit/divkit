// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'content_text.dart';
import 'content_url.dart';

class DivActionCopyToClipboardContent with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(ContentText) contentText,
    required T Function(ContentUrl) contentUrl,
  }) {
    switch (_index!) {
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
        "Type ${value.runtimeType.toString()} is not generalized in DivActionCopyToClipboardContent");
  }

  T maybeMap<T>({
    T Function(ContentText)? contentText,
    T Function(ContentUrl)? contentUrl,
    required T Function() orElse,
  }) {
    switch (_index!) {
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

  static DivActionCopyToClipboardContent? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case ContentText.type:
        return DivActionCopyToClipboardContent.contentText(
            ContentText.fromJson(json)!);
      case ContentUrl.type:
        return DivActionCopyToClipboardContent.contentUrl(
            ContentUrl.fromJson(json)!);
    }
    return null;
  }
}
