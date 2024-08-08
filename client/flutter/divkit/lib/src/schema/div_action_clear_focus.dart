// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivActionClearFocus extends Preloadable with EquatableMixin {
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

  static Future<DivActionClearFocus?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    return const DivActionClearFocus();
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {}
}
