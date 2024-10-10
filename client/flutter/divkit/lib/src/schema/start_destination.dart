// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Specifies container's start as scroll destination.
class StartDestination extends Preloadable with EquatableMixin {
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

  static Future<StartDestination?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    return const StartDestination();
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {}
}
