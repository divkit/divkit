from pydantic import BaseSettings


class DBSettings(BaseSettings):
    """Database settings."""

    user: str
    password: str
    db: str
    host: str
    port: str

    @property
    def db_url(self) -> str:
        """Get database URL."""
        return (
            f"postgresql+asyncpg://{self.user}:"
            f"{self.password}@{self.host}:{self.port}/{self.db}"
        )

    class Config:
        env_prefix = "POSTGRES_"
        env_file = ".env"


def get_db_settings() -> DBSettings:
    """Return an instance DBSettings."""
    return DBSettings()
