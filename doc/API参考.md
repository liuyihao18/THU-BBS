root path = "http://47.93.89.166"

@bp_user.route('/user-api/v1/user/register', methods=['POST'])

params:

```
{
	"email": str,
	"password": str
}
```

ret:

```
{
	"errCode": 0代表成功;1代表参数格式不正确;2代表用户创建失败
}
```



@bp_user.route('/user-api/v1/user/login', methods=['POST'])

params:

```
{
	"email": str,
	"password": str
}
```

ret:

```
成功情形：
{
	"errCode": 0,
	"jwt": jwt
}
失败情形：
{
	"errCode": 1,
	"errMsg": errmsg
}
```

