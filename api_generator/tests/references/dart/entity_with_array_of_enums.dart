// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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
      items: safeParseClass((json['items'] as List<dynamic>).map((s) => EntityWithArrayOfEnumsItem.fromJson(s)!).toList())!,
    );
  }
}

enum EntityWithArrayOfEnumsItem {
  first('first'),
  second('second');

  final String value;

  const EntityWithArrayOfEnumsItem(this.value);

  T map<T>({
    required T Function() first,
    required T Function() second,
  }) {
    switch (this) {
      case EntityWithArrayOfEnumsItem.first:
        return first();
      case EntityWithArrayOfEnumsItem.second:
        return second();
     }
  }

  T maybeMap<T>({
    T Function()? first,
    T Function()? second,
    required T Function() orElse,
  }) {
    switch (this) {
      case EntityWithArrayOfEnumsItem.first:
        return first?.call() ?? orElse();
      case EntityWithArrayOfEnumsItem.second:
        return second?.call() ?? orElse();
     }
  }


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
