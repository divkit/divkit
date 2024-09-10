// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class EntityWithoutProperties extends Preloadable with EquatableMixin  {
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

  static Future<EntityWithoutProperties?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    return const EntityWithoutProperties();
  }

  Future<void> preload(Map<String, dynamic> context,) async {}
}
