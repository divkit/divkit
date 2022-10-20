# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_container
from . import div_custom
from . import div_gallery
from . import div_gif_image
from . import div_grid
from . import div_image
from . import div_indicator
from . import div_input
from . import div_pager
from . import div_separator
from . import div_slider
from . import div_state
from . import div_tabs
from . import div_text

from typing import Union

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
