// Generated code. Do not modify.

class EntityWithPropertyWithDefaultValue {
  const EntityWithPropertyWithDefaultValue({
    this.iNum,
    this.nested,
    this.url,
  });

  final int? iNum;
  final EntityWithPropertyWithDefaultValueNested? nested;
  static const String type = "entity_with_property_with_default_value";
  final Uri? url;
}

class EntityWithPropertyWithDefaultValueNested {
  const EntityWithPropertyWithDefaultValueNested({
    this.iNum,
    required this.nonOptional,
    this.url,
  });

  final int? iNum;
  final String nonOptional;
  final Uri? url;
}
