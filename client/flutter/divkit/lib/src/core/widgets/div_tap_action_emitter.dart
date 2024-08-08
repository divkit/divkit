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

  static DivTapActionModel value(
    BuildContext context,
    DivTapActionData data,
  ) {
    List<DivActionModel> a = [];
    for (final action in data.actions) {
      final rAction = action.value();
      if (rAction.enabled) {
        a.add(rAction);
      }
    }

    List<DivActionModel> la = [];
    for (final action in data.longtapActions) {
      final rAction = action.value();
      if (rAction.enabled) {
        la.add(rAction);
      }
    }

    final animationName = data.actionAnimation?.name.value!;

    return DivTapActionModel(
      actions: a,
      longtapActions: la,
      enabled: a.isNotEmpty || la.isNotEmpty,
      enabledAnimation: !(animationName == DivAnimationName.noAnimation),
    );
  }

  static Stream<DivTapActionModel> from(
    BuildContext context,
    DivTapActionData data,
  ) {
    final variables = watch<DivContext>(context)!.variableManager;

    return variables.watch<DivTapActionModel>((context) async {
      List<DivActionModel> a = [];
      for (final action in data.actions) {
        final rAction = await action.resolve(
          context: context,
        );
        if (rAction.enabled) {
          a.add(rAction);
        }
      }

      List<DivActionModel> la = [];
      for (final action in data.longtapActions) {
        final rAction = await action.resolve(
          context: context,
        );
        if (rAction.enabled) {
          la.add(rAction);
        }
      }

      final animationName =
          await data.actionAnimation?.name.resolveValue(context: context);

      return DivTapActionModel(
        actions: a,
        longtapActions: la,
        enabled: a.isNotEmpty || la.isNotEmpty,
        enabledAnimation: !(animationName == DivAnimationName.noAnimation),
      );
    }).distinct();
  }

  @override
  List<Object?> get props => [
        actions,
        longtapActions,
        enabledAnimation,
      ];
}

/// A wrapper for sending actions.
class DivTapActionEmitter extends StatelessWidget {
  final DivTapActionData? data;

  final Widget child;

  const DivTapActionEmitter({
    super.key,
    this.data,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    final data = this.data;

    if (data != null) {
      return _DivTapActionEmitter(
        data: data,
        child: child,
      );
    }

    return child;
  }
}

class _DivTapActionEmitter extends StatefulWidget {
  final DivTapActionData data;

  final Widget child;

  const _DivTapActionEmitter({
    required this.data,
    required this.child,
  });

  @override
  State<_DivTapActionEmitter> createState() => _DivTapActionEmitterState();
}

class _DivTapActionEmitterState extends State<_DivTapActionEmitter> {
  DivTapActionModel? value;

  Stream<DivTapActionModel>? stream;

  @override
  void initState() {
    super.initState();
    value = DivTapActionModel.value(context, widget.data);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivTapActionModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant _DivTapActionEmitter oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivTapActionModel.value(context, widget.data);
      stream = DivTapActionModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivTapActionModel>(
        initialData: value,
        stream: stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;
            final divContext = watch<DivContext>(context)!;

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
              child: RepaintBoundary(child: widget.child),
            );
          }

          return const SizedBox.shrink();
        },
      );
}
