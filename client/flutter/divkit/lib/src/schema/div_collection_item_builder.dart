// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivCollectionItemBuilder extends Preloadable with EquatableMixin {
  const DivCollectionItemBuilder({
    required this.data,
    this.dataElementName = "it",
    required this.prototypes,
  });

  final Expression<List<dynamic>> data;
  // default value: "it"
  final String dataElementName;
  // at least 1 elements
  final List<DivCollectionItemBuilderPrototype> prototypes;

  @override
  List<Object?> get props => [
        data,
        dataElementName,
        prototypes,
      ];

  DivCollectionItemBuilder copyWith({
    Expression<List<dynamic>>? data,
    String? dataElementName,
    List<DivCollectionItemBuilderPrototype>? prototypes,
  }) =>
      DivCollectionItemBuilder(
        data: data ?? this.data,
        dataElementName: dataElementName ?? this.dataElementName,
        prototypes: prototypes ?? this.prototypes,
      );

  static DivCollectionItemBuilder? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivCollectionItemBuilder(
        data: safeParseListExpr(
          json['data'],
        )!,
        dataElementName: safeParseStr(
          json['data_element_name']?.toString(),
          fallback: "it",
        )!,
        prototypes: safeParseObj(
          safeListMap(
            json['prototypes'],
            (v) => safeParseObj(
              DivCollectionItemBuilderPrototype.fromJson(v),
            )!,
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivCollectionItemBuilder?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivCollectionItemBuilder(
        data: (await safeParseListExprAsync(
          json['data'],
        ))!,
        dataElementName: (await safeParseStrAsync(
          json['data_element_name']?.toString(),
          fallback: "it",
        ))!,
        prototypes: (await safeParseObjAsync(
          await safeListMapAsync(
            json['prototypes'],
            (v) => safeParseObj(
              DivCollectionItemBuilderPrototype.fromJson(v),
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
      await data.preload(context);
      await safeFuturesWait(prototypes, (v) => v.preload(context));
    } catch (e) {
      return;
    }
  }
}

class DivCollectionItemBuilderPrototype extends Preloadable
    with EquatableMixin {
  const DivCollectionItemBuilderPrototype({
    required this.div,
    this.id,
    this.selector = const ValueExpression(true),
  });

  final Div div;

  final Expression<String>? id;
  // default value: true
  final Expression<bool> selector;

  @override
  List<Object?> get props => [
        div,
        id,
        selector,
      ];

  DivCollectionItemBuilderPrototype copyWith({
    Div? div,
    Expression<String>? Function()? id,
    Expression<bool>? selector,
  }) =>
      DivCollectionItemBuilderPrototype(
        div: div ?? this.div,
        id: id != null ? id.call() : this.id,
        selector: selector ?? this.selector,
      );

  static DivCollectionItemBuilderPrototype? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivCollectionItemBuilderPrototype(
        div: safeParseObj(
          Div.fromJson(json['div']),
        )!,
        id: safeParseStrExpr(
          json['id']?.toString(),
        ),
        selector: safeParseBoolExpr(
          json['selector'],
          fallback: true,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivCollectionItemBuilderPrototype?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivCollectionItemBuilderPrototype(
        div: (await safeParseObjAsync(
          Div.fromJson(json['div']),
        ))!,
        id: await safeParseStrExprAsync(
          json['id']?.toString(),
        ),
        selector: (await safeParseBoolExprAsync(
          json['selector'],
          fallback: true,
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
      await div.preload(context);
      await id?.preload(context);
      await selector.preload(context);
    } catch (e) {
      return;
    }
  }
}
