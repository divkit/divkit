import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/input/div_input_model.dart';
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
  final key = GlobalKey();

  late final focusNode = DivFocusNode(divId: widget.data.id);

  final controller = TextEditingController();

  double _getLineHeight(TextStyle line) {
    final TextPainter textPainter = TextPainter(
      text: TextSpan(text: '', style: line),
      maxLines: 1,
      textDirection: TextDirection.ltr,
    )..layout(minWidth: 0, maxWidth: double.infinity);
    return textPainter.height;
  }

  DivInputModel? value;

  Stream<DivInputModel>? stream;

  @override
  void initState() {
    super.initState();
    value = DivInputModel.value(context, widget.data, controller);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivInputModel.from(context, widget.data, controller);
  }

  @override
  void didUpdateWidget(covariant DivInputWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivInputModel.value(context, widget.data, controller);
      stream = DivInputModel.from(context, widget.data, controller);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivInputModel>(
          initialData: value,
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;
              final divContext = watch<DivContext>(context)!;

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
    value = null;
    stream = null;
    super.dispose();
  }
}
