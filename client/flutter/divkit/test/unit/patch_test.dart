import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/patch/patch_manager.dart';
import 'package:divkit/src/core/data/data_provider.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('Patching data test', () {
    test('When no suitable changes does nothing', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = original;
      expect(provider.value, result);
    });
    test('When replacing root div replaces div', () {
      final original = data(divWithId('div_to_replace'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1],
          ),
        ],
      );

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(newText1);
      expect(provider.value, result);
    });
    test('When replacing div with different type replaces div', () {
      final original = data(divWithId('div_to_replace'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [separator],
          ),
        ],
      );

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(separator);
      expect(provider.value, result);
    });
    // Why don't we allow the structure to be patched from the base?
    test('When deleting root div does nothing', () {
      final original = data(divWithId('div_to_delete'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_delete",
            items: null,
          ),
        ],
      );

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = original;
      expect(provider.value, result);
    });
    test('When replacing root div with multiple divs does nothing', () {
      final original = data(divWithId('div_to_replace'));
      const patch = DivPatchModel(
        changes: [
          DivPatchChange(
            id: "div_to_replace",
            items: [newText1, newText2],
          ),
        ],
      );

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = original;
      expect(provider.value, result);
    });
    test('When replacing item in container replaces div', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        container([
          separator,
          newText1,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When replacing items in container replaces divs', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        container([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When deleting item in container removes div', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        container([
          separator,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When have multiple changes applies all', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        container([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When replacing items in gallery replaces divs', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        gallery([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When deleting item in gallery removes div', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        gallery([
          separator,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When replacing and deleting items in grid applies all', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        grid([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When replacing and deleting items in pager applies all', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        pager([
          separator,
          newText1,
          newText2,
          separator,
        ]),
      );
      expect(provider.value, result);
    });
    test('When replacing state div replaces div', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        state([
          newText1,
        ]),
      );
      expect(provider.value, result);
    });
    test('When deleting state div keeps empty state', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        state([
          null,
        ]),
      );

      expect(provider.value, result);
    });
    test('When replacing state div with multiple divs does nothing', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = original;
      expect(provider.value, result);
    });
    test('When replacing tabs div replaces div', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = data(
        tabs([
          newText1,
        ]),
      );
      expect(provider.value, result);
    });
    test('When deleting tabs div does nothing', () {
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

      final provider = DefaultDivDataProvider(original);
      final manager = DefaultDivPatchManager(provider);
      manager.applyPatch(patch);

      final result = original;
      expect(provider.value, result);
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
