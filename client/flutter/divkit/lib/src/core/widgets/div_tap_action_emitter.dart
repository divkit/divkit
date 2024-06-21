import 'package:divkit/src/core/action/action.dart';
import 'package:divkit/src/core/action/action_converter.dart';
import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/generated_sources/div_animation.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;
import 'package:divkit/src/utils/provider.dart';
import 'package:divkit/src/utils/tap_builder.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivTapActionModel with EquatableMixin {
  final List<DivAction> actions;
  final List<DivAction> longtapActions;
  final bool enabled;
  final bool enabledAnimation;

  const DivTapActionModel({
    required this.actions,
    required this.longtapActions,
    required this.enabled,
    required this.enabledAnimation,
  });

  static Stream<DivTapActionModel> from(
    BuildContext context,
    List<dto.DivAction> actions,
    List<dto.DivAction> longtapActions,
    dto.DivAnimation? divAnimation,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;

    return variables.watch<DivTapActionModel>((context) async {
      List<DivAction> a = [];
      for (final action in actions) {
        final rAction = await action.resolve(
          context: context,
        );
        if (rAction.enabled) {
          a.add(rAction);
        }
      }

      List<DivAction> la = [];
      for (final action in longtapActions) {
        final rAction = await action.resolve(
          context: context,
        );
        if (rAction.enabled) {
          la.add(rAction);
        }
      }

      final animationName =
          await divAnimation?.name.resolveValue(context: context);

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
class DivTapActionEmitter extends StatefulWidget {
  final List<dto.DivAction> actions;
  final List<dto.DivAction> longtapActions;
  final DivAnimation? actionAnimation;

  final Widget child;

  const DivTapActionEmitter({
    super.key,
    required this.actions,
    required this.longtapActions,
    required this.child,
    required this.actionAnimation,
  });

  @override
  State<DivTapActionEmitter> createState() => _DivTapActionEmitterState();
}

class _DivTapActionEmitterState extends State<DivTapActionEmitter> {
  // ToDo: Optimize repeated calculations on the same context.
  // The model itself is not long-lived, so you need to keep the stream in the state?
  Stream<DivTapActionModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivTapActionModel.from(
      context,
      widget.actions,
      widget.longtapActions,
      widget.actionAnimation,
    );
  }

  @override
  void didUpdateWidget(covariant DivTapActionEmitter oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.actions != oldWidget.actions) {
      stream = DivTapActionModel.from(
        context,
        widget.actions,
        widget.longtapActions,
        widget.actionAnimation,
      );
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivTapActionModel>(
        stream: stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;
            final divContext = DivKitProvider.watch<DivContext>(context)!;

            return RepaintBoundary(
              child: TapBuilder(
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
              ),
            );
          }

          return const SizedBox.shrink();
        },
      );
}
