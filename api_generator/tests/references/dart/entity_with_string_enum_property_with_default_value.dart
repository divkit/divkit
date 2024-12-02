// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithStringEnumPropertyWithDefaultValue extends Resolvable with EquatableMixin  {
  const EntityWithStringEnumPropertyWithDefaultValue({
    this.value = const ValueExpression(EntityWithStringEnumPropertyWithDefaultValueValue.second),
  });

  static const type = "entity_with_string_enum_property_with_default_value";
   // default value: EntityWithStringEnumPropertyWithDefaultValueValue.second
  final Expression<EntityWithStringEnumPropertyWithDefaultValueValue> value;

  @override
  List<Object?> get props => [
        value,
      ];

  EntityWithStringEnumPropertyWithDefaultValue copyWith({
      Expression<EntityWithStringEnumPropertyWithDefaultValueValue>?  value,
  }) => EntityWithStringEnumPropertyWithDefaultValue(
      value: value ?? this.value,
    );

  static EntityWithStringEnumPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithStringEnumPropertyWithDefaultValue(
        value: reqVProp<EntityWithStringEnumPropertyWithDefaultValueValue>(safeParseStrEnumExpr(json['value'], parse: EntityWithStringEnumPropertyWithDefaultValueValue.fromJson, fallback: EntityWithStringEnumPropertyWithDefaultValueValue.second,), name: 'value',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  EntityWithStringEnumPropertyWithDefaultValue resolve(DivVariableContext context) {
    value?.resolve(context);
    return this;
  }
}

enum EntityWithStringEnumPropertyWithDefaultValueValue implements Resolvable {
  first('first'),
  second('second'),
  third('third');

  final String value;

  const EntityWithStringEnumPropertyWithDefaultValueValue(this.value);
  bool get isFirst => this == first;

  bool get isSecond => this == second;

  bool get isThird => this == third;


  T map<T>({
    required T Function() first,
    required T Function() second,
    required T Function() third,
  }) {
    switch (this) {
      case EntityWithStringEnumPropertyWithDefaultValueValue.first:
        return first();
      case EntityWithStringEnumPropertyWithDefaultValueValue.second:
        return second();
      case EntityWithStringEnumPropertyWithDefaultValueValue.third:
        return third();
     }
  }

  T maybeMap<T>({
    T Function()? first,
    T Function()? second,
    T Function()? third,
    required T Function() orElse,
  }) {
    switch (this) {
      case EntityWithStringEnumPropertyWithDefaultValueValue.first:
        return first?.call() ?? orElse();
      case EntityWithStringEnumPropertyWithDefaultValueValue.second:
        return second?.call() ?? orElse();
      case EntityWithStringEnumPropertyWithDefaultValueValue.third:
        return third?.call() ?? orElse();
     }
  }


  static EntityWithStringEnumPropertyWithDefaultValueValue? fromJson(String? json,) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'first':
        return EntityWithStringEnumPropertyWithDefaultValueValue.first;
        case 'second':
        return EntityWithStringEnumPropertyWithDefaultValueValue.second;
        case 'third':
        return EntityWithStringEnumPropertyWithDefaultValueValue.third;
      }
      return null;
    } catch (e, st) {
      logger.warning("Invalid type of EntityWithStringEnumPropertyWithDefaultValueValue: $json", error: e, stackTrace: st,);
      return null;
    }
  }
  EntityWithStringEnumPropertyWithDefaultValueValue resolve(DivVariableContext context) => this;
}
