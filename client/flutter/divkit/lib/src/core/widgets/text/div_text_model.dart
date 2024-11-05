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

extension DivTextBinder on DivText {
  DivTextModel bind(BuildContext context) {
    final divContext = read<DivContext>(context)!;

    final textScale = divContext.scale.text;
    final viewScale = divContext.scale.view;

    final alignment = DivAlignmentConverter(
      textAlignmentVertical,
      textAlignmentHorizontal,
    ).convert();

    final textAlign = DivTextAlignmentConverter(
      textAlignmentVertical,
      textAlignmentHorizontal,
    ).convertHorizontal();

    final autoEllipsize = this.autoEllipsize?.value ?? false;
    final fontFamily = this.fontFamily?.value;

    final fontSize =
        this.fontSize.value.toDouble() * fontSizeUnit.value.asPx * textScale;

    final lineHeight = this.lineHeight?.value.toDouble();

    final letterSpacing = this.letterSpacing.value;
    final underline = this.underline.value;

    final strike = this.strike.value;
    final shadow = textShadow?.convertShadow(viewScale: viewScale);

    final fontWeightValue = this.fontWeightValue?.value;
    FontWeight? fontWeight = FontWeight.values.firstWhereOrNull(
      (w) => w.value == fontWeightValue,
    );
    fontWeight ??= this.fontWeight.value.convert();

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
          ? (lineHeight * fontSizeUnit.value.asPx) * viewScale / fontSize
          : null,
      color: textColor.value,
      fontWeight: fontWeight,
      letterSpacing: letterSpacing,
      decoration: TextDecoration.combine(
        [
          underline.asUnderline,
          strike.asLineThrough,
        ],
      ),
      decorationColor: textColor.value,
      overflow: autoEllipsize ? TextOverflow.ellipsis : null,
    );

    final text = this.text.value;

    final rangesList = DivRangeHelper.getRangeItems(
      text,
      ranges ?? [],
      style,
      linesStyleList,
      viewScale,
      divContext.fontProvider,
    );

    final maxLines = this.maxLines?.value;

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
