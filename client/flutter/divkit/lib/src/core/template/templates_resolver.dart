typedef Obj = Map<String, dynamic>;
typedef Arr = List<dynamic>;

class _ReplaceResult {
  final Obj obj;
  final Set<String> used;

  const _ReplaceResult(this.obj, this.used);
}

/// Allows inflate the layout with templates. It helps to repeatedly reduce
/// the layout data sent over the network. It has the following key features:
///
/// 1. Templates can be inherited by type:
/// "a": { "type": "text", ... } <- "b": { "type": "a", ... } <- ...
///
/// 2. Templates can be used inside other templates:
/// "box": {
///   "type": "container",
///   "items": [
///     { "type": "a", ... },
///     { "type": "b", ... },
///     { "type": "c", ... }
///   ]
/// }
///
/// 3. There are links to the parameters:
/// "$clear_prop" : "real_prop"
///
/// 4. You can refer to internal fields:
/// "box": {
///   "type": "container",
///   "background": [
///     {
///       "type": "solid",
///       "$color": "bg_color"
///     }
///   ],
///   "items": [...]
/// }
///
/// And usage:
/// {
///   "type": "box",
///   "bg_color": "#ffffff"
/// }
///
/// 5. Transitive links, just like on the DivKit Web!:
/// "a": { "type": "text", "$text": "txt" } <- "b": { "type": "a", "$txt": "day" } <- ...
///
/// The resulting link resolves like this:
/// "$text": "txt" + "$txt": "day" = "$text": "day"
class TemplatesResolver {
  static const type = 'type';
  static const link = '\$';

  final Obj layout;
  final Obj? templates;

  /// You need to call [merge] to get the inflated [layout].
  const TemplatesResolver({
    required this.layout,
    this.templates,
  });

  /// Performs the process of merging the [layout] and [templates].
  Obj merge() {
    if (templates?.isNotEmpty ?? false) {
      return _processObj(layout, _process);
    }
    return layout;
  }

  Obj _process(Obj it) {
    final template = templates?[it[type]] as Obj?;
    if (template == null) {
      return it;
    }
    final context = Obj.of(it)..remove(type);
    return _merge(it, _processObj(template, _process), context);
  }

  Obj _merge(Obj it, Obj ext, Obj ctx) {
    final result = Obj.of(it);
    for (final key in ext.keys) {
      final value = ext[key];
      if (key.startsWith(link)) {
        final ctxKey = key.substring(1);
        if (ctx.containsKey(value)) {
          result[ctxKey] = ctx[value];
          if (value != ctxKey) {
            result.remove(value);
          }
        } else {
          // Transitive links resolves here
          if (ctx.containsKey("$link$value")) {
            result[key] = ctx["$link$value"];
            result.remove("$link$value");
          }
        }
      } else {
        if (value is Obj) {
          if (result.containsKey(type)) {
            final res = _replace(result[key] ?? value, value, ctx);
            for (final r in res.used) {
              result.remove(r);
            }
            result[key] = res.obj;
          }
        } else if (value is Arr) {
          final list = [];
          for (final e in value) {
            final res = _replace(e ?? value, e, ctx);
            for (final r in res.used) {
              result.remove(r);
            }
            list.add(res.obj);
          }
          result[key] = list;
        } else {
          if (key == type || !result.containsKey(key)) {
            result[key] = value;
          }
        }
      }
    }

    return result;
  }

  _ReplaceResult _replace(Obj it, Obj ext, Obj ctx) {
    final result = Obj.of(it);
    final used = <String>{};
    for (final key in ext.keys) {
      final value = ext[key];
      if (key.startsWith(link)) {
        if (ctx.containsKey(value)) {
          final ctxKey = key.substring(1);
          result[ctxKey] = ctx[value];
          used.add(value);
          if (key != ctxKey) {
            result.remove(key);
          }
        }
      } else {
        if (value is Obj) {
          final res = _replace(result[key] ?? value, value, ctx);
          for (final r in res.used) {
            if (result.containsKey(r)) {
              result.remove(r);
            } else {
              used.add(r);
            }
          }
          result[key] = res.obj;
        } else if (value is Arr) {
          final list = [];
          for (final e in value) {
            final res = _replace(e ?? value, e, ctx);
            for (final r in res.used) {
              if (result.containsKey(r)) {
                result.remove(r);
              } else {
                used.add(r);
              }
            }
            list.add(res.obj);
          }
          result[key] = list;
        }
      }
    }

    return _ReplaceResult(result, used);
  }

  /// Applies [process] to the entire [obj] recursively.
  Obj _processObj(
    Obj obj,
    Obj Function(Obj) process,
  ) {
    final it = <String, dynamic>{}; // Obj
    final processed = process(obj);
    for (var e in processed.entries) {
      it[e.key] = (e.value is Obj)
          ? _processObj(e.value, process)
          : (e.value is Arr)
              ? _processArr(e.value, process)
              : e.value;
    }
    return it;
  }

  /// Applies [process] to the entire [arr] recursively.
  Arr _processArr(
    Arr arr,
    Obj Function(Obj) process,
  ) =>
      arr.map((item) {
        if (item is Obj) {
          return _processObj(item, process);
        } else if (item is Arr) {
          return _processArr(item, process);
        }
        return item;
      }).toList();
}
