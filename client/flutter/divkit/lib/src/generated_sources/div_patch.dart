// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div.dart';

class DivPatch with EquatableMixin {
  const DivPatch({
    required this.changes,
    this.mode = const ValueExpression(DivPatchMode.partial),
  });

  // at least 1 elements
  final List<DivPatchChange> changes;
  // default value: DivPatchMode.partial
  final Expression<DivPatchMode> mode;

  @override
  List<Object?> get props => [
        changes,
        mode,
      ];

  static DivPatch? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPatch(
      changes: safeParseObj(
        safeListMap(
            json['changes'],
            (v) => safeParseObj(
                  DivPatchChange.fromJson(v),
                )!),
      )!,
      mode: safeParseStrEnumExpr(
        json['mode'],
        parse: DivPatchMode.fromJson,
        fallback: DivPatchMode.partial,
      )!,
    );
  }
}

enum DivPatchMode {
  transactional('transactional'),
  partial('partial');

  final String value;

  const DivPatchMode(this.value);

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

  static DivPatchMode? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'transactional':
        return DivPatchMode.transactional;
      case 'partial':
        return DivPatchMode.partial;
    }
    return null;
  }
}

class DivPatchChange with EquatableMixin {
  const DivPatchChange({
    required this.id,
    this.items,
  });

  final String id;

  final List<Div>? items;

  @override
  List<Object?> get props => [
        id,
        items,
      ];

  static DivPatchChange? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPatchChange(
      id: safeParseStr(
        json['id']?.toString(),
      )!,
      items: safeParseObj(
        safeListMap(
            json['items'],
            (v) => safeParseObj(
                  Div.fromJson(v),
                )!),
      ),
    );
  }
}
