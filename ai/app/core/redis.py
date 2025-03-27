import redis
import os

r = redis.Redis(
    host=os.getenv("REDIS_HOST", "redis"),
    port=int(os.getenv("REDIS_PORT", 6379)),
    password=os.getenv("REDIS_PASSWORD"),
    decode_responses=True
)

def has_station(station_id: str) -> bool:
    return r.exists(station_id)

def add_station(station_id: str):
    r.set(station_id, "active")

def remove_station(station_id: str):
    r.delete(station_id)