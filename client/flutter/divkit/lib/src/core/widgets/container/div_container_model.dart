import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/widgets/div_widget.dart';
import 'package:divkit/src/generated_sources/div_container.dart';
import 'package:divkit/src/utils/content_alignment_converters.dart';
import 'package:divkit/src/utils/converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivContainerModel with EquatableMixin {
  final List<Widget> children;
  final ContentAlignment contentAlignment;
  final double? aspectRatio;

  const DivContainerModel({
    required this.contentAlignment,
    this.children = const [],
    this.aspectRatio,
  });

  static Stream<DivContainerModel> from(
    BuildContext context,
    DivContainer data,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;

    return variables.watch<DivContainerModel>((context) async {
      final contentAlignment = await PassDivContentAlignment(
        data.orientation,
        data.contentAlignmentVertical,
        data.contentAlignmentHorizontal,
        data.layoutMode,
      ).resolve(
        context: context,
      );

      return DivContainerModel(
        contentAlignment: contentAlignment,
        aspectRatio: await data.aspect?.resolve(
          context: context,
        ),
        children: data.items
                ?.map(
                  (e) => DivWidget(e),
                )
                .toList(growable: false) ??
            [],
      );
    }).distinct(); // The widget is redrawn when the model changes.
  }

  @override
  List<Object?> get props => [
        children,
        contentAlignment,
        aspectRatio,
      ];
}
