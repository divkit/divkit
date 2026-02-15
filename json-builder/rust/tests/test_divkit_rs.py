"""Tests for divkit_rs Python bindings.

Run with: pytest tests/ (after `maturin develop`)
"""

import enum

import divkit_rs
import pytest
from divkit_rs import (
    DivAction,
    DivActionClearFocus,
    DivActionCopyToClipboard,
    DivActionDownload,
    DivActionFocusElement,
    DivActionHideTooltip,
    DivActionScrollTo,
    DivActionSetState,
    DivActionSetVariable,
    DivActionShowTooltip,
    DivActionSubmit,
    DivActionSubmitRequest,
    DivActionTimer,
    DivActionVideo,
    DivAspect,
    DivBorder,
    DivContainer,
    DivEdgeInsets,
    DivFixedSize,
    DivFontWeight,
    DivImage,
    DivLinearGradient,
    DivMatchParentSize,
    DivSolidBackground,
    DivText,
    DivTextRange,
    DivVideo,
    DivVideoSource,
    DivVisibility,
    DivWrapContentSize,
    IndexDestination,
    PyDivEntity,
    RequestHeader,
    StringValue,
)

# ================================================================
# Fix #3 — Boolean serialization
# ================================================================


class TestBoolSerialization:
    """Booleans must serialize as JSON true/false, not 0/1."""

    def test_bool_true_serializes_as_true(self):
        d = DivWrapContentSize(constrained=True).dict()
        assert d["constrained"] is True

    def test_bool_false_serializes_as_false(self):
        d = DivWrapContentSize(constrained=False).dict()
        assert d["constrained"] is False

    def test_bool_type_is_bool(self):
        d = DivBorder(has_shadow=True).dict()
        assert type(d["has_shadow"]) is bool

    def test_bool_in_nested_entity(self):
        c = DivContainer(
            items=[],
            border=DivBorder(has_shadow=True),
        )
        d = c.dict()
        assert d["border"]["has_shadow"] is True


# ================================================================
# Fix #2 — Float serialization
# ================================================================


class TestFloatSerialization:
    """Whole-number floats serialize as integers to match DivKit spec."""

    def test_whole_float_serializes_as_int(self):
        d = DivAspect(ratio=1.0).dict()
        assert d["ratio"] == 1
        assert isinstance(d["ratio"], int)

    def test_fractional_float_stays_float(self):
        d = DivAspect(ratio=1.5).dict()
        assert d["ratio"] == 1.5
        assert isinstance(d["ratio"], float)

    def test_weight_whole_float(self):
        d = DivMatchParentSize(weight=2.0).dict()
        assert d["weight"] == 2
        assert isinstance(d["weight"], int)

    def test_zero_float(self):
        d = DivAspect(ratio=0.0).dict()
        assert d["ratio"] == 0
        assert isinstance(d["ratio"], int)

    def test_negative_whole_float(self):
        d = DivEdgeInsets(left=-8.0).dict()
        assert d["left"] == -8
        assert isinstance(d["left"], int)

    def test_int_passthrough_unchanged(self):
        """Python int values stay as ints (no regression)."""
        d = DivAspect(ratio=1).dict()
        assert d["ratio"] == 1
        assert isinstance(d["ratio"], int)


# ================================================================
# Fix #5 — Entity constructors are real Python classes
# ================================================================


class TestEntityClasses:
    """Entity constructors must be real types, not factory functions."""

    def test_constructor_is_a_type(self):
        assert isinstance(DivContainer, type)
        assert isinstance(DivText, type)
        assert isinstance(DivImage, type)

    def test_isinstance_check(self):
        c = DivContainer(items=[])
        assert isinstance(c, DivContainer)

    def test_isinstance_base_class(self):
        c = DivContainer(items=[])
        assert isinstance(c, PyDivEntity)

    def test_isinstance_negative(self):
        t = DivText(text="hi")
        assert not isinstance(t, DivContainer)

    def test_subclassing(self):
        class MyContainer(DivContainer):
            pass

        mc = MyContainer(items=[])
        assert isinstance(mc, MyContainer)
        assert isinstance(mc, DivContainer)
        assert isinstance(mc, PyDivEntity)

    def test_subclass_dict_works(self):
        class MyContainer(DivContainer):
            pass

        mc = MyContainer(items=[], orientation="vertical")
        d = mc.dict()
        assert d["type"] == "container"
        assert d["orientation"] == "vertical"

    def test_subclass_schema_works(self):
        class MyContainer(DivContainer):
            pass

        mc = MyContainer(items=[])
        s = mc.schema()
        assert "properties" in s
        assert "items" in s["properties"]

    def test_type_name(self):
        assert DivContainer.__name__ == "DivContainer"
        assert DivText.__name__ == "DivText"

    def test_entity_dict_output(self):
        """dict() still produces correct output after the class refactor."""
        c = DivContainer(
            items=[DivText(text="hello")],
            orientation="vertical",
        )
        d = c.dict()
        assert d["type"] == "container"
        assert d["orientation"] == "vertical"
        assert len(d["items"]) == 1
        assert d["items"][0]["type"] == "text"
        assert d["items"][0]["text"] == "hello"


# ================================================================
# Fix #6 — Enums are real enum.Enum subclasses
# ================================================================


class TestEnums:
    """Enum types must be real str+Enum for isinstance, constructors, etc."""

    def test_is_enum_subclass(self):
        assert issubclass(DivFontWeight, enum.Enum)

    def test_is_str_subclass(self):
        assert issubclass(DivFontWeight, str)

    def test_value_equality(self):
        assert DivFontWeight.BOLD == "bold"
        assert DivFontWeight.REGULAR == "regular"
        assert DivVisibility.VISIBLE == "visible"
        assert DivVisibility.GONE == "gone"

    def test_isinstance_check(self):
        assert isinstance(DivFontWeight.BOLD, DivFontWeight)
        assert isinstance(DivVisibility.VISIBLE, DivVisibility)

    def test_constructor_from_value(self):
        fw = DivFontWeight("bold")
        assert fw is DivFontWeight.BOLD

    def test_constructor_invalid_value(self):
        with pytest.raises(ValueError):
            DivFontWeight("nonexistent")

    def test_str_returns_value(self):
        """str() must return the raw value for backward compatibility."""
        assert str(DivFontWeight.BOLD) == "bold"
        assert str(DivVisibility.GONE) == "gone"

    def test_enum_in_entity_serialization(self):
        t = DivText(text="hi", font_weight=DivFontWeight.BOLD)
        d = t.dict()
        assert d["font_weight"] == "bold"

    def test_enum_iteration(self):
        members = list(DivFontWeight)
        values = [m.value for m in members]
        assert "bold" in values
        assert "light" in values
        assert "regular" in values
        assert "medium" in values


# ================================================================
# Schema completeness
# ================================================================


class TestSchema:
    """schema() must report all valid fields for an entity."""

    def test_container_schema_comprehensive(self):
        s = DivContainer(items=[]).schema()
        props = s["properties"]
        # DivContainer has 55+ fields plus type
        assert len(props) > 40

    def test_container_has_common_base_fields(self):
        props = DivContainer(items=[]).schema()["properties"]
        for field in [
            "type",
            "items",
            "visibility",
            "visibility_actions",
            "selected_actions",
            "aspect",
            "clip_to_bounds",
            "background",
            "border",
            "paddings",
            "margins",
            "width",
            "height",
            "id",
            "alpha",
        ]:
            assert field in props, f"Missing field: {field}"

    def test_text_schema_comprehensive(self):
        props = DivText(text="x").schema()["properties"]
        assert len(props) > 40
        for field in [
            "text",
            "font_size",
            "font_weight",
            "text_color",
            "letter_spacing",
            "line_height",
            "max_lines",
        ]:
            assert field in props, f"Missing field: {field}"

    def test_schema_exclude_fields(self):
        s = DivText(text="x").schema(exclude_fields=["accessibility", "actions"])
        props = s["properties"]
        assert "accessibility" not in props
        assert "actions" not in props
        assert "text" in props


# ================================================================
# dict() omits unset fields (issue #4 — verifying current behavior)
# ================================================================


class TestDictMinimal:
    """dict() should only include explicitly set fields + type."""

    def test_minimal_container(self):
        d = DivContainer(items=[]).dict()
        # Should have type + items, nothing else
        assert set(d.keys()) == {"type", "items"}

    def test_minimal_text(self):
        d = DivText(text="hi").dict()
        assert set(d.keys()) == {"type", "text"}

    def test_all_set_fields_present(self):
        d = DivText(text="hi", font_size=16, max_lines=2).dict()
        assert d == {"type": "text", "text": "hi", "font_size": 16, "max_lines": 2}


# ================================================================
# General regression / integration
# ================================================================


class TestIntegration:
    """End-to-end tests for common patterns."""

    def test_nested_entities(self):
        card = DivContainer(
            items=[
                DivText(text="Title", font_size=20),
                DivImage(
                    image_url="https://example.com/img.png",
                    width=DivFixedSize(value=100),
                    height=DivFixedSize(value=100),
                ),
            ],
            orientation="vertical",
            paddings=DivEdgeInsets(left=16, right=16),
            background=[DivSolidBackground(color="#ffffff")],
        )
        d = card.dict()
        assert d["type"] == "container"
        assert len(d["items"]) == 2
        assert d["items"][0]["font_size"] == 20
        assert d["items"][1]["width"] == {"type": "fixed", "value": 100}
        assert d["paddings"]["left"] == 16
        assert d["background"][0] == {"type": "solid", "color": "#ffffff"}

    def test_actions_serialization(self):
        t = DivText(
            text="Click me",
            actions=[DivAction(log_id="tap", url="https://example.com")],
        )
        d = t.dict()
        assert len(d["actions"]) == 1
        assert d["actions"][0]["log_id"] == "tap"
        assert d["actions"][0]["url"] == "https://example.com"

    def test_gradient_background(self):
        bg = DivLinearGradient(angle=90, colors=["#ff0000", "#0000ff"])
        d = bg.dict()
        assert d["type"] == "gradient"
        assert d["angle"] == 90
        assert d["colors"] == ["#ff0000", "#0000ff"]

    def test_build_is_alias_for_dict(self):
        t = DivText(text="hi")
        assert t.build() == t.dict()

    def test_getattr(self):
        t = DivText(text="hello", font_size=14)
        assert t.text == "hello"
        assert t.font_size == 14

    def test_setattr(self):
        t = DivText(text="hello")
        t.font_size = 14
        d = t.dict()
        assert d["font_size"] == 14


# ================================================================
# Duck-typed .dict() protocol on unknown objects
# ================================================================


class TestDictProtocol:
    """Objects with a .dict() method should be auto-converted, not str()-ified."""

    def test_plain_object_with_dict_method(self):
        """A plain Python class with .dict() is accepted in kwargs."""

        class FakeEntity:
            def dict(self):
                return {"type": "solid", "color": "#ff0000"}

        c = DivContainer(items=[], background=[FakeEntity()])
        d = c.dict()
        assert len(d["background"]) == 1
        assert d["background"][0] == {"type": "solid", "color": "#ff0000"}

    def test_nested_dict_protocol(self):
        """Nested objects with .dict() are recursively resolved."""

        class FakeSize:
            def dict(self):
                return {"type": "fixed", "value": 42}

        img = DivImage(image_url="https://example.com/a.png", width=FakeSize())
        d = img.dict()
        assert d["width"] == {"type": "fixed", "value": 42}

    def test_dict_protocol_in_list(self):
        """Objects with .dict() work inside lists."""

        class FakeAction:
            def dict(self):
                return {"log_id": "tap", "url": "https://example.com"}

        t = DivText(text="hi", actions=[FakeAction()])
        d = t.dict()
        assert d["actions"][0]["log_id"] == "tap"

    def test_dict_protocol_returns_nested_dict(self):
        """A .dict() returning nested dicts preserves the structure."""

        class FakeBorder:
            def dict(self):
                return {"has_shadow": True, "corner_radius": 8}

        c = DivContainer(items=[], border=FakeBorder())
        d = c.dict()
        assert d["border"]["has_shadow"] is True
        assert d["border"]["corner_radius"] == 8

    def test_divkit_entity_still_preferred(self):
        """Native PyDivEntity subclasses are still handled via the fast path."""
        bg = DivSolidBackground(color="#00ff00")
        c = DivContainer(items=[], background=[bg])
        d = c.dict()
        assert d["background"][0] == {"type": "solid", "color": "#00ff00"}

    def test_object_without_dict_falls_back_to_string(self):
        """Objects without .dict() still fall back to str()."""

        class Stringable:
            def __str__(self):
                return "hello-from-str"

        t = DivText(text=Stringable())
        d = t.dict()
        assert d["text"] == "hello-from-str"


# ================================================================
# Newly exported types — DivAction*, DivVideo, DivTextRange, etc.
# ================================================================


class TestNewlyExportedTypes:
    """Verify that previously missing types are now importable and functional."""

    # --- DivAction typed actions ---

    def test_action_set_state(self):
        a = DivActionSetState(state_id="0/my_state/active")
        d = a.dict()
        assert d["type"] == "set_state"
        assert d["state_id"] == "0/my_state/active"

    def test_action_set_variable(self):
        a = DivActionSetVariable(
            variable_name="my_var", value=StringValue(value="hello")
        )
        d = a.dict()
        assert d["type"] == "set_variable"
        assert d["variable_name"] == "my_var"

    def test_action_clear_focus(self):
        a = DivActionClearFocus()
        d = a.dict()
        assert d["type"] == "clear_focus"

    def test_action_focus_element(self):
        a = DivActionFocusElement(element_id="my_input")
        d = a.dict()
        assert d["type"] == "focus_element"
        assert d["element_id"] == "my_input"

    def test_action_show_tooltip(self):
        a = DivActionShowTooltip(id="tip1")
        d = a.dict()
        assert d["type"] == "show_tooltip"
        assert d["id"] == "tip1"

    def test_action_hide_tooltip(self):
        a = DivActionHideTooltip(id="tip1")
        d = a.dict()
        assert d["type"] == "hide_tooltip"

    def test_action_scroll_to(self):
        a = DivActionScrollTo(
            id="gallery1",
            destination=IndexDestination(value=3),
        )
        d = a.dict()
        assert d["type"] == "scroll_to"
        assert d["id"] == "gallery1"
        assert d["destination"]["type"] == "index"
        assert d["destination"]["value"] == 3

    def test_action_copy_to_clipboard(self):
        from divkit_rs import ContentText

        a = DivActionCopyToClipboard(content=ContentText(value="copied!"))
        d = a.dict()
        assert d["type"] == "copy_to_clipboard"
        assert d["content"]["type"] == "text"
        assert d["content"]["value"] == "copied!"

    def test_action_download(self):
        a = DivActionDownload(url="https://example.com/data")
        d = a.dict()
        assert d["type"] == "download"
        assert d["url"] == "https://example.com/data"

    def test_action_submit(self):
        a = DivActionSubmit(
            container_id="form1",
            request=DivActionSubmitRequest(
                url="https://api.example.com/submit",
                method="post",
                headers=[RequestHeader(name="X-Token", value="abc")],
            ),
        )
        d = a.dict()
        assert d["type"] == "submit"
        assert d["container_id"] == "form1"
        assert d["request"]["url"] == "https://api.example.com/submit"
        assert d["request"]["method"] == "post"
        assert len(d["request"]["headers"]) == 1
        assert d["request"]["headers"][0]["name"] == "X-Token"

    def test_action_timer(self):
        a = DivActionTimer(id="timer1", action="start")
        d = a.dict()
        assert d["type"] == "timer"
        assert d["id"] == "timer1"
        assert d["action"] == "start"

    def test_action_video(self):
        a = DivActionVideo(id="video1", action="start")
        d = a.dict()
        assert d["type"] == "video"
        assert d["id"] == "video1"
        assert d["action"] == "start"

    # --- Typed actions inside DivAction ---

    def test_typed_action_in_div_action(self):
        a = DivAction(
            log_id="set_state_action",
            typed=DivActionSetState(state_id="0/toggle/on"),
        )
        d = a.dict()
        assert d["log_id"] == "set_state_action"
        assert d["typed"]["type"] == "set_state"
        assert d["typed"]["state_id"] == "0/toggle/on"

    # --- DivVideo ---

    def test_div_video(self):
        v = DivVideo(
            video_sources=[
                DivVideoSource(
                    url="https://example.com/video.mp4",
                    mime_type="video/mp4",
                ),
            ],
        )
        d = v.dict()
        assert d["type"] == "video"
        assert len(d["video_sources"]) == 1
        assert d["video_sources"][0]["type"] == "video_source"
        assert d["video_sources"][0]["mime_type"] == "video/mp4"

    def test_div_video_with_options(self):
        v = DivVideo(
            video_sources=[
                DivVideoSource(
                    url="https://example.com/video.mp4",
                    mime_type="video/mp4",
                ),
            ],
            autostart=True,
            muted=True,
            repeatable=True,
        )
        d = v.dict()
        assert d["autostart"] is True
        assert d["muted"] is True
        assert d["repeatable"] is True

    # --- DivTextRange ---

    def test_div_text_range(self):
        tr = DivTextRange(
            start=0,
            end=5,
            font_weight="bold",
            text_color="#ff0000",
        )
        d = tr.dict()
        assert d["start"] == 0
        assert d["end"] == 5
        assert d["font_weight"] == "bold"
        assert d["text_color"] == "#ff0000"

    def test_text_with_ranges(self):
        t = DivText(
            text="Hello World",
            ranges=[
                DivTextRange(start=0, end=5, font_weight="bold"),
                DivTextRange(start=6, end=11, text_color="#0000ff"),
            ],
        )
        d = t.dict()
        assert d["type"] == "text"
        assert len(d["ranges"]) == 2
        assert d["ranges"][0]["start"] == 0
        assert d["ranges"][0]["font_weight"] == "bold"
        assert d["ranges"][1]["text_color"] == "#0000ff"

    # --- Verify isinstance works on new types ---

    def test_new_types_are_classes(self):
        assert isinstance(DivActionSetState, type)
        assert isinstance(DivVideo, type)
        assert isinstance(DivTextRange, type)
        assert isinstance(DivActionCopyToClipboard, type)

    def test_new_types_isinstance(self):
        a = DivActionSetState(state_id="0/s/active")
        assert isinstance(a, DivActionSetState)
        assert isinstance(a, PyDivEntity)

        v = DivVideo(video_sources=[])
        assert isinstance(v, DivVideo)
        assert isinstance(v, PyDivEntity)

    # --- Bulk import check ---

    def test_all_action_types_importable(self):  # noqa: C901
        """Every DivAction* type is importable from divkit_rs."""
        action_types = [
            "DivActionAnimatorStart",
            "DivActionAnimatorStop",
            "DivActionArrayInsertValue",
            "DivActionArrayRemoveValue",
            "DivActionArraySetValue",
            "DivActionClearFocus",
            "DivActionCopyToClipboard",
            "DivActionCustom",
            "DivActionDictSetValue",
            "DivActionDownload",
            "DivActionFocusElement",
            "DivActionHideTooltip",
            "DivActionMenuItem",
            "DivActionScrollBy",
            "DivActionScrollTo",
            "DivActionSetState",
            "DivActionSetStoredValue",
            "DivActionSetVariable",
            "DivActionShowTooltip",
            "DivActionSubmit",
            "DivActionSubmitRequest",
            "DivActionTimer",
            "DivActionUpdateStructure",
            "DivActionVideo",
        ]
        for name in action_types:
            cls = getattr(divkit_rs, name)
            assert isinstance(cls, type), f"{name} is not a type"


# ================================================================
# P2 — Subclassable classes (template pattern)
# ================================================================


class TestSubclassing:
    """Subclasses of divkit-rs types should work as template classes."""

    # --- Custom __init__ ---

    def test_subclass_custom_init(self):
        """Subclass with custom __init__ that passes fields to super()."""

        class TemplateButton(DivContainer):
            def __init__(self, text="Click", **kwargs):
                super().__init__(items=[DivText(text=text)], **kwargs)

        b = TemplateButton()
        d = b.dict()
        assert d["type"] == "container"
        assert len(d["items"]) == 1
        assert d["items"][0]["type"] == "text"
        assert d["items"][0]["text"] == "Click"

    def test_subclass_custom_init_with_kwargs(self):
        """Extra kwargs passed through to parent."""

        class TemplateButton(DivContainer):
            def __init__(self, text="Click", **kwargs):
                super().__init__(items=[DivText(text=text)], **kwargs)

        b = TemplateButton(text="OK", orientation="vertical")
        d = b.dict()
        assert d["orientation"] == "vertical"
        assert d["items"][0]["text"] == "OK"

    def test_subclass_custom_init_no_kwarg_leak(self):
        """Kwargs consumed by subclass __init__ do NOT leak into dict()."""

        class TemplateButton(DivContainer):
            def __init__(self, text="Click", **kwargs):
                super().__init__(items=[DivText(text=text)], **kwargs)

        b = TemplateButton(text="OK", orientation="vertical")
        d = b.dict()
        assert "text" not in d  # consumed by TemplateButton, not a container field

    # --- Class-level defaults ---

    def test_class_level_defaults(self):
        """Class-level attributes become default field values in dict()."""

        class VerticalContainer(DivContainer):
            orientation = "vertical"

        c = VerticalContainer(items=[])
        d = c.dict()
        assert d["orientation"] == "vertical"
        assert d["items"] == []

    def test_class_level_defaults_kwargs_override(self):
        """Explicit kwargs override class-level defaults."""

        class VerticalContainer(DivContainer):
            orientation = "vertical"

        c = VerticalContainer(items=[], orientation="horizontal")
        assert c.dict()["orientation"] == "horizontal"

    def test_class_level_defaults_no_extra_fields(self):
        """Only fields in _field_names are picked up from class attrs."""

        class MyContainer(DivContainer):
            orientation = "vertical"
            my_custom_attr = "should_not_appear"

        c = MyContainer(items=[])
        d = c.dict()
        assert "my_custom_attr" not in d
        assert d["orientation"] == "vertical"

    # --- Multi-level inheritance ---

    def test_multi_level_inheritance(self):
        """Defaults accumulate across multiple inheritance levels."""

        class Card(DivContainer):
            orientation = "vertical"

        class PaddedCard(Card):
            clip_to_bounds = True

        pc = PaddedCard(items=[])
        d = pc.dict()
        assert d["orientation"] == "vertical"
        assert d["clip_to_bounds"] is True
        assert d["items"] == []

    def test_multi_level_override(self):
        """Subclass defaults override parent defaults."""

        class VerticalContainer(DivContainer):
            orientation = "vertical"

        class HorizontalContainer(VerticalContainer):
            orientation = "horizontal"

        c = HorizontalContainer(items=[])
        assert c.dict()["orientation"] == "horizontal"

    # --- isinstance / type checks ---

    def test_isinstance_chain(self):
        class Card(DivContainer):
            orientation = "vertical"

        c = Card(items=[])
        assert isinstance(c, Card)
        assert isinstance(c, DivContainer)
        assert isinstance(c, PyDivEntity)

    def test_not_isinstance_sibling(self):
        class CardA(DivContainer):
            pass

        class CardB(DivContainer):
            pass

        a = CardA(items=[])
        assert not isinstance(a, CardB)

    # --- __init_subclass__ ---

    def test_init_subclass_hook(self):
        """__init_subclass__ fires for sub-subclasses."""
        registry = []

        class TrackedDiv(DivContainer):
            def __init_subclass__(cls, **kwargs):
                super().__init_subclass__(**kwargs)
                registry.append(cls.__name__)

        class Alpha(TrackedDiv):
            pass

        class Beta(TrackedDiv):
            pass

        assert registry == ["Alpha", "Beta"]

    # --- __repr__ ---

    def test_repr_shows_subclass_name(self):
        class MyText(DivText):
            pass

        t = MyText(text="hello")
        assert "MyText" in repr(t)

    # --- schema ---

    def test_subclass_schema_includes_parent_fields(self):
        class Card(DivContainer):
            orientation = "vertical"

        c = Card(items=[])
        s = c.schema()
        assert "items" in s["properties"]
        assert "orientation" in s["properties"]

    # --- Class metadata accessible ---

    def test_type_name_accessible(self):
        assert DivContainer._type_name == "container"
        assert DivText._type_name == "text"

    def test_field_names_accessible(self):
        assert "items" in DivContainer._field_names
        assert "orientation" in DivContainer._field_names
        assert "text" in DivText._field_names

    def test_field_names_inherited(self):
        class Card(DivContainer):
            pass

        assert Card._type_name == "container"
        assert "items" in Card._field_names

    # --- Complex template pattern ---

    def test_template_with_nested_defaults(self):
        """Real-world template pattern: card with header + body."""

        class InfoCard(DivContainer):
            orientation = "vertical"

            def __init__(self, title, body_items, **kwargs):
                header = DivText(text=title, font_size=20, font_weight="bold")
                super().__init__(
                    items=[header] + body_items,
                    **kwargs,
                )

        card = InfoCard(
            title="Welcome",
            body_items=[DivText(text="Hello world")],
            paddings=DivEdgeInsets(left=16, right=16),
        )
        d = card.dict()
        assert d["type"] == "container"
        assert d["orientation"] == "vertical"
        assert len(d["items"]) == 2
        assert d["items"][0]["text"] == "Welcome"
        assert d["items"][0]["font_size"] == 20
        assert d["items"][1]["text"] == "Hello world"
        assert d["paddings"]["left"] == 16
