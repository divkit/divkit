// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class DivCollectionItemBuilder extends Resolvable with EquatableMixin {
  const DivCollectionItemBuilder({
    required this.data,
    this.dataElementName = "it",
    required this.prototypes,
  });

  /// Data that will be used to create collection elements.
  final Expression<Arr> data;

  /// Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries.
  // default value: "it"
  final String dataElementName;

  /// Array of `div` elements from which the collection elements will be created.
  // at least 1 elements
  final Arr<DivCollectionItemBuilderPrototype> prototypes;

  @override
  List<Object?> get props => [
        data,
        dataElementName,
        prototypes,
      ];

  DivCollectionItemBuilder copyWith({
    Expression<Arr>? data,
    String? dataElementName,
    Arr<DivCollectionItemBuilderPrototype>? prototypes,
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
        data: reqVProp<Arr>(
          safeParseListExpr(
            json['data'],
          ),
          name: 'data',
        ),
        dataElementName: reqProp<String>(
          safeParseStr(
            json['data_element_name'],
            fallback: "it",
          ),
          name: 'data_element_name',
        ),
        prototypes: reqProp<Arr<DivCollectionItemBuilderPrototype>>(
          safeParseObjects(
            json['prototypes'],
            (v) => reqProp<DivCollectionItemBuilderPrototype>(
              safeParseObject(
                v,
                parse: DivCollectionItemBuilderPrototype.fromJson,
              ),
            ),
          ),
          name: 'prototypes',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivCollectionItemBuilder resolve(DivVariableContext context) {
    data.resolve(context);
    tryResolveList(prototypes, (v) => v.resolve(context));
    return this;
  }
}

class DivCollectionItemBuilderPrototype extends Resolvable with EquatableMixin {
  const DivCollectionItemBuilderPrototype({
    required this.div,
    this.id,
    this.selector = const ValueExpression(true),
  });

  /// `Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
  final Div div;

  /// `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
  final Expression<String>? id;

  /// A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
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
        div: reqProp<Div>(
          safeParseObject(
            json['div'],
            parse: Div.fromJson,
          ),
          name: 'div',
        ),
        id: safeParseStrExpr(
          json['id'],
        ),
        selector: reqVProp<bool>(
          safeParseBoolExpr(
            json['selector'],
            fallback: true,
          ),
          name: 'selector',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivCollectionItemBuilderPrototype resolve(DivVariableContext context) {
    id?.resolve(context);
    selector.resolve(context);
    return this;
  }
}
