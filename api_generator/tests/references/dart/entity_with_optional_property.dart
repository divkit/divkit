// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithOptionalProperty extends Resolvable with EquatableMixin  {
  const EntityWithOptionalProperty({
    this.property,
  });

  static const type = "entity_with_optional_property";
  final Expression<String>? property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithOptionalProperty copyWith({
      Expression<String>? Function()?  property,
  }) => EntityWithOptionalProperty(
      property: property != null ? property.call() : this.property,
    );

  static EntityWithOptionalProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithOptionalProperty(
        property: safeParseStrExpr(json['property'],),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  EntityWithOptionalProperty resolve(DivVariableContext context) {
    property?.resolve(context);
    return this;
  }
}
