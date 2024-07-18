// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'entity.dart';
import 'entity_with_string_enum_property.dart';

class EntityWithEntityProperty with EquatableMixin {
  const EntityWithEntityProperty({
    this.entity = const Entity.entityWithStringEnumProperty(const EntityWithStringEnumProperty(property: ValueExpression(EntityWithStringEnumPropertyProperty.second),)),
  });

  static const type = "entity_with_entity_property";
  // default value: const Entity.entityWithStringEnumProperty(const EntityWithStringEnumProperty(property: ValueExpression(EntityWithStringEnumPropertyProperty.second),))
  final Entity entity;

  @override
  List<Object?> get props => [
        entity,
      ];

  EntityWithEntityProperty copyWith({
      Entity?  entity,
  }) => EntityWithEntityProperty(
      entity: entity ?? this.entity,
    );

  static EntityWithEntityProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithEntityProperty(
      entity: safeParseObj(Entity.fromJson(json['entity']), fallback: const Entity.entityWithStringEnumProperty(const EntityWithStringEnumProperty(property: ValueExpression(EntityWithStringEnumPropertyProperty.second),)),)!,
    );
  }
}
