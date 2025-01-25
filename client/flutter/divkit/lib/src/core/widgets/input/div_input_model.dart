import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/alignment.dart';
import 'package:divkit/src/core/converters/converters.dart';
import 'package:divkit/src/core/converters/text_specific.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

class DivInputModel with EquatableMixin {
  final TextStyle textStyle;
  final TextStyle hintStyle;
  final String? hintText;
  final TextInputType? keyboardType;
  final List<TextInputFormatter>? inputFormatters;
  final int? maxLines;
  final bool obscureText;
  final void Function()? onForwardFocus;
  final List<DivActionModel> onBlurActions;
  final List<DivActionModel> onFocusActions;
  final TextAlign textAlign;
  final TextAlignVertical textAlignVertical;

  const DivInputModel({
    required this.textStyle,
    required this.hintStyle,
    required this.keyboardType,
    required this.inputFormatters,
    required this.onBlurActions,
    required this.onFocusActions,
    required this.textAlignVertical,
    required this.textAlign,
    required this.obscureText,
    this.maxLines,
    this.hintText,
    this.onForwardFocus,
  });

  @override
  List<Object?> get props => [
        textStyle,
        hintStyle,
        hintText,
        maxLines,
        keyboardType,
        onForwardFocus,
        onBlurActions,
        onFocusActions,
        textAlign,
        textAlignVertical,
        obscureText,
      ];
}

extension DivInputConverter on DivInput {
  DivInputModel resolve(
    BuildContext context,
    TextEditingController controller,
  ) {
    final divContext = read<DivContext>(context)!;

    final viewScale = divContext.scale.view;
    final textScale = divContext.scale.text;
    final variables = divContext.variables;

    final styleUnit = fontSizeUnit.resolve(variables).asPx;
    final lineHeight = this.lineHeight?.resolve(variables);
    final fontFamily = this.fontFamily?.resolve(variables);
    final fontSize = this.fontSize.resolve(variables).toDouble() * textScale;

    final fontAsset = divContext.fontProvider.resolve(fontFamily);

    final style = TextStyle(
      package: fontAsset.package,
      fontFamily: fontAsset.fontFamily,
      fontFamilyFallback: fontAsset.fontFamilyFallback,
      fontSize: fontSize * styleUnit,
      color: textColor.resolve(variables),
      height: lineHeight != null ? lineHeight * viewScale / fontSize : null,
      fontWeight: fontWeight.resolve(variables).convert(),
    );

    final hintStyle = style.copyWith(
      color: hintColor.resolve(variables),
    );

    final keyboardType = this.keyboardType.resolve(variables);
    if (variables.current[textVariable] != controller.text) {
      controller.text = variables.current[textVariable] ?? '';
    }

    final newFocusNodeId = focus?.nextFocusIds?.forward?.resolve(variables);

    final alignment = DivTextAlignmentConverter(
      textAlignmentVertical,
      textAlignmentHorizontal,
    );

    final obscureText = keyboardType == DivInputKeyboardType.password;

    return DivInputModel(
      textStyle: style,
      hintStyle: hintStyle,
      hintText: hintText?.resolve(variables),
      maxLines: obscureText ? 1 : maxVisibleLines?.resolve(variables),
      obscureText: obscureText,
      keyboardType: keyboardType.map(
        singleLineText: () => TextInputType.text,
        multiLineText: () => TextInputType.multiline,
        phone: () => TextInputType.phone,
        number: () => TextInputType.number,
        email: () => TextInputType.emailAddress,
        uri: () => TextInputType.url,
        password: () => null,
      ),
      onForwardFocus: newFocusNodeId != null
          ? () => _onForwardFocus(divContext.buildContext, newFocusNodeId)
          : null,
      onBlurActions:
          focus?.onBlur?.map((e) => e.resolve(variables)).toList() ?? [],
      onFocusActions:
          focus?.onFocus?.map((e) => e.resolve(variables)).toList() ?? [],
      textAlign: alignment.resolveHorizontal(variables),
      textAlignVertical: alignment.resolveVertical(variables),
    );
  }

  static void _onForwardFocus(BuildContext context, String newFocusNodeId) {
    if (context.mounted) {
      final focusNode = FocusScope.of(context).getById(newFocusNodeId);
      if (focusNode != null) {
        focusNode.requestFocus();
      }
    }
  }
}
