import sys

import pytest

from pydivkit import (
    Div,
    DivAction,
    DivBorder,
    DivContainer,
    DivGifImage,
    DivImage,
    DivText,
    Field,
    Ref,
    make_div,
)


@pytest.mark.skipif(
    sys.version_info < (3, 9),
    reason="subscripting requires python3.9 or higher"
)
def test_sequence_collections_type():
    from collections.abc import Sequence

    class ImageItems(DivContainer):
        image_items: Sequence[DivImage] = Field()
        items = [
            DivContainer(
                items=Ref(image_items),
            ),
        ]

    images = ImageItems(image_items=[DivImage(image_url="https://image.url")])
    assert "card" in make_div(images)


@pytest.mark.xfail
def test_ref_collections_union_type():
    from collections.abc import Sequence
    from typing import Union

    class ImageItems(DivContainer):
        image_items: Union[Sequence[DivImage], Sequence[DivGifImage]] = Field()
        items = [
            DivContainer(
                items=Ref(image_items),
            ),
        ]

    images = ImageItems(image_items=None)
    assert "card" in make_div(images)


@pytest.mark.skipif(
    sys.version_info < (3, 9),
    reason="subscripting requires python3.9 or higher"
)
def test_union_type():
    from collections.abc import Sequence

    class ImageItems(DivContainer):
        image_items: Sequence[Div] = Field()
        items = [
            DivContainer(
                items=Ref(image_items),
            ),
        ]

    images = ImageItems(image_items=[DivImage(image_url="https://image.url")])
    assert "card" in make_div(images)


def test_optional_expr():
    class CategoriesItem(DivContainer):
        corner_radius: int = Field()
        border = DivBorder(corner_radius=Ref(corner_radius))

    assert "card" in make_div(CategoriesItem(items=[], corner_radius=1))


def test_sequence_typing_type():
    from typing import Sequence

    class ImageItems(DivContainer):
        image_items: Sequence[Div] = Field()
        items = [
            DivContainer(
                items=Ref(image_items),
            ),
        ]

    images = ImageItems(image_items=[DivImage(image_url="https://image.url")])
    assert "card" in make_div(images)


@pytest.mark.skipif(
    sys.version_info < (3, 9),
    reason="subscripting requires python3.9 or higher"
)
def test_optional_sequence():
    from collections.abc import Sequence
    class ButtionItem(DivContainer):
        button_actions: Sequence[DivAction] = Field(min_items=1)
        items = [
            DivText(
                text="test",
                actions=Ref(button_actions)
            ),
        ]
