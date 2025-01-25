import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/alignment.dart';
import 'package:divkit/src/core/converters/converters.dart';
import 'package:divkit/src/core/converters/text_specific.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
<<<<<<< HEAD
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:flutter/services.dart';
=======
>>>>>>> c3bef97794d086982d98b2c37095d5be68853a6e

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

<<<<<<< HEAD
  static Stream<DivInputModel> from(
    BuildContext buildContext,
    DivInput data,
    TextEditingController controller,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(buildContext)!.variableManager;

    final divScalingModel = DivKitProvider.watch<DivScalingModel>(buildContext);
    final viewScale = divScalingModel?.viewScale ?? 1;
    final textScale = divScalingModel?.textScale ?? 1;

    controller.addListener(() {
      if (variables.context.current[data.textVariable] != controller.text) {
        variables.updateVariable(data.textVariable, controller.text);
      }
    });

    return variables.watch<DivInputModel>((context) async {
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

      final inputFormatters = keyboardType == DivInputKeyboardType.number
          ? [FilteringTextInputFormatter.digitsOnly]
          : <TextInputFormatter>[];

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
        inputFormatters: inputFormatters,
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

=======
>>>>>>> c3bef97794d086982d98b2c37095d5be68853a6e
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

extension DivInputBinder on DivInput {
  DivInputModel bind(
    BuildContext context,
    TextEditingController controller,
  ) {
    final divContext = read<DivContext>(context)!;

    final viewScale = divContext.scale.view;
    final textScale = divContext.scale.text;
    final variables = divContext.variableManager;

    final styleUnit = fontSizeUnit.value.asPx;
    final lineHeight = this.lineHeight?.value;
    final fontFamily = this.fontFamily?.value;
    final fontSize = this.fontSize.value.toDouble() * textScale;

    final fontAsset = divContext.fontProvider.resolve(fontFamily);

    final style = TextStyle(
      package: fontAsset.package,
      fontFamily: fontAsset.fontFamily,
      fontFamilyFallback: fontAsset.fontFamilyFallback,
      fontSize: fontSize * styleUnit,
      color: textColor.value,
      height: lineHeight != null ? lineHeight * viewScale / fontSize : null,
      fontWeight: fontWeight.value.convert(),
    );

    final hintStyle = style.copyWith(
      color: hintColor.value,
    );

    final keyboardType = this.keyboardType.value;
    if (variables.context.current[textVariable] != controller.text) {
      controller.text = variables.context.current[textVariable] ?? '';
    }

    final newFocusNodeId = focus?.nextFocusIds?.forward?.value;

    final alignment = DivTextAlignmentConverter(
      textAlignmentVertical,
      textAlignmentHorizontal,
    );

    final obscureText = keyboardType == DivInputKeyboardType.password;

    return DivInputModel(
      textStyle: style,
      hintStyle: hintStyle,
      hintText: hintText?.value,
      maxLines: obscureText ? 1 : maxVisibleLines?.value,
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
      onBlurActions: focus?.onBlur?.map((e) => e.convert()).toList() ?? [],
      onFocusActions: focus?.onFocus?.map((e) => e.convert()).toList() ?? [],
      textAlign: alignment.convertHorizontal(),
      textAlignVertical: alignment.convertVertical(),
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
