import 'package:divkit/src/core/runtime/core.dart';
import 'package:divkit/src/core/runtime/entities.dart';

dynamic defaultMembers(
  MemberToken member,
  Map<String, dynamic> context,
) {
  if (member.property is! FunctionToken) {
    throw "Member property expected after '.'";
  }

  final object = member.object.run(context);
  final property = member.property;

  if (property is FunctionToken) {
    final args = property.arguments
        .map((e) => guard(() => e.run(context), null))
        .toList();
    switch (property.identifier) {
      case 'toString':
        if (args.isNotEmpty) {
          throw 'Function has no matching overload for given argument';
        }
        return toString(object);
    }
  }

  if (object is List) {
    if (property is FunctionToken) {
      final args = property.arguments
          .map((e) => guard(() => e.run(context), null))
          .toList();
      switch (property.identifier) {
        case 'getArray':
          return cast<List>(object[trueInt(args[0])]);
        case 'getDict':
          return cast<Map>(object[trueInt(args[0])]);
        case 'getBoolean':
          return cast<bool>(object[trueInt(args[0])]);
        case 'getNumber':
          return trueNum(object[trueInt(args[0])]);
        case 'getInteger':
          return trueInt(object[trueInt(args[0])]);
        case 'getString':
          return cast<String>(object[trueInt(args[0])]);
        case 'getColor':
          return parseColor(object[trueInt(args[0])]);
        case 'getUrl':
          return parseUrl(object[trueInt(args[0])]);
      }
    }
  } else if (object is Map) {
    if (property is FunctionToken) {
      final args = property.arguments
          .map((e) => guard(() => e.run(context), null))
          .toList();
      switch (property.identifier) {
        case 'containsKey':
          return object.containsKey(cast<String>(args[0]));
        case 'getArray':
          return cast<List>(getMapProp(object, args));
        case 'getDict':
          return cast<Map>(getMapProp(object, args));
        case 'getBoolean':
          return cast<bool>(getMapProp(object, args));
        case 'getNumber':
          return trueNum(getMapProp(object, args));
        case 'getInteger':
          return trueInt(getMapProp(object, args));
        case 'getString':
          return cast<String>(getMapProp(object, args));
        case 'getUrl':
          return parseUrl(getMapProp(object, args));
        case 'getColor':
          return parseColor(getMapProp(object, args));
        case 'isEmpty':
          if (args.isNotEmpty) {
            throw 'Function has no matching overload for given argument';
          }
          return object.isEmpty;
      }
    }
  }
}
