// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_copy_to_clipboard_content.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Copies data to the clipboard.
class DivActionCopyToClipboard with EquatableMixin {
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
        content: reqProp<DivActionCopyToClipboardContent>(
          safeParseObject(
            json['content'],
            parse: DivActionCopyToClipboardContent.fromJson,
          ),
          name: 'content',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
