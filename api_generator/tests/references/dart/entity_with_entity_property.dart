// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'entity.dart';
import 'entity_with_string_enum_property.dart';

class EntityWithEntityProperty with EquatableMixin {
  const EntityWithEntityProperty({
    this.entity = const Entity(const EntityWithStringEnumProperty(property: Expression.value(EntityWithStringEnumPropertyProperty.second),)),
  });

  static const type = "entity_with_entity_property";
  // default value: const Entity(const EntityWithStringEnumProperty(property: Expression.value(EntityWithStringEnumPropertyProperty.second),))
  final Entity entity;

  @override
  List<Object?> get props => [
        entity,
      ];

  static EntityWithEntityProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithEntityProperty(
      entity: safeParseClass(Entity.fromJson(json['entity'])) ?? const Entity(const EntityWithStringEnumProperty(property: Expression.value(EntityWithStringEnumPropertyProperty.second),)),
    );
  }
}
