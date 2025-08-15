import 'package:collection/collection.dart';
import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/alignment.dart';
import 'package:divkit/src/core/converters/converters.dart';
import 'package:divkit/src/core/converters/text_specific.dart';
import 'package:divkit/src/core/widgets/text/utils/div_range_helper.dart';
import 'package:divkit/src/core/widgets/text/utils/div_text_range_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

class DivTextModel with EquatableMixin {
  final TextStyle? style;
  final String text;
  final AlignmentGeometry? alignment;
  final TextAlign? textAlign;
  final int? maxLines;
  final List<DivTextRangeModel> ranges;

  const DivTextModel({
    required this.text,
    this.textAlign,
    this.alignment,
    this.style,
    this.maxLines,
    this.ranges = const [],
  });

  @override
  List<Object?> get props => [
        text,
        style,
        alignment,
        textAlign,
        maxLines,
        ranges,
      ];
}

extension DivTextConverter on DivText {
  DivTextModel resolve(BuildContext context) {
    final divContext = read<DivContext>(context)!;

    final variables = divContext.variables;
    final textScale = divContext.scale.text;
    final viewScale = divContext.scale.view;

    final alignment = DivAlignmentConverter(
      textAlignmentVertical,
      textAlignmentHorizontal,
    ).resolve(variables);

    final textAlign = DivTextAlignmentConverter(
      textAlignmentVertical,
      textAlignmentHorizontal,
    ).resolveHorizontal(variables);

    final autoEllipsize = this.autoEllipsize?.resolve(variables) ?? false;
    final fontFamily = this.fontFamily?.resolve(variables);

    final fontSize = this.fontSize.resolve(variables).toDouble() *
        fontSizeUnit.resolve(variables).asPx *
        textScale;

    final lineHeight = this.lineHeight?.resolve(variables).toDouble();

    final letterSpacing = this.letterSpacing.resolve(variables);
    final underline = this.underline.resolve(variables);

    final strike = this.strike.resolve(variables);
    final shadow = textShadow?.resolveShadow(variables, viewScale: viewScale);

    final fontWeightValue = this.fontWeightValue?.resolve(variables);
    FontWeight? fontWeight = FontWeight.values.firstWhereOrNull(
      (w) => w.value == fontWeightValue,
    );
    fontWeight ??= this.fontWeight.resolve(variables).convert();

    final linesStyleList = [
      underline,
      strike,
    ];

    final fontAsset = divContext.fontProvider.resolve(fontFamily);

    final style = TextStyle(
      fontSize: fontSize,
      package: fontAsset.package,
      fontFamilyFallback: fontAsset.fontFamilyFallback,
      fontFamily: fontAsset.fontFamily,
      shadows: shadow != null ? [shadow] : null,
      height: lineHeight != null
          ? (lineHeight * fontSizeUnit.resolve(variables).asPx) *
              viewScale /
              fontSize
          : null,
      color: textColor.resolve(variables),
      fontWeight: fontWeight,
      letterSpacing: letterSpacing,
      decoration: TextDecoration.combine(
        [
          underline.asUnderline,
          strike.asLineThrough,
        ],
      ),
      decorationColor: textColor.resolve(variables),
      overflow: autoEllipsize ? TextOverflow.ellipsis : null,
    );

    final text = this.text.resolve(variables);

    final rangesList = DivRangeHelper.getRangeItems(
      variables,
      text,
      ranges ?? [],
      style,
      linesStyleList,
      viewScale,
      divContext.fontProvider,
    );

    final maxLines = this.maxLines?.resolve(variables);

    return DivTextModel(
      ranges: rangesList,
      text: text,
      style: style,
      alignment: alignment,
      textAlign: textAlign,
      maxLines: maxLines,
    );
  }
}
