import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivPagerModel with EquatableMixin {
  final List<Widget> children;
  final Axis orientation;

  const DivPagerModel({
    required this.children,
    required this.orientation,
  });

  static DivPagerModel? value(
    BuildContext context,
    DivPager data,
  ) {
    try {
      final rawOrientation = data.orientation.requireValue;
      final orientation = _convertOrientation(rawOrientation);

      final children =
          data.items?.map((e) => DivWidget(e)).toList(growable: false) ?? [];

      return DivPagerModel(
        children: children,
        orientation: orientation,
      );
    } catch (e, st) {
      logger.warning(
        'Expression cache is corrupted! Instant rendering is not available for div',
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  static Stream<DivPagerModel> from(
    BuildContext context,
    DivPager data,
    PageController controller,
    ValueGetter<int> currentPage,
  ) {
    final variables = watch<DivContext>(context)!.variableManager;
    final id = data.id;
    if (id != null && variables.context.current[id] != currentPage()) {
      variables.putVariable(id, currentPage());
    }
    return variables.watch<DivPagerModel>((context) async {
      final length = data.items?.length;
      if (id != null && length != null) {
        if (variables.context.current[id] < 0) {
          variables.updateVariable(id, 0);
        }
        if (variables.context.current[id] > length - 1) {
          variables.updateVariable(id, length - 1);
        }
        if (variables.context.current[id] != currentPage()) {
          controller.animateToPage(
            variables.context.current[id],
            duration: const Duration(milliseconds: 200),
            curve: Curves.linear,
          );
        }
      }

      final rawOrientation = await data.orientation.resolveValue(
        context: context,
      );
      final orientation = _convertOrientation(rawOrientation);

      final children =
          data.items?.map((e) => DivWidget(e)).toList(growable: false) ?? [];

      return DivPagerModel(
        children: children,
        orientation: orientation,
      );
    }).distinct();
  }

  @override
  List<Object?> get props => [
        orientation,
      ];

  static Axis _convertOrientation(DivPagerOrientation orientation) {
    switch (orientation) {
      case DivPagerOrientation.horizontal:
        return Axis.horizontal;
      case DivPagerOrientation.vertical:
        return Axis.vertical;
    }
  }
}
