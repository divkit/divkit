import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivPagerModel with EquatableMixin {
  final Axis orientation;
  final List<Widget> children;

  const DivPagerModel({
    required this.children,
    required this.orientation,
  });

  @override
  List<Object?> get props => [
        orientation,
      ];
}

extension DivPagerConvereter on DivPager {
  DivPagerModel init(BuildContext context) {
    final divContext = read<DivContext>(context)!;
    final variables = divContext.variables;

    final orientation =
        _convertOrientation(this.orientation.resolve(variables));
    final children = items?.map((e) => DivWidget(e)).toList() ?? [];
    return DivPagerModel(
      children: children,
      orientation: orientation,
    );
  }

  DivPagerModel resolve(
    BuildContext context,
    PageController controller,
    ValueGetter<int> currentPage,
  ) {
    final divContext = read<DivContext>(context)!;

    final id = this.id;
    final variables = divContext.variables;

    final length = items?.length;
    if (id != null && length != null) {
      if (variables.current[id] != currentPage()) {
        controller.animateToPage(
          variables.current[id],
          duration: const Duration(milliseconds: 200),
          curve: Curves.linear,
        );
      }
    }

    final orientation =
        _convertOrientation(this.orientation.resolve(variables));
    final children = items?.map((e) => DivWidget(e)).toList();
    return DivPagerModel(
      orientation: orientation,
      children: children ?? [],
    );
  }

  static Axis _convertOrientation(DivPagerOrientation orientation) {
    switch (orientation) {
      case DivPagerOrientation.horizontal:
        return Axis.horizontal;
      case DivPagerOrientation.vertical:
        return Axis.vertical;
    }
  }
}
