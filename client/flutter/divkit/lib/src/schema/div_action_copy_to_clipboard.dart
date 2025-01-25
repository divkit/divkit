// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_copy_to_clipboard_content.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Copies data to the clipboard.
class DivActionCopyToClipboard extends Resolvable with EquatableMixin {
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

  @override
  DivActionCopyToClipboard resolve(DivVariableContext context) {
    content.resolve(context);
    return this;
  }
}
