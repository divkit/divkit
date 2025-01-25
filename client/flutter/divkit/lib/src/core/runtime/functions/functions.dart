import 'package:divkit/src/core/runtime/core.dart';
import 'package:divkit/src/core/runtime/entities.dart';
import 'package:divkit/src/core/runtime/functions/base_math_functions.dart';
import 'package:divkit/src/core/runtime/functions/collection_functions.dart';
import 'package:divkit/src/core/runtime/functions/color_functions.dart';
import 'package:divkit/src/core/runtime/functions/datetime_functions.dart';
import 'package:divkit/src/core/runtime/functions/duration_functions.dart';
import 'package:divkit/src/core/runtime/functions/format_functions.dart';
import 'package:divkit/src/core/runtime/functions/legacy_collection_functions.dart';
import 'package:divkit/src/core/runtime/functions/object_functions.dart';
import 'package:divkit/src/core/runtime/functions/storage_functions.dart';

final allDefaultFunctions = <String, RuntimeFunction>{
  ...objectFunctions,
  ...baseMathFunctions,
  ...collectionFunctions,
  ...legacyCollectionFunctions,
  ...colorFunctions,
  ...datetimeFunctions,
  ...durationFunctions,
  ...formatFunctions,
};

dynamic stubFunctions(
  FunctionToken function,
  Map<String, dynamic>? context,
) =>
    null;

dynamic defaultFunctions(
  FunctionToken function,
  Map<String, dynamic> context,
) {
  final identifier = function.identifier;
  final args = function.arguments.map((e) => e.run(context)).toList();
  return allDefaultFunctions[identifier]?.call(args) ??
      storageFunctions[identifier]?.call(args, context);
}
