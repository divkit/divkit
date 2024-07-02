// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/generated_sources/div_container.dart';
import 'package:divkit/src/generated_sources/div_custom.dart';
import 'package:divkit/src/generated_sources/div_gallery.dart';
import 'package:divkit/src/generated_sources/div_gif_image.dart';
import 'package:divkit/src/generated_sources/div_grid.dart';
import 'package:divkit/src/generated_sources/div_image.dart';
import 'package:divkit/src/generated_sources/div_indicator.dart';
import 'package:divkit/src/generated_sources/div_input.dart';
import 'package:divkit/src/generated_sources/div_pager.dart';
import 'package:divkit/src/generated_sources/div_select.dart';
import 'package:divkit/src/generated_sources/div_separator.dart';
import 'package:divkit/src/generated_sources/div_slider.dart';
import 'package:divkit/src/generated_sources/div_state.dart';
import 'package:divkit/src/generated_sources/div_tabs.dart';
import 'package:divkit/src/generated_sources/div_text.dart';
import 'package:divkit/src/generated_sources/div_video.dart';

class Div with EquatableMixin {
  final DivBase value;
  final int _index;

  @override
  List<Object?> get props => [value];

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
    switch (_index) {
      case 0:
        return divContainer(
          value as DivContainer,
        );
      case 1:
        return divCustom(
          value as DivCustom,
        );
      case 2:
        return divGallery(
          value as DivGallery,
        );
      case 3:
        return divGifImage(
          value as DivGifImage,
        );
      case 4:
        return divGrid(
          value as DivGrid,
        );
      case 5:
        return divImage(
          value as DivImage,
        );
      case 6:
        return divIndicator(
          value as DivIndicator,
        );
      case 7:
        return divInput(
          value as DivInput,
        );
      case 8:
        return divPager(
          value as DivPager,
        );
      case 9:
        return divSelect(
          value as DivSelect,
        );
      case 10:
        return divSeparator(
          value as DivSeparator,
        );
      case 11:
        return divSlider(
          value as DivSlider,
        );
      case 12:
        return divState(
          value as DivState,
        );
      case 13:
        return divTabs(
          value as DivTabs,
        );
      case 14:
        return divText(
          value as DivText,
        );
      case 15:
        return divVideo(
          value as DivVideo,
        );
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
    switch (_index) {
      case 0:
        if (divContainer != null) {
          return divContainer(
            value as DivContainer,
          );
        }
        break;
      case 1:
        if (divCustom != null) {
          return divCustom(
            value as DivCustom,
          );
        }
        break;
      case 2:
        if (divGallery != null) {
          return divGallery(
            value as DivGallery,
          );
        }
        break;
      case 3:
        if (divGifImage != null) {
          return divGifImage(
            value as DivGifImage,
          );
        }
        break;
      case 4:
        if (divGrid != null) {
          return divGrid(
            value as DivGrid,
          );
        }
        break;
      case 5:
        if (divImage != null) {
          return divImage(
            value as DivImage,
          );
        }
        break;
      case 6:
        if (divIndicator != null) {
          return divIndicator(
            value as DivIndicator,
          );
        }
        break;
      case 7:
        if (divInput != null) {
          return divInput(
            value as DivInput,
          );
        }
        break;
      case 8:
        if (divPager != null) {
          return divPager(
            value as DivPager,
          );
        }
        break;
      case 9:
        if (divSelect != null) {
          return divSelect(
            value as DivSelect,
          );
        }
        break;
      case 10:
        if (divSeparator != null) {
          return divSeparator(
            value as DivSeparator,
          );
        }
        break;
      case 11:
        if (divSlider != null) {
          return divSlider(
            value as DivSlider,
          );
        }
        break;
      case 12:
        if (divState != null) {
          return divState(
            value as DivState,
          );
        }
        break;
      case 13:
        if (divTabs != null) {
          return divTabs(
            value as DivTabs,
          );
        }
        break;
      case 14:
        if (divText != null) {
          return divText(
            value as DivText,
          );
        }
        break;
      case 15:
        if (divVideo != null) {
          return divVideo(
            value as DivVideo,
          );
        }
        break;
    }
    return orElse();
  }

  const Div.divContainer(
    DivContainer obj,
  )   : value = obj,
        _index = 0;

  const Div.divCustom(
    DivCustom obj,
  )   : value = obj,
        _index = 1;

  const Div.divGallery(
    DivGallery obj,
  )   : value = obj,
        _index = 2;

  const Div.divGifImage(
    DivGifImage obj,
  )   : value = obj,
        _index = 3;

  const Div.divGrid(
    DivGrid obj,
  )   : value = obj,
        _index = 4;

  const Div.divImage(
    DivImage obj,
  )   : value = obj,
        _index = 5;

  const Div.divIndicator(
    DivIndicator obj,
  )   : value = obj,
        _index = 6;

  const Div.divInput(
    DivInput obj,
  )   : value = obj,
        _index = 7;

  const Div.divPager(
    DivPager obj,
  )   : value = obj,
        _index = 8;

  const Div.divSelect(
    DivSelect obj,
  )   : value = obj,
        _index = 9;

  const Div.divSeparator(
    DivSeparator obj,
  )   : value = obj,
        _index = 10;

  const Div.divSlider(
    DivSlider obj,
  )   : value = obj,
        _index = 11;

  const Div.divState(
    DivState obj,
  )   : value = obj,
        _index = 12;

  const Div.divTabs(
    DivTabs obj,
  )   : value = obj,
        _index = 13;

  const Div.divText(
    DivText obj,
  )   : value = obj,
        _index = 14;

  const Div.divVideo(
    DivVideo obj,
  )   : value = obj,
        _index = 15;

  static Div? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivContainer.type:
        return Div.divContainer(DivContainer.fromJson(json)!);
      case DivCustom.type:
        return Div.divCustom(DivCustom.fromJson(json)!);
      case DivGallery.type:
        return Div.divGallery(DivGallery.fromJson(json)!);
      case DivGifImage.type:
        return Div.divGifImage(DivGifImage.fromJson(json)!);
      case DivGrid.type:
        return Div.divGrid(DivGrid.fromJson(json)!);
      case DivImage.type:
        return Div.divImage(DivImage.fromJson(json)!);
      case DivIndicator.type:
        return Div.divIndicator(DivIndicator.fromJson(json)!);
      case DivInput.type:
        return Div.divInput(DivInput.fromJson(json)!);
      case DivPager.type:
        return Div.divPager(DivPager.fromJson(json)!);
      case DivSelect.type:
        return Div.divSelect(DivSelect.fromJson(json)!);
      case DivSeparator.type:
        return Div.divSeparator(DivSeparator.fromJson(json)!);
      case DivSlider.type:
        return Div.divSlider(DivSlider.fromJson(json)!);
      case DivState.type:
        return Div.divState(DivState.fromJson(json)!);
      case DivTabs.type:
        return Div.divTabs(DivTabs.fromJson(json)!);
      case DivText.type:
        return Div.divText(DivText.fromJson(json)!);
      case DivVideo.type:
        return Div.divVideo(DivVideo.fromJson(json)!);
    }
    return null;
  }
}
