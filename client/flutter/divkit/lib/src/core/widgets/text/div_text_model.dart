import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/widgets/text/utils/div_text_range_model.dart';
import 'package:divkit/src/generated_sources/div_text.dart';
import 'package:divkit/src/utils/converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:divkit/src/core/widgets/text/utils/div_range_helper.dart';

class DivTextModel with EquatableMixin {
  final TextStyle? style;
  final String text;
  final AlignmentGeometry? textBoxAlignment;
  final TextAlign? textAlign;
  final int? maxLines;
  final List<DivTextRangeModel> ranges;

  const DivTextModel({
    required this.text,
    this.textAlign,
    this.textBoxAlignment,
    this.style,
    this.maxLines,
    this.ranges = const [],
  });

  static Stream<DivTextModel> from(
    BuildContext context,
    DivText data,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;

    final divScalingModel = DivKitProvider.watch<DivScalingModel>(context);
    final textScale = divScalingModel?.textScale ?? 1;
    final viewScale = divScalingModel?.viewScale ?? 1;

    return variables.watch<DivTextModel>(
      (context) async {
        final alignment = PassDivAlignment(
          data.textAlignmentVertical,
          data.textAlignmentHorizontal,
        );

        final autoEllipsize = await data.autoEllipsize?.resolveValue(
              context: context,
            ) ??
            false;

        final fontSize = (await data.fontSize.resolveValue(
              context: context,
            ))
                .toDouble() *
            (await data.fontSizeUnit.resolveValue(
              context: context,
            ))
                .asPx *
            textScale;

        final lineHeight = (await data.lineHeight?.resolveValue(
          context: context,
        ))
            ?.toDouble();

        final underline = await data.underline.resolveValue(
          context: context,
        );

        final strike = await data.strike.resolveValue(
          context: context,
        );

        final linesStyleList = [
          underline,
          strike,
        ];

        final style = TextStyle(
          fontSize: fontSize,
          height: lineHeight != null
              ? (lineHeight *
                      (await data.fontSizeUnit.resolveValue(
                        context: context,
                      ))
                          .asPx) *
                  viewScale /
                  fontSize
              : null,
          color: await data.textColor.resolveValue(
            context: context,
          ),
          fontWeight: await data.fontWeight.resolve(
            context: context,
          ),
          decoration: TextDecoration.combine(
            [
              underline.asUnderline,
              strike.asLineThrough,
            ],
          ),
          decorationColor: await data.textColor.resolveValue(
            context: context,
          ),
          overflow: autoEllipsize ? TextOverflow.ellipsis : null,
        );

        final text = await data.text.resolveValue(
          context: context,
        );

        final rangesList = await DivRangeHelper.getRangeItems(
          text,
          data.ranges ?? [],
          context,
          style,
          linesStyleList,
        );

        return DivTextModel(
          ranges: rangesList,
          text: text,
          style: style,
          textBoxAlignment: await alignment.resolve(
            context: context,
          ),
          textAlign: await alignment.resolveTextAlign(
            context: context,
          ),
          maxLines: await data.maxLines?.resolveValue(
            context: context,
          ),
        );
      },
    ).distinct(); // The widget is redrawn when the model changes.
  }

  @override
  List<Object?> get props => [
        text,
        style,
        textBoxAlignment,
        textAlign,
        maxLines,
        ranges,
      ];
}
