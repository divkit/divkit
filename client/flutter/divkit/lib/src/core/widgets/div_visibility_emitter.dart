import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';
import 'package:visibility_detector/visibility_detector.dart';

class DivVisibilityEmitter extends StatelessWidget {
  final DivVisibility divVisibility;
  final String id;
  final List<DivVisibilityActionModel> visibilityActions;
  final Widget child;

  const DivVisibilityEmitter({
    required this.id,
    required this.divVisibility,
    required this.visibilityActions,
    required this.child,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final isVisible = divVisibility == DivVisibility.visible;
    return Visibility(
      visible: isVisible,
      // ToDo: Needs replace to something?
      // Have test error, but not checked!
      replacement: IgnorePointer(child: child),
      child: IgnorePointer(
        ignoring: !isVisible,
        child: _DivVisibilityActionEmitter(
          actions: visibilityActions,
          id: id,
          isVisible: isVisible,
          child: child,
        ),
      ),
    );
  }
}

class _DivVisibilityActionEmitter extends StatefulWidget {
  final List<DivVisibilityActionModel> actions;
  final Widget child;
  final bool isVisible;
  final String id;

  const _DivVisibilityActionEmitter({
    required this.child,
    required this.actions,
    required this.id,
    required this.isVisible,
  });

  @override
  State<_DivVisibilityActionEmitter> createState() =>
      _DivVisibilityActionEmitterState();
}

class _DivVisibilityActionEmitterState
    extends State<_DivVisibilityActionEmitter> {
  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final divContext = watch<DivContext>(context)!;
    if (widget.actions.isNotEmpty) {
      divContext.visibilityActionManager.addOrUpdateActions(
        widget.actions,
        widget.id,
      );
    }
  }

  @override
  void didUpdateWidget(covariant _DivVisibilityActionEmitter oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.actions != widget.actions && widget.actions.isNotEmpty) {
      final divContext = watch<DivContext>(context)!;
      if (widget.actions.isNotEmpty) {
        divContext.visibilityActionManager.addOrUpdateActions(
          widget.actions,
          widget.id,
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final divContext = watch<DivContext>(context)!;
    final needVisibilityDetector =
        divContext.visibilityActionManager.hasNoEndActions(widget.id) &&
            widget.isVisible;

    if (!widget.isVisible) {
      divContext.visibilityActionManager.stopAllWaitIfNeed(widget.id);
    }
    if (!needVisibilityDetector) {
      return widget.child;
    }

    return VisibilityDetector(
      key: ValueKey(widget.id),
      child: widget.child,
      onVisibilityChanged: (VisibilityInfo info) {
        divContext.visibilityActionManager.updateActionsStateIfNeed(
          (info.visibleFraction * 100).toInt(),
          divContext,
          widget.id,
        );
      },
    );
  }
}
