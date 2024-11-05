// Since we still support a wide range of flutter_svg versions, we cannot migrate to the new API yet.
// ignore_for_file: deprecated_member_use

import 'dart:math' as math;
import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/image/div_image_model.dart';
import 'package:divkit/src/utils/mapping_widget.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class DivImageWidget extends DivMappingWidget<DivImage, DivImageModel> {
  const DivImageWidget(
    super.data, {
    super.key,
  });

  @override
  DivImageModel value(BuildContext context) {
    final divContext = read<DivContext>(context)!;
    data.resolve(divContext.variables);
    return data.bind(context);
  }

  @override
  Stream<DivImageModel> stream(BuildContext context) {
    final divContext = watch<DivContext>(context)!;
    return divContext.variableManager.watch((values) {
      data.resolve(values);
      return data.bind(context);
    });
  }

  @override
  Widget build(BuildContext context, DivImageModel model) {
    final Widget image;
    if (model.src.startsWith('divkit-asset://')) {
      final src = model.src.replaceFirst('divkit-asset:/', 'assets');
      if (src.endsWith('.svg')) {
        final blendMode = model.colorBlendMode;
        final blendColor = model.color;
        image = SvgPicture.asset(
          src,
          alignment: model.contentAlignment.resolve(
            Directionality.maybeOf(context),
          ),
          fit: model.fit,
          color: blendColor,
          colorBlendMode: blendMode ?? BlendMode.srcIn,
        );
      } else {
        image = Image.asset(
          src,
          fit: model.fit,
          color: model.color,
          colorBlendMode: model.colorBlendMode,
          alignment: model.contentAlignment.resolve(
            Directionality.maybeOf(context),
          ),
        );
      }
    } else {
      if (model.src.endsWith('.svg')) {
        final blendMode = model.colorBlendMode;
        final blendColor = model.color;
        image = SvgPicture.network(
          model.src,
          alignment: model.contentAlignment.resolve(
            Directionality.maybeOf(context),
          ),
          fit: model.fit,
          color: blendColor,
          colorBlendMode: blendMode ?? BlendMode.srcIn,
        );
      } else {
        image = CachedNetworkImage(
          imageUrl: model.src,
          fit: model.fit,
          color: model.color,
          colorBlendMode: model.colorBlendMode,
          alignment: model.contentAlignment.resolve(
            Directionality.maybeOf(context),
          ),
        );
      }
    }

    final Widget blurredImage;
    final blurRadius = model.blurRadius;
    if (blurRadius != null) {
      blurredImage = ImageFiltered(
        imageFilter: ImageFilter.blur(
          sigmaX: blurRadius,
          sigmaY: blurRadius,
        ),
        child: image,
      );
    } else {
      blurredImage = image;
    }

    final Widget mirroredImage;
    if (model.rtlMirror) {
      mirroredImage = Transform(
        transform: Matrix4.rotationY(math.pi),
        alignment: model.contentAlignment,
        child: blurredImage,
      );
    } else {
      mirroredImage = blurredImage;
    }

    return DivBaseWidget(
      data: data,
      aspect: data.aspect?.ratio,
      tapActionData: DivTapActionData(
        action: data.action,
        actions: data.actions,
        longtapActions: data.longtapActions,
        actionAnimation: data.actionAnimation,
      ),
      child: mirroredImage,
    );
  }
}
