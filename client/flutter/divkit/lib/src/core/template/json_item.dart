class JsonItem<T> {
  final T value;

  const JsonItem(this.value);
}

abstract class Copy<T extends JsonItem> {
  T copy();
}

abstract class DeepCopy<T extends JsonItem> {
  T deepCopy(
    MapEntry<String, JsonItem> Function(MapEntry<String, JsonItem> e) action,
  );
}

class IntegerJsonItem extends JsonItem<int> implements Copy<IntegerJsonItem> {
  const IntegerJsonItem(super.value);

  @override
  IntegerJsonItem copy() => IntegerJsonItem(value);
}

class BooleanJsonItem extends JsonItem<bool> implements Copy<BooleanJsonItem> {
  const BooleanJsonItem(super.value);

  @override
  BooleanJsonItem copy() => BooleanJsonItem(value);
}

class StringJsonItem extends JsonItem<String> implements Copy<StringJsonItem> {
  const StringJsonItem(super.value);

  @override
  StringJsonItem copy() => StringJsonItem(value);
}

class FloatJsonItem extends JsonItem<double> implements Copy<FloatJsonItem> {
  const FloatJsonItem(super.value);

  @override
  FloatJsonItem copy() => FloatJsonItem(value);
}

class MapJsonItem extends JsonItem<Map<String, JsonItem>>
    implements DeepCopy<MapJsonItem> {
  const MapJsonItem(super.value);

  bool get isEmpty => value.isEmpty;

  int get totalItems => value.length;

  Iterable<MapEntry<String, JsonItem>> get keyValues => value.entries;

  T? as<T extends JsonItem>(String key) => value[key] as T?;

  MapJsonItem? asMap(String key) => as<MapJsonItem>(key);

  String? asString(String key) => as<StringJsonItem>(key)?.value;

  int? asInt(String key) => as<IntegerJsonItem>(key)?.value;

  bool? asBool(String key) => as<BooleanJsonItem>(key)?.value;

  double? asFloat(String key) => as<FloatJsonItem>(key)?.value;

  ArrayJsonItem? asArray(String key) => as<ArrayJsonItem>(key);

  void clear() => value.clear();

  void addOrUpdate(String key, JsonItem item) => value[key] = item;

  void addNew(String key, JsonItem item) {
    if (!value.containsKey(key)) {
      addOrUpdate(key, item);
    }
  }

  /// Looks for an item with given [key] in the map
  /// We must use Breadth-first search here to check the values of a parent map first
  JsonItem? findDeep(String key) {
    final item = value[key];
    if (item != null) {
      return item;
    }

    final childValues = keyValues.map((e) => e.value);

    for (var e in childValues) {
      if (e is MapJsonItem) {
        final findResult = e.findDeep(key);

        if (findResult != null) {
          return findResult;
        }
      } else if (e is ArrayJsonItem) {
        for (var i in e.value) {
          if (i is MapJsonItem) {
            final findResult = i.findDeep(key);

            if (findResult != null) {
              return findResult;
            }
          }
        }
      }
    }

    return null;
  }

  @override
  MapJsonItem deepCopy(
    MapEntry<String, JsonItem> Function(MapEntry<String, JsonItem> e) action,
  ) {
    List<MapEntry<String, JsonItem>> newEntries = [];

    for (var oldEntry in keyValues) {
      final newEntry = action(oldEntry);

      final newValue = newEntry.value;

      if (newValue is DeepCopy) {
        newEntries.add(
          MapEntry(newEntry.key, (newValue as DeepCopy).deepCopy(action)),
        );
      } else {
        if (oldEntry == newEntry) {
          if (newValue is Copy) {
            newEntries.add(MapEntry(newEntry.key, (newValue as Copy).copy()));
          }
        } else {
          newEntries.add(newEntry);
        }
      }
    }

    return MapJsonItem(Map.fromEntries(newEntries));
  }

  Map<String, dynamic> toGenericMap() {
    List<MapEntry<String, dynamic>> newEntries = [];

    for (var entry in keyValues) {
      if (entry.value is MapJsonItem) {
        newEntries.add(
          MapEntry(
            entry.key,
            (entry.value as MapJsonItem).toGenericMap(),
          ),
        );
      } else if (entry.value is ArrayJsonItem) {
        newEntries.add(
          MapEntry(
            entry.key,
            (entry.value as ArrayJsonItem).toGenericList(),
          ),
        );
      } else {
        newEntries.add(MapEntry(entry.key, entry.value.value));
      }
    }

    return Map.fromEntries(newEntries);
  }
}

class ArrayJsonItem extends JsonItem<List<JsonItem>>
    implements DeepCopy<ArrayJsonItem> {
  const ArrayJsonItem(super.value);

  bool get isEmpty => value.isEmpty;

  int get totalItems => value.length;

  T? as<T extends JsonItem>(int index) => value[index] as T?;

  MapJsonItem? asMap(int index) => as<MapJsonItem>(index);

  String? asString(int index) => as<StringJsonItem>(index)?.value;

  int? asInt(int index) => as<IntegerJsonItem>(index)?.value;

  bool? asBool(int index) => as<BooleanJsonItem>(index)?.value;

  double? asFloat(int index) => as<FloatJsonItem>(index)?.value;

  ArrayJsonItem? asArray(int index) => as<ArrayJsonItem>(index);

  @override
  ArrayJsonItem deepCopy(
    MapEntry<String, JsonItem> Function(MapEntry<String, JsonItem> e) action,
  ) {
    List<JsonItem> items = [];

    for (var oldItem in value) {
      if (oldItem is DeepCopy) {
        items.add((oldItem as DeepCopy).deepCopy(action));
      } else if (oldItem is Copy) {
        items.add((oldItem as Copy).copy());
      }
    }

    return ArrayJsonItem(items);
  }

  List<dynamic> toGenericList() => value.map((e) {
        if (e is MapJsonItem) {
          return e.toGenericMap();
        } else if (e is ArrayJsonItem) {
          return e.toGenericList();
        } else {
          return e.value;
        }
      }).toList();
}
