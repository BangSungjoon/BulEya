import redis

r = redis.Redis(
    host="localhost", 
    port=6379, 
    password="jangan204!!",
    decode_responses=True
)

r.set("test_station", "active")
print("✅ SET 완료")

value = r.get("test_station")
print("🎯 GET 결과:", value)
