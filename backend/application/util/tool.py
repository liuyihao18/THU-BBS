def convert_bool(raw):
    if raw == '0' or raw == 'false' or raw == 'False':
        return False
    return True