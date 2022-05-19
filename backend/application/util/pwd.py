import jwt
import datetime
from config import query_yaml
import scrypt
import base64

def encrypt_password(password):
    salt = query_yaml('app.SALT')
    key = scrypt.hash(password, salt, 32768, 8, 1, 32)
    return base64.b64encode(key).decode("ascii")