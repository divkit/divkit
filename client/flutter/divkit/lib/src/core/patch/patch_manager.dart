import 'package:collection/collection.dart';
import 'package:divkit/src/core/patch/patch.dart';
import 'package:divkit/src/core/protocol/div_data_provider.dart';
import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/protocol/div_patch.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart'
    hide DivPatch;
import 'package:equatable/equatable.dart';

class DefaultDivPatchManager with EquatableMixin implements DivPatchManager {
  final DivDataProvider dataProvider;

  const DefaultDivPatchManager(this.dataProvider);

  @override
  Future<bool> applyPatch(DivPatch patch) async {
    final current = dataProvider.value;
    final states = <DivDataState>[];
    for (final state in current.states) {
      try {
        final result = _update(state.div, patch, true);
        if (result != null) {
          states.add(state.copyWith(div: result));
        }
      } catch (e, st) {
        logger.error("[div-patch]", error: e, stackTrace: st);
        return false;
      }
    }
    dataProvider.update(current.copyWith(states: states));
    return true;
  }

  /// Apply [patch] changes to single [div], throws Exception if try change to multiple items.
  /// [root] - If true, not support deletion via null.
  Div? _updateSingle(Div div, DivPatch patch, [bool root = false]) {
    for (var change in patch.changes) {
      if (div.value.id == change.id) {
        if (!root && change.items == null) {
          return null;
        } else if (change.items?.length == 1) {
          return change.items![0];
        } else {
          final message =
              "Patch with multiple items, but awaits single: ${change.id}";
          if (patch.mode == DivPatchMode.transactional) {
            throw Exception(message);
          } else {
            logger.warning(message);
          }
        }
      }
    }
    return div;
  }

  static List<T>? _toList<T>(T? obj) => obj == null ? null : [obj];

  /// Replace [div] to items form [patch] changes in parent witch support multiple items.
  List<Div>? _updateMultiple(Div div, DivPatch patch) {
    for (var change in patch.changes) {
      if (div.value.id == change.id) {
        return change.items;
      }
    }
    return _toList(_update(div, patch));
  }

  /// Distribution of updates strategy depending on the type of div.
  Div? _update(Div div, DivPatch patch, [bool root = false]) => _updateSingle(
        div.maybeMap(
          divContainer: (v) => Div.divContainer(_updateContainer(v, patch)),
          divGallery: (v) => Div.divGallery(_updateGallery(v, patch)),
          divGrid: (v) => Div.divGrid(_updateGrid(v, patch)),
          divPager: (v) => Div.divPager(_updatePager(v, patch)),
          divState: (v) => Div.divState(_updateState(v, patch)),
          divTabs: (v) => Div.divTabs(_updateTabs(v, patch)),
          orElse: () => div,
        ),
        patch,
        root,
      );

  DivContainer _updateContainer(DivContainer div, DivPatch patch) =>
      div.copyWith(
        items: () =>
            div.items
                ?.map((e) => _updateMultiple(e, patch))
                .whereNotNull()
                .expand((e) => e)
                .toList() ??
            [],
      );

  DivGallery _updateGallery(DivGallery div, DivPatch patch) => div.copyWith(
        items: () =>
            div.items
                ?.map((e) => _updateMultiple(e, patch))
                .whereNotNull()
                .expand((e) => e)
                .toList() ??
            [],
      );

  DivGrid _updateGrid(DivGrid div, DivPatch patch) => div.copyWith(
        items: () =>
            div.items
                ?.map((e) => _updateMultiple(e, patch))
                .whereNotNull()
                .expand((e) => e)
                .toList() ??
            [],
      );

  DivPager _updatePager(DivPager div, DivPatch patch) => div.copyWith(
        items: () =>
            div.items
                ?.map((e) => _updateMultiple(e, patch))
                .whereNotNull()
                .expand((e) => e)
                .toList() ??
            [],
      );

  DivState _updateState(DivState div, DivPatch patch) => div.copyWith(
        states: div.states
            .map(
              (e) => e.div != null
                  ? e.copyWith(div: () => _update(e.div!, patch))
                  : e,
            )
            .toList(),
      );

  DivTabs _updateTabs(DivTabs div, DivPatch patch) => div.copyWith(
        items: div.items
            .map((e) => e.copyWith(div: _update(e.div, patch)))
            .toList(),
      );

  @override
  List<Object?> get props => [dataProvider.value];
}
