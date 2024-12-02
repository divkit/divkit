// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithoutProperties extends Resolvable with EquatableMixin  {
  const EntityWithoutProperties();

  static const type = "entity_without_properties";

  @override
  List<Object?> get props => [];

  EntityWithoutProperties? copyWith() => this;

  static EntityWithoutProperties? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    return const EntityWithoutProperties();
  }

  EntityWithoutProperties resolve(DivVariableContext context) => this;
}
