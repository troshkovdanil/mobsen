import requests
import time


while True:
    time.sleep(1)
    url = 'https://ptsv3.com/t/gps_dt/d/0'
    r = requests.get(url)
    text = str(r.text)
    if text == "Failed getting Dump.":
        time.sleep(1)
    else:
        print(text[:-1])
        url = url + '/flush'
        requests.get(url)
