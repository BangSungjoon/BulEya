import requests
from app.core.redis import has_station, add_station, remove_station

SPRING_ENDPOINT = "http://spring-server/api/fire-report"

async def report_fire(station_id, fire_images: dict, fire_beacons: list):
    files = {}
    beacon_list = []

    for idx, (beacon_code, image_data) in enumerate(fire_images.items()):
        files[f"image_{idx}"] = (f"{beacon_code}.jpg", image_data, "image/jpeg")
        beacon_list.append({
            "beacon_code": beacon_code,
            "is_active_fire": 1
        })

    data = {
        "station_id": station_id,
        "beacon_list": beacon_list
    }

    response = requests.post(
        SPRING_ENDPOINT,
        data={"json": json.dumps(data)},
        files=files
    )

    # redis 처리
    if not has_station(station_id):
        add_station(station_id)
    else:
        remove_station(station_id)
        # 정상 상태 보고
        normal_data = {
            "station_id": station_id,
            "beacon_list": [{"beacon_code": b, "is_active_fire": 0} for b in fire_beacons]
        }
        requests.post(
            SPRING_ENDPOINT,
            data={"json": json.dumps(normal_data)}
        )
