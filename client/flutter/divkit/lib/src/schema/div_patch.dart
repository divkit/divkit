// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing.dart';
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
  final Arr<DivPatchChange> changes;

  /// Procedure for applying changes:
  /// • `transactional` — if an error occurs during application of at least one element, the changes aren't applied.
  /// • `partial` — all possible changes are applied. If there are errors, they are reported.
  // default value: DivPatchMode.partial
  final Expression<DivPatchMode> mode;

  /// Actions to perform after changes are applied.
  final Arr<DivAction>? onAppliedActions;

  /// Actions to perform if there’s an error when applying changes in transaction mode.
  final Arr<DivAction>? onFailedActions;

  @override
  List<Object?> get props => [
        changes,
        mode,
        onAppliedActions,
        onFailedActions,
      ];

  DivPatch copyWith({
    Arr<DivPatchChange>? changes,
    Expression<DivPatchMode>? mode,
    Arr<DivAction>? Function()? onAppliedActions,
    Arr<DivAction>? Function()? onFailedActions,
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
        changes: reqProp<Arr<DivPatchChange>>(
          safeParseObjects(
            json['changes'],
            (v) => reqProp<DivPatchChange>(
              safeParseObject(
                v,
                parse: DivPatchChange.fromJson,
              ),
            ),
          ),
          name: 'changes',
        ),
        mode: reqVProp<DivPatchMode>(
          safeParseStrEnumExpr(
            json['mode'],
            parse: DivPatchMode.fromJson,
            fallback: DivPatchMode.partial,
          ),
          name: 'mode',
        ),
        onAppliedActions: safeParseObjects(
          json['on_applied_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        onFailedActions: safeParseObjects(
          json['on_failed_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivPatch resolve(DivVariableContext context) {
    tryResolveList(changes, (v) => v.resolve(context));
    mode.resolve(context);
    tryResolveList(onAppliedActions, (v) => v.resolve(context));
    tryResolveList(onFailedActions, (v) => v.resolve(context));
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
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivPatchMode: $json",
        error: e,
        stackTrace: st,
      );
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
  final Arr<Div>? items;

  @override
  List<Object?> get props => [
        id,
        items,
      ];

  DivPatchChange copyWith({
    String? id,
    Arr<Div>? Function()? items,
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
        id: reqProp<String>(
          safeParseStr(
            json['id'],
          ),
          name: 'id',
        ),
        items: safeParseObjects(
          json['items'],
          (v) => reqProp<Div>(
            safeParseObject(
              v,
              parse: Div.fromJson,
            ),
          ),
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivPatchChange resolve(DivVariableContext context) {
    return this;
  }
}
