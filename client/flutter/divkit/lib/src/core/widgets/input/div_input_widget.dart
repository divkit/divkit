import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/widgets/base/div_base_widget.dart';
import 'package:divkit/src/core/widgets/input/div_input_model.dart';
import 'package:divkit/src/generated_sources/div_input.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';

class DivInputWidget extends StatefulWidget {
  final DivInput data;

  const DivInputWidget(
    this.data, {
    super.key,
  });

  @override
  State<StatefulWidget> createState() => _DivInputWidget();
}

class _DivInputWidget extends State<DivInputWidget> {
  // ToDo: Optimize repeated calculations on the same context.
  // The model itself is not long-lived, so you need to keep the stream in the state?
  Stream<DivInputModel>? stream;

  late final DivFocusNode focusNode = DivFocusNode(divId: widget.data.id);

  final key = GlobalKey();

  final TextEditingController controller = TextEditingController();

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivInputModel.from(context, widget.data, controller);
  }

  @override
  void didUpdateWidget(covariant DivInputWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivInputModel.from(context, widget.data, controller);
    }
  }

  double _getLineHeight(TextStyle line) {
    final TextPainter textPainter = TextPainter(
      text: TextSpan(text: '', style: line),
      maxLines: 1,
      textDirection: TextDirection.ltr,
    )..layout(minWidth: 0, maxWidth: double.infinity);
    return textPainter.height;
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivInputModel>(
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;
              final divContext = DivKitProvider.watch<DivContext>(context)!;

              return Material(
                type: MaterialType.transparency,
                child: Focus.withExternalFocusNode(
                  focusNode: focusNode,
                  onFocusChange: (gain) async {
                    if (gain) {
                      for (final action in model.onFocusActions) {
                        action.execute(divContext);
                      }
                    } else {
                      for (final action in model.onBlurActions) {
                        action.execute(divContext);
                      }
                    }
                  },
                  child: Baseline(
                    baseline: _getLineHeight(model.textStyle) / 2,
                    baselineType: TextBaseline.alphabetic,
                    child: TextField(
                      key: key,
                      focusNode: focusNode.child,
                      style: model.textStyle,
                      decoration: InputDecoration(
                        hintStyle: model.hintStyle,
                        hintText: model.hintText,
                        border: InputBorder.none,
                        contentPadding: EdgeInsets.zero,
                        constraints: BoxConstraints(
                          maxHeight: (model.maxLines ?? double.infinity) *
                              _getLineHeight(model.textStyle),
                        ),
                        isDense: true,
                        isCollapsed: true,
                      ),
                      textAlignVertical: model.textAlignVertical,
                      textAlign: model.textAlign,
                      maxLines: model.maxLines,
                      obscureText: model.obscureText,
                      keyboardType: model.keyboardType,
                      inputFormatters: model.inputFormatters,
                      controller: controller,
                      onEditingComplete: model.onForwardFocus,
                      textInputAction: model.onForwardFocus != null
                          ? TextInputAction.next
                          : null,
                    ),
                  ),
                ),
              );
            }

            return const SizedBox.shrink();
          },
        ),
      );

  @override
  void dispose() {
    controller.dispose();
    focusNode.dispose();
    stream = null;
    super.dispose();
  }
}
