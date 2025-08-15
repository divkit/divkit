import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:divkit/src/utils/tap_builder.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivTapActionData {
  final List<DivAction> actions;
  final List<DivAction> longtapActions;
  final DivAnimation? actionAnimation;

  DivTapActionData({
    this.actionAnimation,
    DivAction? action,
    List<DivAction>? actions,
    List<DivAction>? longtapActions,
  })  : actions = action != null
            ? ((actions ?? const []) + [action])
            : (actions ?? const []),
        longtapActions = longtapActions ?? [];
}

class DivTapActionModel with EquatableMixin {
  final List<DivActionModel> actions;
  final List<DivActionModel> longtapActions;
  final bool enabled;
  final bool enabledAnimation;

  const DivTapActionModel({
    required this.actions,
    required this.longtapActions,
    required this.enabled,
    required this.enabledAnimation,
  });

  @override
  List<Object?> get props => [
        actions,
        longtapActions,
        enabledAnimation,
      ];
}

extension DivTapActionDataConverter on DivTapActionData {
  DivTapActionModel resolve(BuildContext context) {
    final divContext = read<DivContext>(context)!;
    final variables = divContext.variables;

    final animationName = actionAnimation?.name.resolve(variables);
    return DivTapActionModel(
      actions: actions.map((e) => e.resolve(variables)).toList(),
      longtapActions: longtapActions.map((e) => e.resolve(variables)).toList(),
      enabled: actions.isNotEmpty || longtapActions.isNotEmpty,
      enabledAnimation: !(animationName == DivAnimationName.noAnimation),
    );
  }
}

class DivTapActionEmitter extends StatefulWidget {
  final DivTapActionData? data;
  final Widget child;

  const DivTapActionEmitter({
    this.data,
    super.key,
    required this.child,
  });

  @override
  State<DivTapActionEmitter> createState() => _DivTapActionEmitterState();
}

/// A wrapper for sending actions.
class _DivTapActionEmitterState extends State<DivTapActionEmitter> {
  DivTapActionModel? value;
  Stream<DivTapActionModel?>? stream;

  @override
  void initState() {
    super.initState();
    value = widget.data?.resolve(context);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= watch<DivContext>(context)!.variableManager.watch(
          (values) => widget.data?.resolve(context),
        );
  }

  @override
  void didUpdateWidget(covariant DivTapActionEmitter oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = widget.data?.resolve(context);
      stream = watch<DivContext>(context)!.variableManager.watch(
            (values) => widget.data?.resolve(context),
          );
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivTapActionModel?>(
        initialData: value,
        stream: stream,
        builder: (context, snapshot) {
          final divContext = watch<DivContext>(context)!;
          final model = snapshot.data;

          if (model == null) {
            return widget.child;
          }

          return TapBuilder(
            enabled: model.enabled,
            onTap: () async {
              for (final action in model.actions) {
                await action.execute(divContext);
              }
            },
            onLongPress: () async {
              for (final action in model.longtapActions) {
                await action.execute(divContext);
              }
            },
            builder: (context, pressed, hovered, child) {
              if (!model.enabledAnimation) {
                return child;
              }
              double opacity = 1.0;
              if (pressed) {
                opacity = 0.6;
              } else if (hovered) {
                opacity = 0.8;
              }
              return AnimatedOpacity(
                opacity: opacity,
                duration: const Duration(milliseconds: 100),
                child: child,
              );
            },
            child: widget.child,
          );
        },
      );

  @override
  void dispose() {
    value = null;
    stream = null;
    super.dispose();
  }
}
