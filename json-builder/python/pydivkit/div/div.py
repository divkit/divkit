# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Field

from . import (
    div_container, div_custom, div_gallery, div_gif_image, div_grid, div_image,
    div_indicator, div_input, div_pager, div_separator, div_slider, div_state,
    div_tabs, div_text,
)


Div = Union[
    div_image.DivImage,
    div_gif_image.DivGifImage,
    div_text.DivText,
    div_separator.DivSeparator,
    div_container.DivContainer,
    div_grid.DivGrid,
    div_gallery.DivGallery,
    div_pager.DivPager,
    div_tabs.DivTabs,
    div_state.DivState,
    div_custom.DivCustom,
    div_indicator.DivIndicator,
    div_slider.DivSlider,
    div_input.DivInput,
]
