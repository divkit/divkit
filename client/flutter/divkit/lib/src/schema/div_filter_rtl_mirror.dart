// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Mirrors an image if the system uses RTL (Right-to-Left) text direction.
class DivFilterRtlMirror extends Resolvable with EquatableMixin {
  const DivFilterRtlMirror();

  static const type = "rtl_mirror";

  @override
  List<Object?> get props => [];

  DivFilterRtlMirror? copyWith() => this;

  static DivFilterRtlMirror? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    return const DivFilterRtlMirror();
  }

  @override
  DivFilterRtlMirror resolve(DivVariableContext context) => this;
}
