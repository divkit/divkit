// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Infinite number of repetitions.
class DivInfinityCount extends Preloadable with EquatableMixin {
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

  static Future<DivInfinityCount?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    return const DivInfinityCount();
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {}
}
