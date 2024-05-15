// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'content_text.dart';
import 'content_url.dart';

class DivActionCopyToClipboardContent with EquatableMixin {
  const DivActionCopyToClipboardContent(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is ContentText) {
      return value;
    }
    if (value is ContentUrl) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivActionCopyToClipboardContent");
  }

  T map<T>({
    required T Function(ContentText) contentText,
    required T Function(ContentUrl) contentUrl,
  }) {
    final value = _value;
    if (value is ContentText) {
      return contentText(value);
    }
    if (value is ContentUrl) {
      return contentUrl(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivActionCopyToClipboardContent");
  }

  T maybeMap<T>({
    T Function(ContentText)? contentText,
    T Function(ContentUrl)? contentUrl,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is ContentText && contentText != null) {
      return contentText(value);
    }
    if (value is ContentUrl && contentUrl != null) {
      return contentUrl(value);
    }
    return orElse();
  }

  const DivActionCopyToClipboardContent.contentText(
    ContentText value,
  ) : _value = value;

  const DivActionCopyToClipboardContent.contentUrl(
    ContentUrl value,
  ) : _value = value;

  static DivActionCopyToClipboardContent? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case ContentText.type:
        return DivActionCopyToClipboardContent(ContentText.fromJson(json)!);
      case ContentUrl.type:
        return DivActionCopyToClipboardContent(ContentUrl.fromJson(json)!);
    }
    return null;
  }
}
