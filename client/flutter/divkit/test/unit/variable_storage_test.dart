import 'package:divkit/src/core/variable/variable.dart';
import 'package:divkit/src/core/variable/variable_manager.dart';
import 'package:divkit/src/core/variable/variable_storage.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:flutter_test/flutter_test.dart';

Map<String, dynamic> initValues = {
  "string_var": "value",
  "int_var": 100,
  "number_var": 123.34,
  "bool_var": true,
  "url_var": safeParseUri("https://test.url"),
};

Map<String, dynamic> initValuesOuter = {
  "outer_string_var": "outer value",
  "outer_int_var": 200,
};

Map<String, dynamic> afterUpdate(Map<String, dynamic>? update) => {
      ...initValues,
      ...(update ?? {}),
    };

Map<String, dynamic> afterUpdateOuter(Map<String, dynamic>? update) => {
      ...initValuesOuter,
      ...initValues,
      ...(update ?? {}),
    };

List<DivVariableModel> createNewVars() => [
      DivVariableModel(
        name: "string_var",
        value: "value",
        safeParse: safeParseStr,
      ),
      DivVariableModel(
        name: "int_var",
        value: 100,
        safeParse: safeParseInt,
      ),
      DivVariableModel(
        name: "number_var",
        value: 123.34,
        safeParse: safeParseDouble,
      ),
      DivVariableModel(
        name: "bool_var",
        value: true,
        safeParse: safeParseBool,
      ),
      DivVariableModel(
        name: "url_var",
        value: safeParseUri("https://test.url"),
        safeParse: safeParseUri,
      ),
    ];

List<DivVariableModel> createNewVarsOuter() => [
      DivVariableModel(
        name: "outer_string_var",
        value: "outer value",
        safeParse: safeParseStr,
      ),
      DivVariableModel(
        name: "outer_int_var",
        value: 200,
        safeParse: safeParseInt,
      ),
    ];

void main() {
  group(
    'VariableStorage implementation test',
    () {
      test(
        'put variable',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );
          variables.putVariable("new_var", "new value");
          expect(
            afterUpdate({"new_var": "new value"}),
            variables.context.current,
          );
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'rewrite variable',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );
          variables.putVariable("string_var", "new value");
          expect(
            afterUpdate({"string_var": "new value"}),
            variables.context.current,
          );
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'GetsValue',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );
          expect("value", variables.context.current["string_var"]);
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'ReturnsNullForUnknownVariable',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );
          expect(null, variables.context.current["unknown_var"]);
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'UpdatesStringVariable',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );
          variables.updateVariable("string_var", "new value");
          expect("new value", variables.context.current["string_var"]);
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'UpdatesNumberVariable',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );
          variables.updateVariable("number_var", 234.567);
          expect(234.567, variables.context.current["number_var"]);
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'DoesNothingForUnknownVariable',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(),
          );
          variables.updateVariable("number_var", "new value");
          expect({}, variables.context.current);
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'ClearsStorage',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );
          await variables.dispose();
          expect({}, variables.context.current);
          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'CheckStream',
        () async {
          final variables = DefaultDivVariableManager(
            storage: DefaultDivVariableStorage(variables: createNewVars()),
          );

          final stream =
              variables.watch((context) => context.current).asBroadcastStream();

          final first = await stream.first;
          expect(afterUpdate(null), first);

          variables.updateVariable("int_var", 0);

          final second = await stream.first;
          expect(afterUpdate({"int_var": 0}), second);

          await variables.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
    },
  );

  group(
    'Two VariableStorages interaction tests',
    () {
      test(
        'ReturnsValueFromOuterStorage',
        () async {
          final outerStorage =
              DefaultDivVariableStorage(variables: createNewVarsOuter());
          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );

          final variables = DefaultDivVariableManager(storage: storage);

          expect("outer value", variables.context.current["outer_string_var"]);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'DoesNotUpdateShadowedVariable',
        () async {
          final outerStorage = DefaultDivVariableStorage(
            variables: [
              DivVariableModel(
                name: "string_var",
                value: "outer value",
                safeParse: safeParseStr,
              ),
            ],
          );
          final outerVariables =
              DefaultDivVariableManager(storage: outerStorage);

          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );
          final variables = DefaultDivVariableManager(storage: storage);

          variables.updateVariable("string_var", "new value");

          expect("new value", variables.context.current["string_var"]);
          expect("outer value", outerVariables.context.current["string_var"]);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'DoesNotClearOuterStorage',
        () async {
          final outerStorage = DefaultDivVariableStorage(
            variables: createNewVarsOuter(),
          );
          final outerVariables =
              DefaultDivVariableManager(storage: outerStorage);

          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );
          final variables = DefaultDivVariableManager(storage: storage);

          await variables.dispose();

          expect({}, variables.context.current);
          expect(initValuesOuter, outerVariables.context.current);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'CheckStorageChanges',
        () async {
          final outerStorage = DefaultDivVariableStorage(
            variables: createNewVarsOuter(),
          );
          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );

          final variables = DefaultDivVariableManager(storage: storage);

          final stream =
              variables.watch((context) => context.current).asBroadcastStream();

          final first = await stream.first;
          expect(afterUpdateOuter(null), first);

          variables.updateVariable("outer_int_var", 0);

          final second = await stream.first;
          expect(afterUpdateOuter({"outer_int_var": 0}), second);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'CheckOuterStorageChanges',
        () async {
          final outerStorage = DefaultDivVariableStorage(
            variables: createNewVarsOuter(),
          );
          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );

          final variables = DefaultDivVariableManager(storage: storage);

          outerStorage.put(DivVariableModel(name: "global", value: 0));

          expect(afterUpdateOuter({"global": 0}), variables.context.current);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'CheckUpdateOuterStorageAndListenInnerStream',
        () async {
          final outerStorage = DefaultDivVariableStorage(
            variables: createNewVarsOuter(),
          );
          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );

          final variables = DefaultDivVariableManager(storage: storage);

          final stream =
              variables.watch((context) => context.current).asBroadcastStream();

          final first = await stream.first;
          expect(afterUpdateOuter(null), first);

          outerStorage
              .update(DivVariableModel(name: "outer_int_var", value: 0));

          final second = await stream.first;
          expect(afterUpdateOuter({"outer_int_var": 0}), second);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'CheckCreateVarInStorageAndListenInnerStream',
        () async {
          final outerStorage = DefaultDivVariableStorage(
            variables: createNewVarsOuter(),
          );
          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );

          final variables = DefaultDivVariableManager(storage: storage);

          final stream =
              variables.watch((context) => context.current).asBroadcastStream();

          final first = await stream.first;
          expect(afterUpdateOuter(null), first);

          variables.putVariable("new_int_var", 0);

          final second = await stream.first;
          expect(afterUpdateOuter({"new_int_var": 0}), second);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
      test(
        'CheckCreateVarInOuterStorageAndListenInnerStream',
        () async {
          final outerStorage = DefaultDivVariableStorage(
            variables: createNewVarsOuter(),
          );
          final storage = DefaultDivVariableStorage(
            inheritedStorage: outerStorage,
            variables: createNewVars(),
          );

          final variables = DefaultDivVariableManager(storage: storage);

          final stream =
              variables.watch((context) => context.current).asBroadcastStream();

          final first = await stream.first;
          expect(afterUpdateOuter(null), first);

          outerStorage.put(DivVariableModel(name: "new_int_var", value: 0));

          final second = await stream.first;
          expect(afterUpdateOuter({"new_int_var": 0}), second);

          await variables.dispose();
          await outerStorage.dispose();
        },
        timeout: const Timeout(Duration(seconds: 1)),
      );
    },
  );
}
