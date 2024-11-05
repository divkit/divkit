// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Edits the element.
class DivPatch extends Resolvable with EquatableMixin {
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

  /// Actions to perform after changes are applied.
  final List<DivAction>? onAppliedActions;

  /// Actions to perform if there’s an error when applying changes in transaction mode.
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

  @override
  DivPatch resolve(DivVariableContext context) {
    safeListResolve(changes, (v) => v.resolve(context));
    mode.resolve(context);
    safeListResolve(onAppliedActions, (v) => v.resolve(context));
    safeListResolve(onFailedActions, (v) => v.resolve(context));
    return this;
  }
}

enum DivPatchMode implements Resolvable {
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

  @override
  DivPatchMode resolve(DivVariableContext context) => this;
}

class DivPatchChange extends Resolvable with EquatableMixin {
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

  @override
  DivPatchChange resolve(DivVariableContext context) {
    return this;
  }
}
