import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/widgets/base/div_base_widget.dart';
import 'package:divkit/src/core/widgets/text/div_text_model.dart';
import 'package:divkit/src/generated_sources/div_text.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:divkit/src/core/action/action.dart';

import 'utils/div_text_range_model.dart';

class DivTextWidget extends StatefulWidget {
  final DivText data;

  const DivTextWidget(
    this.data, {
    super.key,
  });

  @override
  State<DivTextWidget> createState() => _DivTextWidgetState();
}

class _DivTextWidgetState extends State<DivTextWidget> {
  // ToDo: Optimize repeated calculations on the same context.
  // The model itself is not long-lived, so you need to keep the stream in the state?
  Stream<DivTextModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    stream ??= DivTextModel.from(
      context,
      widget.data,
    );
  }

  @override
  void didUpdateWidget(covariant DivTextWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.data != oldWidget.data) {
      stream = DivTextModel.from(
        context,
        widget.data,
      );
    }
  }

  Future<void> _onTapRanges(DivTextRangeModel item) async {
    final divContext = DivKitProvider.watch<DivContext>(
      context,
    );
    if (divContext != null) {
      final actions = item.optionModel.actions;
      for (DivAction action in actions) {
        await action.execute(divContext);
      }
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        action: widget.data.action,
        actions: widget.data.actions,
        longtapActions: widget.data.longtapActions,
        actionAnimation: widget.data.actionAnimation,
        child: StreamBuilder<DivTextModel>(
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;
              final textBoxAlignment = model.textBoxAlignment;

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
                                    ..onTap = () async {
                                      await _onTapRanges(item);
                                    },
                                ),
                              )
                              .toList(),
                        ),
                      ),
              );

              if (textBoxAlignment != null) {
                return Align(
                  widthFactor: 1,
                  heightFactor: 1,
                  alignment: textBoxAlignment,
                  child: textWidget,
                );
              }

              return textWidget;
            }

            return const SizedBox.shrink();
          },
        ),
      );

  @override
  void dispose() {
    stream = null;
    super.dispose();
  }
}
