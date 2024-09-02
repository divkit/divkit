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
  Obj mergeSync() {
    if (templates?.isNotEmpty ?? false) {
      return _processObjSync(layout, _processSync);
    }
    return layout;
  }

  Obj _processSync(Obj it) {
    final template = templates?[it[type]] as Obj?;
    if (template == null) {
      return it;
    }
    final context = Obj.of(it)..remove(type);
    return _mergeSync(it, _processObjSync(template, _processSync), context);
  }

  Obj _mergeSync(Obj it, Obj ext, Obj ctx) {
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
            final res = _replaceSync(result[key] ?? value, value, ctx);
            for (final r in res.used) {
              result.remove(r);
            }
            result[key] = res.obj;
          }
        } else if (value is Arr) {
          final list = [];
          for (final e in value) {
            final res = _replaceSync(e ?? value, e, ctx);
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

  _ReplaceResult _replaceSync(Obj it, Obj ext, Obj ctx) {
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
          final res = _replaceSync(result[key] ?? value, value, ctx);
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
            if (e != null && e is! Obj) {
              list.add(e);
              continue;
            }

            final res = _replaceSync(e ?? value, e, ctx);
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
  Obj _processObjSync(
    Obj obj,
    Obj Function(Obj) process,
  ) {
    final it = <String, dynamic>{}; // Obj
    final processed = process(obj);
    for (var e in processed.entries) {
      it[e.key] = (e.value is Obj)
          ? _processObjSync(e.value, process)
          : (e.value is Arr)
              ? _processArrSync(e.value, process)
              : e.value;
    }
    return it;
  }

  /// Applies [process] to the entire [arr] recursively.
  Arr _processArrSync(
    Arr arr,
    Obj Function(Obj) process,
  ) =>
      arr.map((item) {
        if (item is Obj) {
          return _processObjSync(item, process);
        } else if (item is Arr) {
          return _processArrSync(item, process);
        }
        return item;
      }).toList();

  /// Performs the process of merging the [layout] and [templates].
  Future<Obj> merge() async {
    if (templates?.isNotEmpty ?? false) {
      return _processObj(layout, _process);
    }
    return layout;
  }

  Future<Obj> _process(Obj it) async {
    final template = templates?[it[type]] as Obj?;
    if (template == null) {
      return it;
    }
    final context = Obj.of(it)..remove(type);
    return _merge(it, await _processObj(template, _process), context);
  }

  Future<Obj> _merge(Obj it, Obj ext, Obj ctx) async {
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
            final res = await _replace(result[key] ?? value, value, ctx);
            for (final r in res.used) {
              result.remove(r);
            }
            result[key] = res.obj;
          }
        } else if (value is Arr) {
          final list = [];
          for (final e in value) {
            final res = await _replace(e ?? value, e, ctx);
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

  Future<_ReplaceResult> _replace(Obj it, Obj ext, Obj ctx) async {
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
          final res = await _replace(result[key] ?? value, value, ctx);
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
            if (e != null && e is! Obj) {
              list.add(e);
              continue;
            }

            final res = await _replace(e ?? value, e, ctx);
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
  Future<Obj> _processObj(
    Obj obj,
    Future<Obj> Function(Obj) process,
  ) async {
    final it = <String, dynamic>{}; // Obj
    final processed = await process(obj);
    for (var e in processed.entries) {
      it[e.key] = (e.value is Obj)
          ? await _processObj(e.value, process)
          : (e.value is Arr)
              ? await _processArr(e.value, process)
              : e.value;
    }
    return it;
  }

  /// Applies [process] to the entire [arr] recursively.
  Future<Arr> _processArr(
    Arr arr,
    Future<Obj> Function(Obj) process,
  ) async {
    List it = [];
    for (final item in arr) {
      if (item is Obj) {
        it.add(await _processObj(item, process));
      } else if (item is Arr) {
        it.add(await _processArr(item, process));
      } else {
        it.add(item);
      }
    }
    return it;
  }
}
