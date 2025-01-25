// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Specifies container's end as scroll destination.
class EndDestination extends Resolvable with EquatableMixin {
  const EndDestination();

  static const type = "end";

  @override
  List<Object?> get props => [];

  EndDestination? copyWith() => this;

  static EndDestination? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    return const EndDestination();
  }

  @override
  EndDestination resolve(DivVariableContext context) => this;
}
