// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithPropertyWithDefaultValue with EquatableMixin {
  const EntityWithPropertyWithDefaultValue({
    this.iNum = 0,
    this.nested,
    this.url = const Uri.parse("https://yandex.ru"),
  });

  static const type = "entity_with_property_with_default_value";
  // constraint: number >= 0; default value: 0
  final int iNum;

  final EntityWithPropertyWithDefaultValueNested? nested;
  // valid schemes: [https]; default value: const Uri.parse("https://yandex.ru")
  final Uri url;

  @override
  List<Object?> get props => [
        iNum,
        nested,
        url,
      ];

  static EntityWithPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithPropertyWithDefaultValue(
      iNum: safeParseInt(json['iNum']) ?? 0,
      nested: EntityWithPropertyWithDefaultValueNested.fromJson(json['nested']),
      url: safeParseUri(json['url']) ?? const Uri.parse("https://yandex.ru"),
    );
  }
}

class EntityWithPropertyWithDefaultValueNested with EquatableMixin {
  const EntityWithPropertyWithDefaultValueNested({
    this.iNum = 0,
    required this.nonOptional,
    this.url = const Uri.parse("https://yandex.ru"),
  });

  // constraint: number >= 0; default value: 0
  final int iNum;

  final String nonOptional;
  // valid schemes: [https]; default value: const Uri.parse("https://yandex.ru")
  final Uri url;

  @override
  List<Object?> get props => [
        iNum,
        nonOptional,
        url,
      ];

  static EntityWithPropertyWithDefaultValueNested? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithPropertyWithDefaultValueNested(
      iNum: safeParseInt(json['iNum']) ?? 0,
      nonOptional: json['non_optional']!.toString(),
      url: safeParseUri(json['url']) ?? const Uri.parse("https://yandex.ru"),
    );
  }
}
