from flask import request, g
from mjwt import decode_jwt

def verify_identity():
    g.userid = None
    try:
        authorization = request.headers.get('Authorization')
    except Exception:
        return
    payload, verified = decode_jwt(authorization)
    if verified:
        g.userid = payload['userid']