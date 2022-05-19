import datetime

def now():
    return datetime.datetime.now()

def unique_str():
    return str(int(now().timestamp()))