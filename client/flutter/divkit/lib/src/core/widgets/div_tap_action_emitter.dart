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

extension DivTapActionDataBinder on DivTapActionData {
  DivTapActionModel bind(BuildContext context) {
    final animationName = actionAnimation?.name.value;
    return DivTapActionModel(
      actions: actions.map((e) => e.convert()).toList(),
      longtapActions: longtapActions.map((e) => e.convert()).toList(),
      enabled: actions.isNotEmpty || longtapActions.isNotEmpty,
      enabledAnimation: !(animationName == DivAnimationName.noAnimation),
    );
  }
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
      final divContext = watch<DivContext>(context)!;
      final model = data.bind(context);

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
        child: child,
      );
    }

    return child;
  }
}
