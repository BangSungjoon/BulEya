# 입력 및 출력 Pydantic 스키마
from pydantic import BaseModel
from typing import List

class CCTVItem(BaseModel):
    beacon_code: str
    rtsp_url: str

class CCTVRequest(BaseModel):
    station_id: str
    cctv_list: List[CCTVItem]