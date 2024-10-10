// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Specifies container's end as scroll destination.
class EndDestination extends Preloadable with EquatableMixin {
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

  static Future<EndDestination?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    return const EndDestination();
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {}
}
