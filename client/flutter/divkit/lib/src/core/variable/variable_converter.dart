import 'package:divkit/divkit.dart';

extension PassDivVariable on DivVariable {
  /// Creating a reactive variable from raw data.
  DivVariableModel get pass => map(
        booleanVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseBool,
        ),
        colorVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseColor,
        ),
        integerVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseInt,
        ),
        numberVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseDouble,
        ),
        stringVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseStr,
        ),
        urlVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseUri,
        ),
        arrayVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseList,
        ),
        dictVariable: (data) => DivVariableModel(
          name: data.name,
          value: data.value,
          safeParse: safeParseMap,
        ),
      );
}
