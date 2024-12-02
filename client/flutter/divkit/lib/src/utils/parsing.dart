import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';

export 'package:divkit/src/core/expression/expression.dart';
export 'package:divkit/src/core/expression/parse.dart';
export 'package:divkit/src/core/protocol/div_logger.dart';
export 'package:divkit/src/core/types.dart';
export 'package:divkit/src/core/variable/variable_context.dart';

T reqProp<T>(Object? object, {String? name}) {
  if (object != null && object is T) {
    return object as T;
  }
  if (name != null) {
    throw 'Expect "$name": $T';
  } else {
    throw 'Expect: $T';
  }
}

Expression<T> reqVProp<T>(Object? object, {String? name}) {
  if (object != null && object is Expression<T>) {
    return object;
  }
  if (name != null) {
    throw 'Expect "$name": Expression<$T>';
  } else {
    throw 'Expect: Expression<$T>';
  }
}

void tryResolveList(
  dynamic list,
  Function(dynamic) resolve,
) {
  if (list == null) {
    return;
  } else if (list is List<dynamic>) {
    for (final l in list) {
      try {
        resolve(l);
      } catch (e, st) {
        logger.warning("Couldn't resolve $l", error: e, stackTrace: st);
      }
    }
  }
}
