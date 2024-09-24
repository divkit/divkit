import 'package:collection/collection.dart';
import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/text/utils/div_range_helper.dart';
import 'package:divkit/src/core/widgets/text/utils/div_text_range_model.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

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

  static DivTextModel? value(
    BuildContext context,
    DivText data,
  ) {
    try {
      final scalingModel = read<DivScalingModel>(context);
      final textScale = scalingModel?.textScale ?? 1;
      final viewScale = scalingModel?.viewScale ?? 1;

      final alignment = PassDivAlignment(
        data.textAlignmentVertical,
        data.textAlignmentHorizontal,
      );

      final autoEllipsize = data.autoEllipsize?.value ?? false;
      final fontFamily = data.fontFamily?.requireValue;

      final fontSize = data.fontSize.requireValue.toDouble() *
          data.fontSizeUnit.requireValue.asPx *
          textScale;

      final lineHeight = data.lineHeight?.requireValue.toDouble();

      final letterSpacing = data.letterSpacing.requireValue;
      final underline = data.underline.requireValue;

      final strike = data.strike.requireValue;
      final shadow = data.textShadow?.valueShadow(viewScale: viewScale);

      final fontWeightValue = data.fontWeightValue?.requireValue;
      FontWeight? fontWeight = FontWeight.values.firstWhereOrNull(
        (element) => element.value == fontWeightValue,
      );
      fontWeight ??= data.fontWeight.passValue();

      final linesStyleList = [
        underline,
        strike,
      ];

      final fontProvider = read<DivFontProvider>(context)!;
      final fontAsset = fontProvider.resolve(fontFamily);

      final style = TextStyle(
        fontSize: fontSize,
        package: fontAsset.package,
        fontFamilyFallback: fontAsset.fontFamilyFallback,
        fontFamily: fontAsset.fontFamily,
        shadows: shadow != null ? [shadow] : null,
        height: lineHeight != null
            ? (lineHeight * data.fontSizeUnit.requireValue.asPx) *
                viewScale /
                fontSize
            : null,
        color: data.textColor.value!,
        fontWeight: fontWeight,
        letterSpacing: letterSpacing,
        decoration: TextDecoration.combine(
          [
            underline.asUnderline,
            strike.asLineThrough,
          ],
        ),
        decorationColor: data.textColor.requireValue,
        overflow: autoEllipsize ? TextOverflow.ellipsis : null,
      );

      final text = data.text.requireValue;

      final rangesList = DivRangeHelper.getRangeItemsSync(
        text,
        data.ranges ?? [],
        style,
        linesStyleList,
        viewScale,
        fontProvider,
      );

      return DivTextModel(
        ranges: rangesList,
        text: text,
        style: style,
        textBoxAlignment: alignment.valueAlignmentGeometry(),
        textAlign: alignment.valueTextAlign(),
        maxLines: data.maxLines?.requireValue,
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

  static Stream<DivTextModel> from(
    BuildContext context,
    DivText data,
  ) {
    final divContext = watch<DivContext>(context)!;
    final variables = divContext.variableManager;

    final divScalingModel = watch<DivScalingModel>(context);
    final textScale = divScalingModel?.textScale ?? 1;
    final viewScale = divScalingModel?.viewScale ?? 1;

    final fontProvider = watch<DivFontProvider>(context)!;

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

        final fontFamily = (await data.fontFamily?.resolveValue(
          context: context,
        ));

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

        final letterSpacing = await data.letterSpacing.resolveValue(
          context: context,
        );

        final linesStyleList = [
          underline,
          strike,
        ];

        final fontWeightValue = await data.fontWeightValue?.resolveValue(
          context: context,
        );
        FontWeight? fontWeight = FontWeight.values.firstWhereOrNull(
          (element) => element.value == fontWeightValue,
        );
        fontWeight ??= await data.fontWeight.resolve(context: context);

        final shadow = await data.textShadow?.resolveShadow(
          context: context,
          viewScale: viewScale,
        );

        final fontAsset = fontProvider.resolve(fontFamily);
        final style = TextStyle(
          package: fontAsset.package,
          fontFamily: fontAsset.fontFamily,
          fontFamilyFallback: fontAsset.fontFamilyFallback,
          fontSize: fontSize,
          letterSpacing: letterSpacing,
          shadows: shadow != null ? [shadow] : null,
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
          fontWeight: fontWeight,
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
          viewScale,
          fontProvider,
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
