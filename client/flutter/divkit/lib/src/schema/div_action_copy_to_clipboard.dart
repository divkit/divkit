// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_copy_to_clipboard_content.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivActionCopyToClipboard extends Preloadable with EquatableMixin {
  const DivActionCopyToClipboard({
    required this.content,
  });

  static const type = "copy_to_clipboard";

  final DivActionCopyToClipboardContent content;

  @override
  List<Object?> get props => [
        content,
      ];

  DivActionCopyToClipboard copyWith({
    DivActionCopyToClipboardContent? content,
  }) =>
      DivActionCopyToClipboard(
        content: content ?? this.content,
      );

  static DivActionCopyToClipboard? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionCopyToClipboard(
        content: safeParseObj(
          DivActionCopyToClipboardContent.fromJson(json['content']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionCopyToClipboard?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionCopyToClipboard(
        content: (await safeParseObjAsync(
          DivActionCopyToClipboardContent.fromJson(json['content']),
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
      await content.preload(context);
    } catch (e) {
      return;
    }
  }
}
