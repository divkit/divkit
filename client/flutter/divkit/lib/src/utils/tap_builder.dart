import 'package:flutter/widgets.dart';

/// Builder callback to create a custom touch animation depending on the state.
typedef InteractiveWidgetBuilder = Widget Function(
  BuildContext context,
  bool pressed,
  bool hovered,
  Widget child,
);

/// Simple builder for custom animations that support [pressed] and [hovered] state.
class TapBuilder extends StatefulWidget {
  /// Callback on the action of tapping.
  final VoidCallback? onTap;

  /// Callback on the action of double tapping.
  final VoidCallback? onDoubleTap;

  /// Callback on the action of long pressing.
  final VoidCallback? onLongPress;

  /// You can maintain the state of taping and hovering.
  final InteractiveWidgetBuilder builder;

  final Widget child;

  /// Shows whether the tap builder isn't available for operation.
  ///
  /// The default is true.
  final bool enabled;

  const TapBuilder({
    required this.builder,
    required this.child,
    this.onTap,
    this.onDoubleTap,
    this.onLongPress,
    this.enabled = true,
    super.key,
  });

  @override
  State<TapBuilder> createState() => _TapBuilderState();
}

class _TapBuilderState extends State<TapBuilder> {
  bool pressed = false;
  bool hovered = false;

  @override
  Widget build(BuildContext context) => widget.enabled
      ? MouseRegion(
          onEnter: (_) => setState(() => hovered = true),
          onExit: (_) => setState(() => hovered = false),
          child: GestureDetector(
            onTapDown: (_) => setState(() => pressed = true),
            onTapUp: (_) => setState(() => pressed = false),
            onTapCancel: () => setState(() => pressed = false),
            onTap: widget.onTap,
            onDoubleTap: widget.onDoubleTap,
            onLongPress: widget.onLongPress,
            child: widget.builder(context, pressed, hovered, widget.child),
          ),
        )
      : widget.child;
}
