// Generated code. Do not modify.

import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivAppearanceSetTransition extends Preloadable with EquatableMixin {
  const DivAppearanceSetTransition({
    required this.items,
  });

  static const type = "set";
  // at least 1 elements
  final List<DivAppearanceTransition> items;

  @override
  List<Object?> get props => [
        items,
      ];

  DivAppearanceSetTransition copyWith({
    List<DivAppearanceTransition>? items,
  }) =>
      DivAppearanceSetTransition(
        items: items ?? this.items,
      );

  static DivAppearanceSetTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivAppearanceSetTransition(
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              DivAppearanceTransition.fromJson(v),
            )!,
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivAppearanceSetTransition?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivAppearanceSetTransition(
        items: (await safeParseObjAsync(
          await safeListMapAsync(
            json['items'],
            (v) => safeParseObj(
              DivAppearanceTransition.fromJson(v),
            )!,
          ),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await safeFuturesWait(items, (v) => v.preload(context));
    } catch (e) {
      return;
    }
  }
}
