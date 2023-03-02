// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithArrayOfEnums with EquatableMixin {
  const EntityWithArrayOfEnums({
    required this.items,
  });

  static const type = "entity_with_array_of_enums";
  // at least 1 elements
  final List<EntityWithArrayOfEnumsItem> items;

  @override
  List<Object?> get props => [
        items,
      ];

  static EntityWithArrayOfEnums? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithArrayOfEnums(
      items: (json['items'] as List<dynamic>).map((s) => EntityWithArrayOfEnumsItem.fromJson(s)!).toList(),
    );
  }
}

enum EntityWithArrayOfEnumsItem {
  first('first'),
  second('second');

  final String value;

  const EntityWithArrayOfEnumsItem(this.value);

  static EntityWithArrayOfEnumsItem? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'first':
        return EntityWithArrayOfEnumsItem.first;
      case 'second':
        return EntityWithArrayOfEnumsItem.second;
    }
    return null;
  }
}
