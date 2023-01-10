from sqlalchemy import Column, Index
from sqlalchemy.dialects.postgresql import JSONB, TIMESTAMP, UUID, VARCHAR
from sqlalchemy.sql import func

from app.db.models.base import Base


class Templates(Base):
    __tablename__ = "templates"

    id = Column(UUID, primary_key=True)
    template = Column(JSONB, nullable=False)
    description = Column(VARCHAR(300), nullable=True)
    title = Column(VARCHAR(30), nullable=False)
    dt_updated = Column(
        TIMESTAMP(timezone=True),
        server_default=func.now(),
        nullable=False,
    )
    preview_filename = Column(VARCHAR(100), nullable=True)
    __table_args__ = (
        Index(
            "ix_gin_templates_title",
            func.lower(title),
            postgresql_using="gin",
            postgresql_ops={
                "lower(title)": "gin_trgm_ops",
            },
        ),
    )
