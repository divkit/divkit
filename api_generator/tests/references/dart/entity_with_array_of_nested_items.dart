// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'entity.dart';
import 'package:divkit/src/utils/parsing.dart';


class EntityWithArrayOfNestedItems extends Resolvable with EquatableMixin  {
  const EntityWithArrayOfNestedItems({
    required this.items,
  });

  static const type = "entity_with_array_of_nested_items";
   // at least 1 elements
  final Arr<EntityWithArrayOfNestedItemsItem> items;

  @override
  List<Object?> get props => [
        items,
      ];

  EntityWithArrayOfNestedItems copyWith({
      Arr<EntityWithArrayOfNestedItemsItem>?  items,
  }) => EntityWithArrayOfNestedItems(
      items: items ?? this.items,
    );

  static EntityWithArrayOfNestedItems? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayOfNestedItems(
        items: reqProp<Arr<EntityWithArrayOfNestedItemsItem>>(safeParseObjects(json['items'],(v) => reqProp<EntityWithArrayOfNestedItemsItem>(safeParseObject(v, parse: EntityWithArrayOfNestedItemsItem.fromJson,),), ), name: 'items',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  EntityWithArrayOfNestedItems resolve(DivVariableContext context) {
    return this;
  }
}


class EntityWithArrayOfNestedItemsItem extends Resolvable with EquatableMixin  {
  const EntityWithArrayOfNestedItemsItem({
    required this.entity,
    required this.property,
  });

  final Entity entity;
  final Expression<String> property;

  @override
  List<Object?> get props => [
        entity,
        property,
      ];

  EntityWithArrayOfNestedItemsItem copyWith({
      Entity?  entity,
      Expression<String>?  property,
  }) => EntityWithArrayOfNestedItemsItem(
      entity: entity ?? this.entity,
      property: property ?? this.property,
    );

  static EntityWithArrayOfNestedItemsItem? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayOfNestedItemsItem(
        entity: reqProp<Entity>(safeParseObject(json['entity'], parse: Entity.fromJson,), name: 'entity',),
        property: reqVProp<String>(safeParseStrExpr(json['property'],), name: 'property',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  EntityWithArrayOfNestedItemsItem resolve(DivVariableContext context) {
    entity.resolve(context);
    property.resolve(context);
    return this;
  }
}
