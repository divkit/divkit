# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_absolute_edge_insets


# Image in 9-patch format (https://developer.android.com/studio/write/draw9patch).
class DivNinePatchBackground(BaseDiv):

    def __init__(
        self, *,
        type: str = "nine_patch_image",
        image_url: typing.Optional[typing.Union[Expr, str]] = None,
        insets: typing.Optional[div_absolute_edge_insets.DivAbsoluteEdgeInsets] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            image_url=image_url,
            insets=insets,
            **kwargs,
        )

    type: str = Field(default="nine_patch_image")
    image_url: typing.Union[Expr, str] = Field(
        format="uri", 
        description="Image URL.",
    )
    insets: div_absolute_edge_insets.DivAbsoluteEdgeInsets = Field(
        description=(
            "Margins that break the image into parts using the same "
            "rules as the CSS`border-image-slice` "
            "property(https://developer.mozilla.org/en-US/docs/Web/CSS/b"
            "order-image-slice)."
        ),
    )


DivNinePatchBackground.update_forward_refs()
