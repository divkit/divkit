import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/content_alignment_converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivContainerModel with EquatableMixin {
  final List<Widget> children;
  final ContentAlignment contentAlignment;

  const DivContainerModel({
    required this.contentAlignment,
    this.children = const [],
  });

  static DivContainerModel? value(
    BuildContext context,
    DivContainer data,
  ) {
    try {
      final contentAlignment = PassDivContentAlignment(
        data.orientation,
        data.contentAlignmentVertical,
        data.contentAlignmentHorizontal,
        data.layoutMode,
      ).requireValue;

      return DivContainerModel(
        contentAlignment: contentAlignment,
        children: data.items
                ?.map(
                  (e) => DivWidget(e),
                )
                .toList(growable: false) ??
            [],
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

  static Stream<DivContainerModel> from(
    BuildContext context,
    DivContainer data,
  ) {
    final variables = watch<DivContext>(context)!.variableManager;

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
      ];
}
