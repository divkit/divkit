// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'entity.dart';
import 'entity_with_string_enum_property.dart';
import 'package:divkit/src/utils/parsing.dart';


class EntityWithEntityProperty with EquatableMixin  {
  const EntityWithEntityProperty({
    this.entity = const Entity.entityWithStringEnumProperty(const EntityWithStringEnumProperty(property: ValueExpression(EntityWithStringEnumPropertyProperty.second,),),),
  });

  static const type = "entity_with_entity_property";
   // default value: const Entity.entityWithStringEnumProperty(const EntityWithStringEnumProperty(property: ValueExpression(EntityWithStringEnumPropertyProperty.second,),),)
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

  static EntityWithEntityProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithEntityProperty(
        entity: reqProp<Entity>(safeParseObject(json['entity'], parse: Entity.fromJson, fallback: const Entity.entityWithStringEnumProperty(const EntityWithStringEnumProperty(property: ValueExpression(EntityWithStringEnumPropertyProperty.second,),),),), name: 'entity',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
