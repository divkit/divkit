import 'package:divkit/src/core/variable/variable.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;
import 'package:divkit/src/utils/parsing_utils.dart';

extension PassDivVariable on dto.DivVariable {
  /// Creating a reactive variable from raw data.
  DivVariable get pass => map(
        booleanVariable: (data) => DivVariable(
          name: data.name,
          value: data.value,
          safeParse: safeParseBool,
        ),
        colorVariable: (data) => DivVariable(
          name: data.name,
          value: data.value,
          safeParse: safeParseColor,
        ),
        integerVariable: (data) => DivVariable(
          name: data.name,
          value: data.value,
          safeParse: safeParseInt,
        ),
        numberVariable: (data) => DivVariable(
          name: data.name,
          value: data.value,
          safeParse: safeParseDouble,
        ),
        stringVariable: (data) => DivVariable(
          name: data.name,
          value: data.value,
          safeParse: safeParseStr,
        ),
        urlVariable: (data) => DivVariable(
          name: data.name,
          value: data.value,
          safeParse: safeParseUri,
        ),
        arrayVariable: (data) => DivVariable(
          name: data.name,
          value: data.value,
          safeParse: safeParseList,
        ),
        dictVariable: (data) => DivVariable(
          name: data.name,
          value: data.value['value'],
          safeParse: safeParseMap,
        ),
      );
}
