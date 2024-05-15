import 'package:divkit/src/core/action/action.dart';
import 'package:divkit/src/core/action/action_converter.dart';
import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart' as dto;
import 'package:divkit/src/utils/provider.dart';
import 'package:divkit/src/utils/tap_builder.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivTapActionModel with EquatableMixin {
  final List<DivAction> actions;
  final bool enabled;

  const DivTapActionModel({
    required this.actions,
    required this.enabled,
  });

  static Stream<DivTapActionModel> from(
    BuildContext context,
    List<dto.DivAction> actions,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;

    return variables.watch<DivTapActionModel>((context) async {
      List<DivAction> result = [];
      for (final action in actions) {
        final rAction = await action.resolve(
          context: context,
        );
        if (rAction.enabled) {
          result.add(rAction);
        }
      }

      return DivTapActionModel(
        actions: result,
        enabled: result.isNotEmpty,
      );
    }).distinct();
  }

  @override
  List<Object?> get props => [
        actions,
      ];
}

/// A wrapper for sending actions.
class DivTapActionEmitter extends StatefulWidget {
  final List<dto.DivAction> actions;

  final Widget child;

  const DivTapActionEmitter({
    super.key,
    required this.actions,
    required this.child,
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

    stream ??= DivTapActionModel.from(context, widget.actions);
  }

  @override
  void didUpdateWidget(covariant DivTapActionEmitter oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.actions != oldWidget.actions) {
      stream = DivTapActionModel.from(context, widget.actions);
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivTapActionModel>(
        stream: DivTapActionModel.from(context, widget.actions),
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
                builder: (context, pressed, hovered, child) {
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
