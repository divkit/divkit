// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Edits the element.
class DivPatch extends Preloadable with EquatableMixin {
  const DivPatch({
    required this.changes,
    this.mode = const ValueExpression(DivPatchMode.partial),
    this.onAppliedActions,
    this.onFailedActions,
  });

  /// Element changes.
  // at least 1 elements
  final List<DivPatchChange> changes;

  /// Procedure for applying changes:
  /// • `transactional` — if an error occurs during application of at least one element, the changes aren't applied.
  /// • `partial` — all possible changes are applied. If there are errors, they are reported.
  // default value: DivPatchMode.partial
  final Expression<DivPatchMode> mode;

  /// Actions after applying patch.
  final List<DivAction>? onAppliedActions;

  /// Actions after an error applying patch in transactional mode.
  final List<DivAction>? onFailedActions;

  @override
  List<Object?> get props => [
        changes,
        mode,
        onAppliedActions,
        onFailedActions,
      ];

  DivPatch copyWith({
    List<DivPatchChange>? changes,
    Expression<DivPatchMode>? mode,
    List<DivAction>? Function()? onAppliedActions,
    List<DivAction>? Function()? onFailedActions,
  }) =>
      DivPatch(
        changes: changes ?? this.changes,
        mode: mode ?? this.mode,
        onAppliedActions: onAppliedActions != null
            ? onAppliedActions.call()
            : this.onAppliedActions,
        onFailedActions: onFailedActions != null
            ? onFailedActions.call()
            : this.onFailedActions,
      );

  static DivPatch? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPatch(
        changes: safeParseObj(
          safeListMap(
            json['changes'],
            (v) => safeParseObj(
              DivPatchChange.fromJson(v),
            )!,
          ),
        )!,
        mode: safeParseStrEnumExpr(
          json['mode'],
          parse: DivPatchMode.fromJson,
          fallback: DivPatchMode.partial,
        )!,
        onAppliedActions: safeParseObj(
          safeListMap(
            json['on_applied_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        onFailedActions: safeParseObj(
          safeListMap(
            json['on_failed_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivPatch?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPatch(
        changes: (await safeParseObjAsync(
          await safeListMapAsync(
            json['changes'],
            (v) => safeParseObj(
              DivPatchChange.fromJson(v),
            )!,
          ),
        ))!,
        mode: (await safeParseStrEnumExprAsync(
          json['mode'],
          parse: DivPatchMode.fromJson,
          fallback: DivPatchMode.partial,
        ))!,
        onAppliedActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['on_applied_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        onFailedActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['on_failed_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
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
      await safeFuturesWait(changes, (v) => v.preload(context));
      await mode.preload(context);
      await safeFuturesWait(onAppliedActions, (v) => v.preload(context));
      await safeFuturesWait(onFailedActions, (v) => v.preload(context));
    } catch (e) {
      return;
    }
  }
}

enum DivPatchMode implements Preloadable {
  transactional('transactional'),
  partial('partial');

  final String value;

  const DivPatchMode(this.value);
  bool get isTransactional => this == transactional;

  bool get isPartial => this == partial;

  T map<T>({
    required T Function() transactional,
    required T Function() partial,
  }) {
    switch (this) {
      case DivPatchMode.transactional:
        return transactional();
      case DivPatchMode.partial:
        return partial();
    }
  }

  T maybeMap<T>({
    T Function()? transactional,
    T Function()? partial,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivPatchMode.transactional:
        return transactional?.call() ?? orElse();
      case DivPatchMode.partial:
        return partial?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivPatchMode? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'transactional':
          return DivPatchMode.transactional;
        case 'partial':
          return DivPatchMode.partial;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivPatchMode?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'transactional':
          return DivPatchMode.transactional;
        case 'partial':
          return DivPatchMode.partial;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

class DivPatchChange extends Preloadable with EquatableMixin {
  const DivPatchChange({
    required this.id,
    this.items,
  });

  /// ID of an element to be replaced or removed.
  final String id;

  /// Elements to be inserted. If the parameter isn't specified, the element will be removed.
  final List<Div>? items;

  @override
  List<Object?> get props => [
        id,
        items,
      ];

  DivPatchChange copyWith({
    String? id,
    List<Div>? Function()? items,
  }) =>
      DivPatchChange(
        id: id ?? this.id,
        items: items != null ? items.call() : this.items,
      );

  static DivPatchChange? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPatchChange(
        id: safeParseStr(
          json['id']?.toString(),
        )!,
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              Div.fromJson(v),
            )!,
          ),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivPatchChange?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPatchChange(
        id: (await safeParseStrAsync(
          json['id']?.toString(),
        ))!,
        items: await safeParseObjAsync(
          await safeListMapAsync(
            json['items'],
            (v) => safeParseObj(
              Div.fromJson(v),
            )!,
          ),
        ),
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
