// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Infinite number of repetitions.
class DivInfinityCount extends Resolvable with EquatableMixin {
  const DivInfinityCount();

  static const type = "infinity";

  @override
  List<Object?> get props => [];

  DivInfinityCount? copyWith() => this;

  static DivInfinityCount? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    return const DivInfinityCount();
  }

  @override
  DivInfinityCount resolve(DivVariableContext context) => this;
}
