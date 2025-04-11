import redis
import os

r = redis.Redis(
    host=os.getenv("REDIS_HOST", "redis"),
    port=int(os.getenv("REDIS_PORT", 6379)),
    password=os.getenv("REDIS_PASSWORD"),
    decode_responses=True
)

# def has_station(station_id: str) -> bool:
#     return r.exists(station_id)

# def add_station(station_id: str):
#     r.set(station_id, "active")

# def remove_station(station_id: str):
#     r.delete(station_id)

# beacon 단위 상태 관리 함수들
def set_beacon_fire_count(beacon_code: int, count: int):
    """
    beacon_code에 대한 화재 미감지 횟수를 설정
    ex) 감지 실패 시 count += 1, 감지 성공 시 count = 0
    """
    r.set(f"fire:beacon:{beacon_code}", count)

def get_beacon_fire_count(beacon_code: int) -> int:
    """
    beacon_code에 대해 Redis에 저장된 화재 미감지 횟수를 반환
    없으면 0으로 간주
    """
    value = r.get(f"fire:beacon:{beacon_code}")
    return int(value) if value is not None else 0

def delete_beacon(beacon_code: int):
    """
    beacon_code의 화재 추적 상태를 삭제 (3회 이상 미감지된 경우 사용)
    """
    r.delete(f"fire:beacon:{beacon_code}")

def has_beacon(beacon_code: int) -> bool:
    """
    beacon_code에 대한 감시 상태가 Redis에 존재하는지 여부 반환
    """
    return r.exists(f"fire:beacon:{beacon_code}")

# station 단위 상태 관리 함수들
def set_station_fire_count(station_id: str, count: int):
    """
    station_id에 대해 전체적으로 화재가 감지되지 않은 횟수를 저장
    모든 구역에서 화재가 없을 경우에만 증가됨
    """
    r.set(f"fire:station:{station_id}", count)

def get_station_fire_count(station_id: str) -> int:
    """
    station_id에 대해 저장된 전체 화재 미감지 횟수를 반환
    없으면 0으로 간주
    """
    value = r.get(f"fire:station:{station_id}")
    return int(value) if value is not None else 0

def delete_station(station_id: str):
    """
    station_id의 전체 화재 상태를 삭제 (종료 판정 후)
    """
    r.delete(f"fire:station:{station_id}")

def has_station(station_id: str) -> bool:
    """
    station_id의 화재 추적 상태가 Redis에 존재하는지 여부 반환
    """
    return r.exists(f"fire:station:{station_id}")


# Redis에 존재하는 beacon 목록 조회
def get_all_fire_beacons() -> list[int]:
    """
    Redis에 저장된 모든 fire:beacon:{beacon_code} 키를 검색하여
    beacon_code 목록 (int 리스트)로 반환
    """
    keys = r.keys("fire:beacon:*")
    return [int(key.split(":")[-1]) for key in keys]