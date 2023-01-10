from sqlalchemy import Column, Index
from sqlalchemy.dialects.postgresql import TEXT, UUID

from app.db.models.base import Base


class Tags(Base):
    __tablename__ = "tags"

    id = Column(UUID, primary_key=True)
    tag = Column(TEXT, nullable=False, unique=True)
    __table_args__ = (
        Index(
            "ix_gin_tags_tag",
            tag,
            postgresql_using="gin",
            postgresql_ops={
                "tag": "gin_trgm_ops",
            },
        ),
    )
