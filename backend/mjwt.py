import jwt
import datetime
from config import query_yaml

def generate_jwt(payload, expiry=None):
    """
    :param payload: dict 载荷
    :param expiry: datetime 有效期
    :return: 生成jwt
    """
    if expiry == None:
        now = datetime.datetime.now()
        expire_hours = query_yaml('app.EXPIREHOURS')
        expiry = now + datetime.timedelta(hours=expire_hours)

    _payload = {'exp': expiry}
    _payload.update(payload)

    secret = query_yaml('app.SECRET')

    token = jwt.encode(_payload, secret, algorithm='HS256')
    return token.decode()

def decode_jwt(authorization):
    secret = query_yaml('app.SECRET')
    try:
        payload = jwt.decode(authorization, secret, algorithms='HS256')
        return payload, True
    except Exception as e:
        return e, False