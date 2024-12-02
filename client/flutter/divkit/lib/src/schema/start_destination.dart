// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Specifies the start of the container as the scrolling end position.
class StartDestination extends Resolvable with EquatableMixin {
  const StartDestination();

  static const type = "start";

  @override
  List<Object?> get props => [];

  StartDestination? copyWith() => this;

  static StartDestination? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    return const StartDestination();
  }

  @override
  StartDestination resolve(DivVariableContext context) => this;
}
