// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Accessibility settings.
class DivAccessibility extends Preloadable with EquatableMixin {
  const DivAccessibility({
    this.description,
    this.hint,
    this.mode = const ValueExpression(DivAccessibilityMode.default_),
    this.muteAfterAction = const ValueExpression(false),
    this.stateDescription,
    this.type = DivAccessibilityType.auto,
  });

  /// Element description. It is used as the main description for screen reading applications.
  final Expression<String>? description;

  /// A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
  final Expression<String>? hint;

  /// The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
  // default value: DivAccessibilityMode.default_
  final Expression<DivAccessibilityMode> mode;

  /// Mutes the screen reader sound after interacting with the element.
  // default value: false
  final Expression<bool> muteAfterAction;

  /// Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
  final Expression<String>? stateDescription;

  /// Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
  // default value: DivAccessibilityType.auto
  final DivAccessibilityType type;

  @override
  List<Object?> get props => [
        description,
        hint,
        mode,
        muteAfterAction,
        stateDescription,
        type,
      ];

  DivAccessibility copyWith({
    Expression<String>? Function()? description,
    Expression<String>? Function()? hint,
    Expression<DivAccessibilityMode>? mode,
    Expression<bool>? muteAfterAction,
    Expression<String>? Function()? stateDescription,
    DivAccessibilityType? type,
  }) =>
      DivAccessibility(
        description:
            description != null ? description.call() : this.description,
        hint: hint != null ? hint.call() : this.hint,
        mode: mode ?? this.mode,
        muteAfterAction: muteAfterAction ?? this.muteAfterAction,
        stateDescription: stateDescription != null
            ? stateDescription.call()
            : this.stateDescription,
        type: type ?? this.type,
      );

  static DivAccessibility? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivAccessibility(
        description: safeParseStrExpr(
          json['description']?.toString(),
        ),
        hint: safeParseStrExpr(
          json['hint']?.toString(),
        ),
        mode: safeParseStrEnumExpr(
          json['mode'],
          parse: DivAccessibilityMode.fromJson,
          fallback: DivAccessibilityMode.default_,
        )!,
        muteAfterAction: safeParseBoolExpr(
          json['mute_after_action'],
          fallback: false,
        )!,
        stateDescription: safeParseStrExpr(
          json['state_description']?.toString(),
        ),
        type: safeParseStrEnum(
          json['type'],
          parse: DivAccessibilityType.fromJson,
          fallback: DivAccessibilityType.auto,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivAccessibility?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivAccessibility(
        description: await safeParseStrExprAsync(
          json['description']?.toString(),
        ),
        hint: await safeParseStrExprAsync(
          json['hint']?.toString(),
        ),
        mode: (await safeParseStrEnumExprAsync(
          json['mode'],
          parse: DivAccessibilityMode.fromJson,
          fallback: DivAccessibilityMode.default_,
        ))!,
        muteAfterAction: (await safeParseBoolExprAsync(
          json['mute_after_action'],
          fallback: false,
        ))!,
        stateDescription: await safeParseStrExprAsync(
          json['state_description']?.toString(),
        ),
        type: (await safeParseStrEnumAsync(
          json['type'],
          parse: DivAccessibilityType.fromJson,
          fallback: DivAccessibilityType.auto,
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await description?.preload(context);
      await hint?.preload(context);
      await mode.preload(context);
      await muteAfterAction.preload(context);
      await stateDescription?.preload(context);
      await type.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivAccessibilityType implements Preloadable {
  none('none'),
  button('button'),
  image('image'),
  text('text'),
  editText('edit_text'),
  header('header'),
  tabBar('tab_bar'),
  list('list'),
  select('select'),
  auto('auto');

  final String value;

  const DivAccessibilityType(this.value);
  bool get isNone => this == none;

  bool get isButton => this == button;

  bool get isImage => this == image;

  bool get isText => this == text;

  bool get isEditText => this == editText;

  bool get isHeader => this == header;

  bool get isTabBar => this == tabBar;

  bool get isList => this == list;

  bool get isSelect => this == select;

  bool get isAuto => this == auto;

  T map<T>({
    required T Function() none,
    required T Function() button,
    required T Function() image,
    required T Function() text,
    required T Function() editText,
    required T Function() header,
    required T Function() tabBar,
    required T Function() list,
    required T Function() select,
    required T Function() auto,
  }) {
    switch (this) {
      case DivAccessibilityType.none:
        return none();
      case DivAccessibilityType.button:
        return button();
      case DivAccessibilityType.image:
        return image();
      case DivAccessibilityType.text:
        return text();
      case DivAccessibilityType.editText:
        return editText();
      case DivAccessibilityType.header:
        return header();
      case DivAccessibilityType.tabBar:
        return tabBar();
      case DivAccessibilityType.list:
        return list();
      case DivAccessibilityType.select:
        return select();
      case DivAccessibilityType.auto:
        return auto();
    }
  }

  T maybeMap<T>({
    T Function()? none,
    T Function()? button,
    T Function()? image,
    T Function()? text,
    T Function()? editText,
    T Function()? header,
    T Function()? tabBar,
    T Function()? list,
    T Function()? select,
    T Function()? auto,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivAccessibilityType.none:
        return none?.call() ?? orElse();
      case DivAccessibilityType.button:
        return button?.call() ?? orElse();
      case DivAccessibilityType.image:
        return image?.call() ?? orElse();
      case DivAccessibilityType.text:
        return text?.call() ?? orElse();
      case DivAccessibilityType.editText:
        return editText?.call() ?? orElse();
      case DivAccessibilityType.header:
        return header?.call() ?? orElse();
      case DivAccessibilityType.tabBar:
        return tabBar?.call() ?? orElse();
      case DivAccessibilityType.list:
        return list?.call() ?? orElse();
      case DivAccessibilityType.select:
        return select?.call() ?? orElse();
      case DivAccessibilityType.auto:
        return auto?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivAccessibilityType? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivAccessibilityType.none;
        case 'button':
          return DivAccessibilityType.button;
        case 'image':
          return DivAccessibilityType.image;
        case 'text':
          return DivAccessibilityType.text;
        case 'edit_text':
          return DivAccessibilityType.editText;
        case 'header':
          return DivAccessibilityType.header;
        case 'tab_bar':
          return DivAccessibilityType.tabBar;
        case 'list':
          return DivAccessibilityType.list;
        case 'select':
          return DivAccessibilityType.select;
        case 'auto':
          return DivAccessibilityType.auto;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivAccessibilityType?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'none':
          return DivAccessibilityType.none;
        case 'button':
          return DivAccessibilityType.button;
        case 'image':
          return DivAccessibilityType.image;
        case 'text':
          return DivAccessibilityType.text;
        case 'edit_text':
          return DivAccessibilityType.editText;
        case 'header':
          return DivAccessibilityType.header;
        case 'tab_bar':
          return DivAccessibilityType.tabBar;
        case 'list':
          return DivAccessibilityType.list;
        case 'select':
          return DivAccessibilityType.select;
        case 'auto':
          return DivAccessibilityType.auto;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

enum DivAccessibilityMode implements Preloadable {
  default_('default'),
  merge('merge'),
  exclude('exclude');

  final String value;

  const DivAccessibilityMode(this.value);
  bool get isDefault => this == default_;

  bool get isMerge => this == merge;

  bool get isExclude => this == exclude;

  T map<T>({
    required T Function() default_,
    required T Function() merge,
    required T Function() exclude,
  }) {
    switch (this) {
      case DivAccessibilityMode.default_:
        return default_();
      case DivAccessibilityMode.merge:
        return merge();
      case DivAccessibilityMode.exclude:
        return exclude();
    }
  }

  T maybeMap<T>({
    T Function()? default_,
    T Function()? merge,
    T Function()? exclude,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivAccessibilityMode.default_:
        return default_?.call() ?? orElse();
      case DivAccessibilityMode.merge:
        return merge?.call() ?? orElse();
      case DivAccessibilityMode.exclude:
        return exclude?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivAccessibilityMode? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'default':
          return DivAccessibilityMode.default_;
        case 'merge':
          return DivAccessibilityMode.merge;
        case 'exclude':
          return DivAccessibilityMode.exclude;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivAccessibilityMode?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'default':
          return DivAccessibilityMode.default_;
        case 'merge':
          return DivAccessibilityMode.merge;
        case 'exclude':
          return DivAccessibilityMode.exclude;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
