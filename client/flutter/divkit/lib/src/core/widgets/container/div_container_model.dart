import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/content_alignment.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivContainerModel with EquatableMixin {
  final List<Widget> children;
  final ContentAlignment contentAlignment;

  const DivContainerModel({
    required this.contentAlignment,
    this.children = const [],
  });

  @override
  List<Object?> get props => [
        children,
        contentAlignment,
      ];
}

extension DivContainerBinder on DivContainer {
  DivContainerModel bind(BuildContext context) {
    final children = items?.map((e) => DivWidget(e)).toList();
    return DivContainerModel(
      contentAlignment: DivContentAlignmentConverter(
        orientation,
        contentAlignmentVertical,
        contentAlignmentHorizontal,
        layoutMode,
      ).convert(),
      children: children ?? [],
    );
  }
}
