// Generated code. Do not modify.

import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Animations.
class DivChangeSetTransition extends Resolvable with EquatableMixin {
  const DivChangeSetTransition({
    required this.items,
  });

  static const type = "set";

  /// List of animations.
  // at least 1 elements
  final Arr<DivChangeTransition> items;

  @override
  List<Object?> get props => [
        items,
      ];

  DivChangeSetTransition copyWith({
    Arr<DivChangeTransition>? items,
  }) =>
      DivChangeSetTransition(
        items: items ?? this.items,
      );

  static DivChangeSetTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivChangeSetTransition(
        items: reqProp<Arr<DivChangeTransition>>(
          safeParseObjects(
            json['items'],
            (v) => reqProp<DivChangeTransition>(
              safeParseObject(
                v,
                parse: DivChangeTransition.fromJson,
              ),
            ),
          ),
          name: 'items',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivChangeSetTransition resolve(DivVariableContext context) {
    return this;
  }
}
