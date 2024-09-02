import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/patch/patch_manager.dart';
import 'package:divkit/src/core/data/data_provider.dart';
import 'package:divkit/src/core/variable/variable_manager.dart';
import 'package:flutter_test/flutter_test.dart';

Future<DivData> applyPatch(DivData original, DivPatchModel patch) async {
  final divContext = DivRootContext()
    ..variableManager = DefaultDivVariableManager(
      storage: DefaultDivVariableStorage(),
    )
    ..dataProvider = DefaultDivDataProvider(original);
  final manager = DefaultDivPatchManager(divContext);
  await manager.applyPatch(patch);

  return divContext.dataProvider!.value;
}

void main() async {
  group('Patching data test', () {
    test('When no suitable changes does nothing', () async {
      final original = data(
        container(
          [divWithId('some_div')],
        ),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = original;
      expect(patched, result);
    });
    test('When replacing root div replaces div', () async {
      final original = data(divWithId('div_to_replace'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(newText1);
      expect(patched, result);
    });
    test('When replacing div with different type replaces div', () async {
      final original = data(divWithId('div_to_replace'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [separator],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(separator);
      expect(patched, result);
    });
    // Why don't we allow the structure to be patched from the base?
    test('When deleting root div does nothing', () async {
      final original = data(divWithId('div_to_delete'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = original;
      expect(patched, result);
    });
    test('When replacing root div with multiple divs does nothing', () async {
      final original = data(divWithId('div_to_replace'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = original;
      expect(patched, result);
    });
    test('When replacing item in container replaces div', () async {
      final original = data(
        container([
          separator,
          divWithId('div_to_replace'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        container([
          separator,
          newText1,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When replacing items in container replaces divs', () async {
      final original = data(
        container([
          separator,
          divWithId('div_to_replace'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        container([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When deleting item in container removes div', () async {
      final original = data(
        container([
          separator,
          divWithId('div_to_delete'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        container([
          separator,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When have multiple changes applies all', () async {
      final original = data(
        container([
          separator,
          divWithId('div_to_replace'),
          divWithId('div_to_delete'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        container([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When replacing items in gallery replaces divs', () async {
      final original = data(
        gallery([
          separator,
          divWithId('div_to_replace'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        gallery([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When deleting item in gallery removes div', () async {
      final original = data(
        gallery([
          separator,
          divWithId('div_to_delete'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        gallery([
          separator,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When replacing and deleting items in grid applies all', () async {
      final original = data(
        grid([
          separator,
          divWithId('div_to_replace'),
          divWithId('div_to_delete'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        grid([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When replacing and deleting items in pager applies all', () async {
      final original = data(
        pager([
          separator,
          divWithId('div_to_replace'),
          divWithId('div_to_delete'),
          separator,
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        pager([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(patched, result);
    });
    test('When replacing state div replaces div', () async {
      final original = data(
        state([
          divWithId('div_to_replace'),
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        state([
          newText1,
        ]),
      );
      expect(patched, result);
    });
    test('When deleting state div keeps empty state', () async {
      final original = data(
        state([
          divWithId('div_to_delete'),
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        state([
          null,
        ]),
      );

      expect(patched, result);
    });
    test('When replacing state div with multiple divs does nothing', () async {
      final original = data(
        state([
          divWithId('div_to_replace'),
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = original;
      expect(patched, result);
    });
    test('When replacing tabs div replaces div', () async {
      final original = data(
        tabs([
          divWithId('div_to_replace'),
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1],
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = data(
        tabs([
          newText1,
        ]),
      );
      expect(patched, result);
    });
    test('When deleting tabs div does nothing', () async {
      final original = data(
        tabs([
          divWithId('div_to_delete'),
        ]),
      );
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final patched = await applyPatch(original, patch);

      final result = original;
      expect(patched, result);
    });
  });
}

DivData data(Div div) => DivData(
      logId: '',
      states: [
        DivDataState(
          stateId: 0,
          div: div,
        ),
      ],
    );

Div container(List<Div>? items) => Div.divContainer(
      DivContainer(items: items),
    );

Div gallery(List<Div>? items) => Div.divGallery(
      DivGallery(items: items),
    );

Div grid(List<Div>? items) => Div.divGrid(
      DivGrid(items: items, columnCount: const ValueExpression(1)),
    );

Div pager(List<Div>? items) => Div.divPager(
      DivPager(
        items: items,
        layoutMode: const DivPagerLayoutMode.divPageSize(
          DivPageSize(pageWidth: DivPercentageSize(value: ValueExpression(10))),
        ),
      ),
    );

Div state(List<Div?> items) => Div.divState(
      DivState(
        states: items
            .map(
              (e) => DivStateState(div: e, stateId: ''),
            )
            .toList(),
      ),
    );

Div tabs(List<Div> items) => Div.divTabs(
      DivTabs(
        items: items
            .map(
              (e) => DivTabsItem(div: e, title: const ValueExpression('')),
            )
            .toList(),
      ),
    );

Div divWithId([String id = 'div_to_replace']) => Div.divText(
      DivText(
        id: id,
        text: const ValueExpression('Old text'),
      ),
    );

const newText1 = Div.divText(
  DivText(
    text: ValueExpression('New text 1'),
  ),
);

const newText2 = Div.divText(
  DivText(
    text: ValueExpression('New text 2'),
  ),
);

const separator = Div.divSeparator(
  DivSeparator(),
);
