// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_action_copy_to_clipboard_content.dart';

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

  static DivActionCopyToClipboard? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivActionCopyToClipboard(
      content: safeParseObj(
        DivActionCopyToClipboardContent.fromJson(json['content']),
      )!,
    );
  }
}
