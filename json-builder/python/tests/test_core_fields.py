import pytest

from pydivkit.core import BaseEntity, Expr, Field, Ref


# ---------------------------------------------------------------------------
# Expr (public class)
# ---------------------------------------------------------------------------
class TestExpr:
    def test_valid_expr(self):
        e = Expr("@{a + b}")
        assert str(e) == "@{a + b}"

    def test_repr(self):
        e = Expr("@{some_var}")
        assert repr(e) == "Expr(@{some_var})"

    def test_str(self):
        e = Expr("@{some_var}")
        assert str(e) == "@{some_var}"

    def test_invalid_expr_no_closing(self):
        with pytest.raises(ValueError):
            Expr("@{unclosed")

    def test_invalid_expr_no_at(self):
        with pytest.raises(ValueError):
            Expr("{no_at}")

    def test_invalid_expr_no_markers(self):
        with pytest.raises(ValueError):
            Expr("notcontains")


# ---------------------------------------------------------------------------
# Field (public factory function)
# ---------------------------------------------------------------------------
class TestField:
    def test_field_creates_entity_field(self):
        """Field() can be used as a class annotation to define entity fields."""

        class FieldEntity(BaseEntity):
            name: str = Field()

        entity = FieldEntity(name="test")
        assert entity.dict() == {"name": "test"}

    def test_field_with_default(self):
        class DefaultEntity(BaseEntity):
            color: str = Field(default="red")

        entity = DefaultEntity()
        assert entity.dict() == {"color": "red"}

    def test_field_with_name_override(self):
        """Field(name=...) overrides the serialized field name."""

        class NamedEntity(BaseEntity):
            my_field: str = Field(name="custom_name")

        entity = NamedEntity(my_field="value")
        assert entity.dict() == {"custom_name": "value"}

    def test_field_constraints_reflected_in_schema(self):
        """Field constraints appear in schema output."""
        from pydivkit.core import BaseDiv

        class ConstraintBase(BaseDiv):
            type: str = Field(name="type", default="constraint_test")
            count: int = Field(ge=0, le=100)

        class ConstraintChild(ConstraintBase):
            pass

        schema = ConstraintChild.schema()
        props = schema.get("properties", {})
        assert "count" in props
        count_schema = props["count"]
        assert count_schema.get("minimum") == 0
        assert count_schema.get("maximum") == 100


# ---------------------------------------------------------------------------
# Ref (public factory function)
# ---------------------------------------------------------------------------
class TestRef:
    def test_ref_non_field_raises(self):
        with pytest.raises(ValueError, match="Expected type is Field"):
            Ref("not_a_field")

    def test_ref_int_raises(self):
        with pytest.raises(ValueError, match="Expected type is Field"):
            Ref(42)

    def test_ref_creates_template_reference(self):
        """Ref() creates a template reference observable in make_div."""
        from pydivkit import DivContainer, DivText, make_div

        class RefTemplate(DivContainer):
            label: str = Field()
            items = [DivText(text=Ref(label))]

        result = make_div(RefTemplate(items=[], label="hello"))
        tpl_name = RefTemplate.template_name
        templates = result["templates"]
        assert tpl_name in templates
        # The $text ref is inside the nested DivText item
        tpl_items = templates[tpl_name]["items"]
        text_item = tpl_items[0]
        assert "$text" in text_item
        assert text_item["$text"] == "label"
