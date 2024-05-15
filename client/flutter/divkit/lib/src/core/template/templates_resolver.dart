import 'package:divkit/src/core/template/json_item.dart';
import 'package:divkit/src/core/template/json_parser.dart';

class TemplatesResolver {
  final MapJsonItem? _templatesMap;
  final MapJsonItem _layoutMap;

  static const _keyType = 'type';

  const TemplatesResolver({
    required MapJsonItem layout,
    MapJsonItem? templates,
  })  : _layoutMap = layout,
        _templatesMap = templates;

  factory TemplatesResolver.fromTemplates({
    required Map<String, dynamic> layout,
    Map<String, dynamic>? templates,
  }) =>
      TemplatesResolver(
        layout: JsonParser.parse(layout),
        templates: JsonParser.parse(templates),
      );

  MapJsonItem merge() {
    final layout = _layoutMap.deepCopy((e) => e);

    if (!(_templatesMap?.isEmpty ?? true)) {
      layout.keyValues.where((e) {
        if (e.key == _keyType) {
          return false;
        }

        if (e.value is! MapJsonItem && e.value is! ArrayJsonItem) {
          return false;
        }

        return true;
      }).forEach((e) {
        if (e.value is MapJsonItem) {
          _process(e.value as MapJsonItem);
        } else if (e.value is ArrayJsonItem) {
          for (var el in (e.value as ArrayJsonItem).value) {
            if (el is MapJsonItem) {
              _process(el);
            }
          }
        }
      });
    }

    return layout;
  }

  void _process(MapJsonItem item) {
    final template = _findTemplate(item.asString(_keyType));

    if (template == null) {
      // process search in items and move deeper
      item.keyValues.map((e) => e.value).forEach((e) {
        if (e is MapJsonItem) {
          _process(e);
        } else if (e is ArrayJsonItem) {
          for (var i in e.value) {
            if (i is MapJsonItem) {
              _process(i);
            }
          }
        }
      });
    } else {
      // process template
      _mergeWithTemplate(item: item, template: template);
      // process resolved template
      _process(item);
    }
  }

  MapJsonItem? _findTemplate(String? key) =>
      key == null ? null : _templatesMap?.asMap(key);

  void _mergeWithTemplate({
    required MapJsonItem item,
    required MapJsonItem template,
  }) {
    final Set<String> namedParameters = {};
    final newTemplate = template.deepCopy((e) {
      if (e.key.startsWith("\$")) {
        final newKey = e.key.replaceAll("\$", '');
        final keyToSearch = (e.value as StringJsonItem).value;

        final newValue = item.findDeep(keyToSearch);

        namedParameters.add(keyToSearch);

        return newValue != null ? MapEntry(newKey, newValue) : e;
      } else {
        return e;
      }
    });

    newTemplate.value.forEach((key, value) {
      if (key.startsWith("\$")) {
        // do not add $param to result map
        return;
      }
      if (key == _keyType) {
        item.addOrUpdate(key, value);
      } else {
        item.addNew(key, value);
      }
    });

    // remove named parameters from result map
    for (var element in namedParameters) {
      item.value.remove(element);
    }
  }
}
