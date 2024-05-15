// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_base.dart';
import 'div_container.dart';
import 'div_custom.dart';
import 'div_gallery.dart';
import 'div_gif_image.dart';
import 'div_grid.dart';
import 'div_image.dart';
import 'div_indicator.dart';
import 'div_input.dart';
import 'div_pager.dart';
import 'div_select.dart';
import 'div_separator.dart';
import 'div_slider.dart';
import 'div_state.dart';
import 'div_tabs.dart';
import 'div_text.dart';
import 'div_video.dart';

class Div with EquatableMixin {
  const Div(DivBase value) : _value = value;

  final DivBase _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  DivBase get value {
    final value = _value;
    if (value is DivContainer) {
      return value;
    }
    if (value is DivCustom) {
      return value;
    }
    if (value is DivGallery) {
      return value;
    }
    if (value is DivGifImage) {
      return value;
    }
    if (value is DivGrid) {
      return value;
    }
    if (value is DivImage) {
      return value;
    }
    if (value is DivIndicator) {
      return value;
    }
    if (value is DivInput) {
      return value;
    }
    if (value is DivPager) {
      return value;
    }
    if (value is DivSelect) {
      return value;
    }
    if (value is DivSeparator) {
      return value;
    }
    if (value is DivSlider) {
      return value;
    }
    if (value is DivState) {
      return value;
    }
    if (value is DivTabs) {
      return value;
    }
    if (value is DivText) {
      return value;
    }
    if (value is DivVideo) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in Div");
  }

  T map<T>({
    required T Function(DivContainer) divContainer,
    required T Function(DivCustom) divCustom,
    required T Function(DivGallery) divGallery,
    required T Function(DivGifImage) divGifImage,
    required T Function(DivGrid) divGrid,
    required T Function(DivImage) divImage,
    required T Function(DivIndicator) divIndicator,
    required T Function(DivInput) divInput,
    required T Function(DivPager) divPager,
    required T Function(DivSelect) divSelect,
    required T Function(DivSeparator) divSeparator,
    required T Function(DivSlider) divSlider,
    required T Function(DivState) divState,
    required T Function(DivTabs) divTabs,
    required T Function(DivText) divText,
    required T Function(DivVideo) divVideo,
  }) {
    final value = _value;
    if (value is DivContainer) {
      return divContainer(value);
    }
    if (value is DivCustom) {
      return divCustom(value);
    }
    if (value is DivGallery) {
      return divGallery(value);
    }
    if (value is DivGifImage) {
      return divGifImage(value);
    }
    if (value is DivGrid) {
      return divGrid(value);
    }
    if (value is DivImage) {
      return divImage(value);
    }
    if (value is DivIndicator) {
      return divIndicator(value);
    }
    if (value is DivInput) {
      return divInput(value);
    }
    if (value is DivPager) {
      return divPager(value);
    }
    if (value is DivSelect) {
      return divSelect(value);
    }
    if (value is DivSeparator) {
      return divSeparator(value);
    }
    if (value is DivSlider) {
      return divSlider(value);
    }
    if (value is DivState) {
      return divState(value);
    }
    if (value is DivTabs) {
      return divTabs(value);
    }
    if (value is DivText) {
      return divText(value);
    }
    if (value is DivVideo) {
      return divVideo(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in Div");
  }

  T maybeMap<T>({
    T Function(DivContainer)? divContainer,
    T Function(DivCustom)? divCustom,
    T Function(DivGallery)? divGallery,
    T Function(DivGifImage)? divGifImage,
    T Function(DivGrid)? divGrid,
    T Function(DivImage)? divImage,
    T Function(DivIndicator)? divIndicator,
    T Function(DivInput)? divInput,
    T Function(DivPager)? divPager,
    T Function(DivSelect)? divSelect,
    T Function(DivSeparator)? divSeparator,
    T Function(DivSlider)? divSlider,
    T Function(DivState)? divState,
    T Function(DivTabs)? divTabs,
    T Function(DivText)? divText,
    T Function(DivVideo)? divVideo,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivContainer && divContainer != null) {
      return divContainer(value);
    }
    if (value is DivCustom && divCustom != null) {
      return divCustom(value);
    }
    if (value is DivGallery && divGallery != null) {
      return divGallery(value);
    }
    if (value is DivGifImage && divGifImage != null) {
      return divGifImage(value);
    }
    if (value is DivGrid && divGrid != null) {
      return divGrid(value);
    }
    if (value is DivImage && divImage != null) {
      return divImage(value);
    }
    if (value is DivIndicator && divIndicator != null) {
      return divIndicator(value);
    }
    if (value is DivInput && divInput != null) {
      return divInput(value);
    }
    if (value is DivPager && divPager != null) {
      return divPager(value);
    }
    if (value is DivSelect && divSelect != null) {
      return divSelect(value);
    }
    if (value is DivSeparator && divSeparator != null) {
      return divSeparator(value);
    }
    if (value is DivSlider && divSlider != null) {
      return divSlider(value);
    }
    if (value is DivState && divState != null) {
      return divState(value);
    }
    if (value is DivTabs && divTabs != null) {
      return divTabs(value);
    }
    if (value is DivText && divText != null) {
      return divText(value);
    }
    if (value is DivVideo && divVideo != null) {
      return divVideo(value);
    }
    return orElse();
  }

  const Div.divContainer(
    DivContainer value,
  ) : _value = value;

  const Div.divCustom(
    DivCustom value,
  ) : _value = value;

  const Div.divGallery(
    DivGallery value,
  ) : _value = value;

  const Div.divGifImage(
    DivGifImage value,
  ) : _value = value;

  const Div.divGrid(
    DivGrid value,
  ) : _value = value;

  const Div.divImage(
    DivImage value,
  ) : _value = value;

  const Div.divIndicator(
    DivIndicator value,
  ) : _value = value;

  const Div.divInput(
    DivInput value,
  ) : _value = value;

  const Div.divPager(
    DivPager value,
  ) : _value = value;

  const Div.divSelect(
    DivSelect value,
  ) : _value = value;

  const Div.divSeparator(
    DivSeparator value,
  ) : _value = value;

  const Div.divSlider(
    DivSlider value,
  ) : _value = value;

  const Div.divState(
    DivState value,
  ) : _value = value;

  const Div.divTabs(
    DivTabs value,
  ) : _value = value;

  const Div.divText(
    DivText value,
  ) : _value = value;

  const Div.divVideo(
    DivVideo value,
  ) : _value = value;

  static Div? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivImage.type:
        return Div(DivImage.fromJson(json)!);
      case DivGifImage.type:
        return Div(DivGifImage.fromJson(json)!);
      case DivText.type:
        return Div(DivText.fromJson(json)!);
      case DivSeparator.type:
        return Div(DivSeparator.fromJson(json)!);
      case DivContainer.type:
        return Div(DivContainer.fromJson(json)!);
      case DivGrid.type:
        return Div(DivGrid.fromJson(json)!);
      case DivGallery.type:
        return Div(DivGallery.fromJson(json)!);
      case DivPager.type:
        return Div(DivPager.fromJson(json)!);
      case DivTabs.type:
        return Div(DivTabs.fromJson(json)!);
      case DivState.type:
        return Div(DivState.fromJson(json)!);
      case DivCustom.type:
        return Div(DivCustom.fromJson(json)!);
      case DivIndicator.type:
        return Div(DivIndicator.fromJson(json)!);
      case DivSlider.type:
        return Div(DivSlider.fromJson(json)!);
      case DivInput.type:
        return Div(DivInput.fromJson(json)!);
      case DivSelect.type:
        return Div(DivSelect.fromJson(json)!);
      case DivVideo.type:
        return Div(DivVideo.fromJson(json)!);
    }
    return null;
  }
}
