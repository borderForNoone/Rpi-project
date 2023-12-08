import json
import random

DHT_PIN = 4

humidity = random.uniform(20, 30)
temperature = random.uniform(20, 30)

if humidity is not None and temperature is not None:
    data = {"temperature": temperature, "humidity": humidity}
    print(json.dumps(data))
else:
    print(json.dumps({"temperature": None, "humidity": None}))
