import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

class DivInputModel with EquatableMixin {
  final TextStyle textStyle;
  final TextStyle hintStyle;
  final String? hintText;
  final TextInputType? keyboardType;
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
    required this.onBlurActions,
    required this.onFocusActions,
    required this.textAlignVertical,
    required this.textAlign,
    required this.obscureText,
    this.maxLines,
    this.hintText,
    this.onForwardFocus,
  });

  static DivInputModel? value(
    BuildContext buildContext,
    DivInput data,
    TextEditingController controller,
  ) {
    try {
      final divScalingModel = read<DivScalingModel>(buildContext);
      final viewScale = divScalingModel?.viewScale ?? 1;
      final textScale = divScalingModel?.textScale ?? 1;

      final variables = read<DivContext>(buildContext)!.variableManager;

      controller.addListener(() {
        if (variables.context.current[data.textVariable] != controller.text) {
          variables.updateVariable(data.textVariable, controller.text);
        }
      });

      final styleUnit = data.fontSizeUnit.requireValue.asPx;
      final lineHeight = data.lineHeight?.requireValue;
      final fontFamily = data.fontFamily?.requireValue;
      final fontSize = data.fontSize.value!.toDouble() * textScale;

      final style = TextStyle(
        fontFamily: fontFamily,
        fontSize: fontSize * styleUnit,
        color: data.textColor.value!,
        height: lineHeight != null ? lineHeight * viewScale / fontSize : null,
        fontWeight: data.fontWeight.passValue(),
      );

      final hintStyle = style.copyWith(
        color: data.hintColor.requireValue,
      );

      final keyboardType = data.keyboardType.requireValue;
      if (variables.context.current[data.textVariable] != controller.text) {
        controller.text = variables.context.current[data.textVariable] ?? '';
      }

      final newFocusNodeId = data.focus?.nextFocusIds?.forward?.requireValue;

      final alignment = PassDivTextAlignment(
        data.textAlignmentVertical,
        data.textAlignmentHorizontal,
      );

      final obscureText = keyboardType == DivInputKeyboardType.password;

      return DivInputModel(
        textStyle: style,
        hintStyle: hintStyle,
        hintText: data.hintText?.requireValue,
        maxLines: obscureText ? 1 : data.maxVisibleLines?.requireValue,
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
            ? () => _onForwardFocus(buildContext, newFocusNodeId)
            : null,
        onBlurActions: data.focus?.valueOnBlurActions() ?? [],
        onFocusActions: data.focus?.valueOnFocusActions() ?? [],
        textAlign: alignment.valueTextAlign(),
        textAlignVertical: alignment.valueTextAlignVertical(),
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

  static Stream<DivInputModel> from(
    BuildContext buildContext,
    DivInput data,
    TextEditingController controller,
  ) {
    final variables = watch<DivContext>(buildContext)!.variableManager;

    final divScalingModel = watch<DivScalingModel>(buildContext);
    final viewScale = divScalingModel?.viewScale ?? 1;
    final textScale = divScalingModel?.textScale ?? 1;

    return variables.watch<DivInputModel>((context) async {
      final fontFamily = await data.fontFamily?.resolveValue(context: context);
      final styleUnit = (await data.fontSizeUnit.resolveValue(
        context: context,
      ))
          .asPx;
      final lineHeight = await data.lineHeight?.resolveValue(
        context: context,
      );
      final fontSize = (await data.fontSize.resolveValue(
            context: context,
          ))
              .toDouble() *
          textScale;

      final style = TextStyle(
        fontFamily: fontFamily,
        fontSize: fontSize * styleUnit,
        color: await data.textColor.resolveValue(
          context: context,
        ),
        height: lineHeight != null ? lineHeight * viewScale / fontSize : null,
        fontWeight: await data.fontWeight.resolve(
          context: context,
        ),
      );

      final hintStyle = style.copyWith(
        color: await data.hintColor.resolveValue(
          context: context,
        ),
      );

      final keyboardType = await data.keyboardType.resolveValue(
        context: context,
      );
      if (variables.context.current[data.textVariable] != controller.text) {
        controller.text = variables.context.current[data.textVariable] ?? '';
      }

      final newFocusNodeId =
          await data.focus?.nextFocusIds?.forward?.resolveValue(
        context: context,
      );

      final alignment = PassDivTextAlignment(
        data.textAlignmentVertical,
        data.textAlignmentHorizontal,
      );

      final obscureText = keyboardType == DivInputKeyboardType.password;

      return DivInputModel(
        textStyle: style,
        hintStyle: hintStyle,
        hintText: await data.hintText?.resolveValue(
          context: context,
        ),
        maxLines: obscureText
            ? 1
            : await data.maxVisibleLines?.resolveValue(
                context: context,
              ),
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
            ? () => _onForwardFocus(buildContext, newFocusNodeId)
            : null,
        onBlurActions: await data.focus?.resolveOnBlurActions(
              context: context,
            ) ??
            [],
        onFocusActions: await data.focus?.resolveOnFocusActions(
              context: context,
            ) ??
            [],
        textAlign: await alignment.resolveTextAlign(
          context: context,
        ),
        textAlignVertical: await alignment.resolveTextAlignVertical(
          context: context,
        ),
      );
    }).distinct();
  }

  static void _onForwardFocus(BuildContext context, String newFocusNodeId) {
    if (context.mounted) {
      final focusNode = FocusScope.of(context).getById(newFocusNodeId);
      if (focusNode != null) {
        focusNode.requestFocus();
      }
    }
  }

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
