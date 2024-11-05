import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/text/div_text_model.dart';
import 'package:divkit/src/core/widgets/text/utils/div_text_range_model.dart';
import 'package:divkit/src/utils/mapping_widget.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';

class DivTextWidget extends DivMappingWidget<DivText, DivTextModel> {
  const DivTextWidget(
    super.data, {
    super.key,
  });

  Future<void> _onTapRanges(
      BuildContext context, DivTextRangeModel item) async {
    final divContext = watch<DivContext>(context);
    if (divContext != null) {
      final actions = item.optionModel.actions;
      for (DivActionModel action in actions) {
        await action.execute(divContext);
      }
    }
  }

  @override
  DivTextModel value(BuildContext context) {
    final divContext = read<DivContext>(context)!;
    data.resolve(divContext.variables);
    return data.bind(context);
  }

  @override
  Stream<DivTextModel> stream(BuildContext context) {
    final divContext = watch<DivContext>(context)!;
    return divContext.variableManager.watch((values) {
      data.resolve(values);
      return data.bind(context);
    });
  }

  @override
  Widget build(BuildContext context, DivTextModel model) {
    final textWidget = Material(
      type: MaterialType.transparency,
      child: model.ranges.isEmpty
          ? Text(
              model.text,
              style: model.style,
              textAlign: model.textAlign,
              maxLines: model.maxLines,
            )
          : RichText(
              overflow: TextOverflow.ellipsis,
              textAlign: model.textAlign ?? TextAlign.start,
              maxLines: model.maxLines,
              text: TextSpan(
                style: model.style,
                children: model.ranges
                    .map(
                      (item) => TextSpan(
                        text: item.text,
                        style: item.optionModel.style,
                        recognizer: TapGestureRecognizer()
                          ..onTap =
                              () async => await _onTapRanges(context, item),
                      ),
                    )
                    .toList(),
              ),
            ),
    );

    final alignment = model.alignment;
    Widget alignmentText = textWidget;

    if (alignment != null) {
      alignmentText = Align(
        widthFactor: 1,
        heightFactor: 1,
        alignment: alignment,
        child: textWidget,
      );
    }

    return DivBaseWidget(
      data: data,
      tapActionData: DivTapActionData(
        action: data.action,
        actions: data.actions,
        longtapActions: data.longtapActions,
        actionAnimation: data.actionAnimation,
      ),
      child: alignmentText,
    );
  }
}
