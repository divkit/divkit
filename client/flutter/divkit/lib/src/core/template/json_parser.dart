import 'package:divkit/src/core/template/json_item.dart';

class JsonParser {
  const JsonParser();

  static MapJsonItem parse(Map<String, dynamic>? json) {
    if (json == null) {
      return const MapJsonItem({});
    }
    final parseResult = <String, JsonItem>{};

    _parseDecoded(json, parseResult);

    return MapJsonItem(parseResult);
  }

  static void _parseDecoded(
    Map<String, dynamic> raw,
    Map<String, JsonItem> accumulator,
  ) {
    raw.forEach((key, value) {
      if (value is String) {
        accumulator[key] = StringJsonItem(value);
      } else if (value is Map) {
        final newAccumulator = <String, JsonItem>{};

        _parseDecoded(value as Map<String, dynamic>, newAccumulator);

        accumulator[key] = MapJsonItem(newAccumulator);
      } else if (value is List) {
        final newAccumulator = <String, JsonItem>{};
        final newRaw = (value)
            .asMap()
            .map((key, value) => MapEntry(key.toString(), value));

        _parseDecoded(newRaw, newAccumulator);

        final listValue = newAccumulator.entries.map((e) => e.value).toList();
        accumulator[key] = ArrayJsonItem(listValue);
      } else if (value is int) {
        accumulator[key] = IntegerJsonItem(value);
      } else if (value is double) {
        accumulator[key] = FloatJsonItem(value);
      } else if (value is bool) {
        accumulator[key] = BooleanJsonItem(value);
      }
    });
  }
}
