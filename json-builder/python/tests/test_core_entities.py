import enum
from typing import Any, Mapping, Optional, Sequence, Union

import pytest

from pydivkit.core import BaseDiv, BaseEntity, Expr, Field, Ref


# ---------------------------------------------------------------------------
# BaseEntity: construction and field assignment
# ---------------------------------------------------------------------------
class TestBaseEntityInit:
    def test_create_simple_entity(self):
        class SimpleEntity(BaseEntity):
            name: str = Field()

        entity = SimpleEntity(name="hello")
        assert entity.name == "hello"

    def test_field_with_default(self):
        class DefaultEntity(BaseEntity):
            color: str = Field(default="red")

        entity = DefaultEntity()
        assert entity.color == "red"

    def test_optional_field_default_none(self):
        """Optional field defaults to None when not provided."""

        class OptEntity(BaseEntity):
            label: Optional[str] = Field()

        entity = OptEntity()
        assert entity.label is None

    def test_sequence_field(self):
        class SeqEntity(BaseEntity):
            items: Sequence[int] = Field()

        entity = SeqEntity(items=[1, 2, 3])
        assert entity.items == [1, 2, 3]

    def test_sequence_field_wrong_type_raises(self):
        class SeqEntity2(BaseEntity):
            items: Sequence[int] = Field()

        with pytest.raises(ValueError, match="has wrong type"):
            SeqEntity2(items=42)

    def test_mapping_field(self):
        class MapEntity(BaseEntity):
            data: Mapping[str, int] = Field()

        entity = MapEntity(data={"a": 1, "b": 2})
        assert entity.data == {"a": 1, "b": 2}

    def test_mapping_field_wrong_type_raises(self):
        class MapEntity2(BaseEntity):
            data: Mapping[str, int] = Field()

        with pytest.raises(ValueError, match="has wrong type"):
            MapEntity2(data=42)

    def test_any_field_passthrough(self):
        class AnyEntity(BaseEntity):
            data: Any = Field()

        value = {"key": [1, 2, 3]}
        entity = AnyEntity(data=value)
        assert entity.data is value

    def test_union_field_exact_match(self):
        class UnionEntity(BaseEntity):
            val: Union[int, str] = Field()

        entity = UnionEntity(val=42)
        assert entity.val == 42

    def test_union_field_no_match_raises(self):
        class UnionEntity2(BaseEntity):
            val: Union[int, float] = Field()

        with pytest.raises(ValueError, match="has wrong type"):
            UnionEntity2(val=[])

    def test_type_coercion(self):
        """Values are coerced to field type (e.g. int -> float)."""

        class CoerceEntity(BaseEntity):
            val: float = Field()

        entity = CoerceEntity(val=42)
        assert isinstance(entity.val, float)
        assert entity.val == 42.0

    def test_unknown_attribute_raises(self):
        class StrictEntity(BaseEntity):
            val: int = Field(default=0)

        entity = StrictEntity()
        with pytest.raises(KeyError, match="has no attribute 'unknown'"):
            entity.unknown = 42

    def test_wrong_type_raises(self):
        """Passing an incompatible value type raises ValueError."""

        class StrictTypeEntity(BaseEntity):
            val: int = Field()

        with pytest.raises(ValueError, match="has wrong type"):
            StrictTypeEntity(val=[])


# ---------------------------------------------------------------------------
# BaseEntity: type field validation
# ---------------------------------------------------------------------------
class TestBaseEntityTypeField:
    def test_type_field_wrong_value_raises(self):
        class TypedEntity(BaseEntity):
            type: str = Field(name="type", default="expected")

        with pytest.raises(ValueError, match="has wrong value"):
            TypedEntity(type="wrong")

    def test_type_field_correct_value(self):
        class TypedEntity2(BaseEntity):
            type: str = Field(name="type", default="correct")

        entity = TypedEntity2(type="correct")
        assert entity.type == "correct"


# ---------------------------------------------------------------------------
# BaseEntity: dict() serialization
# ---------------------------------------------------------------------------
class TestBaseEntityDict:
    def test_simple_dict(self):
        class DictEntity(BaseEntity):
            name: str = Field(default="default")
            count: Optional[int] = Field()

        entity = DictEntity(name="test", count=5)
        assert entity.dict() == {"name": "test", "count": 5}

    def test_dict_skips_none(self):
        class NoneEntity(BaseEntity):
            name: str = Field(default="x")
            opt: Optional[int] = Field()

        entity = NoneEntity(name="hello")
        assert entity.dict() == {"name": "hello"}

    def test_dict_with_nested_entity(self):
        class Inner(BaseEntity):
            x: int = Field(default=1)

        class Outer(BaseEntity):
            inner: Inner = Field()

        entity = Outer(inner=Inner(x=10))
        assert entity.dict() == {"inner": {"x": 10}}

    def test_dict_with_sequence(self):
        class SeqDictEntity(BaseEntity):
            items: Sequence[int] = Field()

        entity = SeqDictEntity(items=[1, 2, 3])
        assert entity.dict() == {"items": [1, 2, 3]}

    def test_dict_with_mapping(self):
        class MapDictEntity(BaseEntity):
            data: Mapping[str, Any] = Field()

        entity = MapDictEntity(data={"key": "value"})
        assert entity.dict() == {"data": {"key": "value"}}

    def test_dict_with_expr(self):
        class ExprEntity(BaseEntity):
            val: Union[str, Expr] = Field()

        entity = ExprEntity(val=Expr("@{some_var}"))
        assert entity.dict() == {"val": "@{some_var}"}

    def test_dict_with_enum(self):
        class Color(enum.Enum):
            RED = "red"
            GREEN = "green"

        class EnumEntity(BaseEntity):
            color: Color = Field(default=Color.RED)

        entity = EnumEntity(color=Color.GREEN)
        assert entity.dict() == {"color": "green"}

    def test_dict_with_nested_entity_in_sequence(self):
        class Item(BaseEntity):
            x: int = Field(default=1)

        class ListEntity(BaseEntity):
            items: Sequence[Item] = Field()

        entity = ListEntity(items=[Item(x=10)])
        assert entity.dict() == {"items": [{"x": 10}]}


# ---------------------------------------------------------------------------
# BaseEntity: template_name
# ---------------------------------------------------------------------------
class TestBaseEntityTemplateName:
    def test_default_template_name(self):
        class NameEntity(BaseEntity):
            val: int = Field(default=0)

        expected = f"{NameEntity.__module__}.{NameEntity.__name__}"
        assert NameEntity.template_name == expected

    def test_custom_template_name(self):
        class CustomNameEntity(BaseEntity):
            __template_name__ = "custom.template"
            val: int = Field(default=0)

        assert CustomNameEntity.template_name == "custom.template"


# ---------------------------------------------------------------------------
# BaseEntity: related_templates
# ---------------------------------------------------------------------------
class TestBaseEntityRelatedTemplates:
    def test_empty_for_plain_entity(self):
        class PlainEntity(BaseEntity):
            val: int = Field(default=0)

        entity = PlainEntity()
        assert entity.related_templates() == set()


# ---------------------------------------------------------------------------
# BaseEntity: union with injected types (dict -> entity cast)
# ---------------------------------------------------------------------------
class TestBaseEntityUnionInjectedTypes:
    def test_union_field_dict_cast_to_entity(self):
        """Dict with 'type' field is cast to correct entity in a Union."""

        class TypeAlpha(BaseEntity):
            type: str = Field(name="type", default="alpha")
            val: int = Field(default=0)

        class TypeBeta(BaseEntity):
            type: str = Field(name="type", default="beta")
            name: str = Field(default="x")

        class Container(BaseEntity):
            child: Union[TypeAlpha, TypeBeta] = Field()

        entity = Container(child={"type": "alpha", "val": 42})
        assert isinstance(entity.child, TypeAlpha)
        assert entity.child.val == 42

    def test_union_field_dict_without_type_raises(self):
        class TypeGamma(BaseEntity):
            type: str = Field(name="type", default="gamma")
            val: int = Field(default=0)

        class TypeDelta(BaseEntity):
            type: str = Field(name="type", default="delta")
            val: int = Field(default=0)

        class Container2(BaseEntity):
            child: Union[TypeGamma, TypeDelta] = Field()

        with pytest.raises(ValueError, match="does not have field type"):
            Container2(child={"val": 42})

    def test_union_field_dict_unknown_type_raises(self):
        class TypeEpsilon(BaseEntity):
            type: str = Field(name="type", default="epsilon")
            val: int = Field(default=0)

        class TypeZeta(BaseEntity):
            type: str = Field(name="type", default="zeta")
            val: int = Field(default=0)

        class Container3(BaseEntity):
            child: Union[TypeEpsilon, TypeZeta] = Field()

        with pytest.raises(ValueError, match="does not contain type"):
            Container3(child={"type": "unknown", "val": 1})


# ---------------------------------------------------------------------------
# BaseDiv: template building
# ---------------------------------------------------------------------------
class TestBaseDivTemplate:
    def test_direct_subclass_is_not_template(self):
        class RawDiv(BaseDiv):
            name: str = Field(default="x")

        with pytest.raises(TypeError, match="is not a template"):
            RawDiv.template()

    def test_template_with_tpl_values(self):
        class CompBase(BaseDiv):
            type: str = Field(name="type", default="component")
            text: str = Field()

        class MyComponent(CompBase):
            text = "default_text"

        tpl = MyComponent.template()
        assert tpl["type"] == "component"
        assert tpl["text"] == "default_text"

    def test_template_with_ref_field(self):
        class RefBase(BaseDiv):
            type: str = Field(name="type", default="ref_comp")
            label: str = Field()

        class RefTemplate(RefBase):
            custom_label: str = Field()
            label = Ref(custom_label)

        tpl = RefTemplate.template()
        assert tpl["type"] == "ref_comp"
        assert "$label" in tpl
        assert tpl["$label"] == "custom_label"

    def test_template_caching(self):
        class CacheBase(BaseDiv):
            type: str = Field(name="type", default="cache_test")
            val: int = Field()

        class CachedComponent(CacheBase):
            val = 42

        tpl1 = CachedComponent.template()
        tpl2 = CachedComponent.template()
        assert tpl1 is tpl2

    def test_template_none_tpl_value_not_overridden(self):
        """Instantiating with None doesn't override template values."""

        class TplBase(BaseDiv):
            type: str = Field(name="type", default="tpl_base")
            color: str = Field()

        class TplChild(TplBase):
            color = "red"

        instance = TplChild()
        assert instance.dict().get("type") is not None

    def test_multiple_bases_raises(self):
        with pytest.raises(TypeError, match="cannot be uniquely identified"):

            class MultiBaseDiv(BaseDiv, BaseEntity):
                pass


# ---------------------------------------------------------------------------
# BaseDiv: schema()
# ---------------------------------------------------------------------------
class TestBaseDivSchema:
    def test_schema_returns_object_with_definitions(self):
        class SchBase(BaseDiv):
            type: str = Field(name="type", default="schema_test")
            val: int = Field()

        class SchChild(SchBase):
            pass

        schema = SchChild.schema()
        assert schema["type"] == "object"
        assert "definitions" in schema
        assert isinstance(schema["definitions"], dict)

    def test_schema_tpl_values_excluded(self):
        class SchBase2(BaseDiv):
            type: str = Field(name="type", default="sch_tpl")
            color: str = Field()
            size: int = Field()

        class SchChild2(SchBase2):
            color = "red"

        schema = SchChild2.schema()
        props = schema.get("properties", {})
        assert "color" not in props
        assert "size" in props

    def test_schema_with_exclude_fields(self):
        class SchBase3(BaseDiv):
            type: str = Field(name="type", default="sch_excl")
            a: str = Field()
            b: str = Field()

        class SchChild3(SchBase3):
            pass

        schema = SchChild3.schema(exclude_fields=["a"])
        props = schema.get("properties", {})
        assert "a" not in props

    def test_schema_with_nested_exclude(self):
        class SchBase4(BaseDiv):
            type: str = Field(name="type", default="sch_nest")
            a: str = Field()
            b: str = Field()

        class SchChild4(SchBase4):
            pass

        schema = SchChild4.schema(exclude_fields=["a.nested"])
        assert "definitions" in schema

    def test_schema_optional_not_required(self):
        class SchBase5(BaseDiv):
            type: str = Field(name="type", default="sch_opt")
            required_field: int = Field()
            optional_field: Optional[str] = Field()

        class SchChild5(SchBase5):
            pass

        schema = SchChild5.schema()
        required = schema.get("required", [])
        assert "required_field" in required
        assert "optional_field" not in required

    def test_schema_int_field(self):
        class IntBase(BaseDiv):
            type: str = Field(name="type", default="int_sch")
            count: int = Field()

        class IntChild(IntBase):
            pass

        schema = IntChild.schema()
        props = schema.get("properties", {})
        assert props["count"]["type"] == "integer"

    def test_schema_str_field(self):
        class StrBase(BaseDiv):
            type: str = Field(name="type", default="str_sch")
            name: str = Field()

        class StrChild(StrBase):
            pass

        schema = StrChild.schema()
        props = schema.get("properties", {})
        assert props["name"]["type"] == "string"

    def test_schema_float_field(self):
        class FloatBase(BaseDiv):
            type: str = Field(name="type", default="float_sch")
            rate: float = Field()

        class FloatChild(FloatBase):
            pass

        schema = FloatChild.schema()
        props = schema.get("properties", {})
        assert props["rate"]["type"] == "number"

    def test_schema_bool_field(self):
        class BoolBase(BaseDiv):
            type: str = Field(name="type", default="bool_sch")
            flag: bool = Field()

        class BoolChild(BoolBase):
            pass

        schema = BoolChild.schema()
        props = schema.get("properties", {})
        assert props["flag"]["type"] == "integer"
        assert props["flag"]["format"] == "boolean"

    def test_schema_sequence_field(self):
        class SeqBase(BaseDiv):
            type: str = Field(name="type", default="seq_sch")
            items: Sequence[int] = Field()

        class SeqChild(SeqBase):
            pass

        schema = SeqChild.schema()
        props = schema.get("properties", {})
        assert props["items"]["type"] == "array"
        assert props["items"]["items"]["type"] == "integer"

    def test_schema_mapping_field(self):
        class MapBase(BaseDiv):
            type: str = Field(name="type", default="map_sch")
            data: Mapping[str, Any] = Field()

        class MapChild(MapBase):
            pass

        schema = MapChild.schema()
        props = schema.get("properties", {})
        assert props["data"]["type"] == "object"
        assert props["data"]["additionalProperties"] is True

    def test_schema_union_field(self):
        class UnionBase(BaseDiv):
            type: str = Field(name="type", default="union_sch")
            val: Union[int, str] = Field()

        class UnionChild(UnionBase):
            pass

        schema = UnionChild.schema()
        props = schema.get("properties", {})
        assert "anyOf" in props["val"]
        type_values = [a["type"] for a in props["val"]["anyOf"]]
        assert "integer" in type_values
        assert "string" in type_values

    def test_schema_enum_field(self):
        class Direction(enum.Enum):
            LEFT = "left"
            RIGHT = "right"

        class EnumBase(BaseDiv):
            type: str = Field(name="type", default="enum_sch")
            dir: Direction = Field()

        class EnumChild(EnumBase):
            pass

        schema = EnumChild.schema()
        props = schema.get("properties", {})
        assert props["dir"]["type"] == "string"
        assert set(props["dir"]["enum"]) == {"left", "right"}

    def test_schema_nested_entity_field(self):
        class Nested(BaseEntity):
            x: int = Field(default=0)

        class NestBase(BaseDiv):
            type: str = Field(name="type", default="nest_sch")
            child: Nested = Field()

        class NestChild(NestBase):
            pass

        schema = NestChild.schema()
        props = schema.get("properties", {})
        assert "$ref" in props["child"]
        assert "Nested" in props["child"]["$ref"]
        assert "Nested" in schema["definitions"]

    def test_schema_field_with_description_and_constraints(self):
        class DescBase(BaseDiv):
            type: str = Field(name="type", default="desc_sch")
            count: int = Field(
                description="Item count",
                ge=0,
                le=100,
            )

        class DescChild(DescBase):
            pass

        schema = DescChild.schema()
        props = schema.get("properties", {})
        count_schema = props["count"]
        assert count_schema.get("description") == "Item count"
        assert count_schema.get("minimum") == 0
        assert count_schema.get("maximum") == 100

    def test_schema_type_field(self):
        """The 'type' field with a default generates enum schema."""

        class TypeBase(BaseDiv):
            type: str = Field(name="type", default="type_sch")
            val: int = Field()

        class TypeChild(TypeBase):
            pass

        schema = TypeChild.schema()
        props = schema.get("properties", {})
        type_prop = props["type"]
        assert type_prop["type"] == "string"
        assert "enum" in type_prop


# ---------------------------------------------------------------------------
# Integration with generated DivKit classes
# ---------------------------------------------------------------------------
class TestIntegration:
    def test_make_div_with_template(self):
        from pydivkit import DivContainer, DivText, make_div

        class MyTemplate(DivContainer):
            label: str = Field()
            items = [DivText(text=Ref(label))]

        result = make_div(MyTemplate(items=[], label="hello"))
        assert "templates" in result
        assert "card" in result
        tpl_name = MyTemplate.template_name
        assert tpl_name in result["templates"]

    def test_schema_with_generated_classes(self):
        from pydivkit import DivContainer, DivText

        class TextTemplate(DivContainer):
            label: str = Field()
            items = [DivText(text=Ref(label))]

        schema = TextTemplate.schema()
        assert schema["type"] == "object"
        assert "definitions" in schema
