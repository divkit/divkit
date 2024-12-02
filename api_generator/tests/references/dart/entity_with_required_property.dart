// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithRequiredProperty extends Resolvable with EquatableMixin  {
  const EntityWithRequiredProperty({
    required this.property,
  });

  static const type = "entity_with_required_property";
   // at least 1 char
  final Expression<String> property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithRequiredProperty copyWith({
      Expression<String>?  property,
  }) => EntityWithRequiredProperty(
      property: property ?? this.property,
    );

  static EntityWithRequiredProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithRequiredProperty(
        property: reqVProp<String>(safeParseStrExpr(json['property'],), name: 'property',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  EntityWithRequiredProperty resolve(DivVariableContext context) {
    property.resolve(context);
    return this;
  }
}
