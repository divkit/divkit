// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Removes focus from an element.
class DivActionClearFocus extends Resolvable with EquatableMixin {
  const DivActionClearFocus();

  static const type = "clear_focus";

  @override
  List<Object?> get props => [];

  DivActionClearFocus? copyWith() => this;

  static DivActionClearFocus? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    return const DivActionClearFocus();
  }

  @override
  DivActionClearFocus resolve(DivVariableContext context) => this;
}
